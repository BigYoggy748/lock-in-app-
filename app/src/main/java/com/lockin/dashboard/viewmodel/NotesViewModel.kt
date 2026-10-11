package com.lockin.dashboard.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lockin.dashboard.data.db.LockInDatabase
import com.lockin.dashboard.data.model.*
import com.lockin.dashboard.data.repository.LockInRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val db = LockInDatabase.getDatabase(application)
    private val repository = LockInRepository(db.dayLogDao(), db.taskDao(), db.focusSessionDao(), db.noteDao())

    // ── Categories ────────────────────────────────────────────────────────────
    val availableCategories = listOf("All", "General", "Work", "Personal", "Ideas", "Checklists", "Study")

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // ── Notes List ────────────────────────────────────────────────────────────
    val notes: StateFlow<List<Note>> = combine(
        _selectedCategory,
        _searchQuery
    ) { category, query ->
        Pair(category, query)
    }.flatMapLatest { (category, query) ->
        when {
            query.isNotBlank() -> repository.searchNotes(query)
            category != "All"  -> repository.getNotesByCategory(category)
            else               -> repository.getAllNotes()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Editor State ──────────────────────────────────────────────────────────
    private val _activeNote = MutableStateFlow<Note?>(null)
    val activeNote: StateFlow<Note?> = _activeNote.asStateFlow()

    private val _activeTitle = MutableStateFlow("")
    val activeTitle: StateFlow<String> = _activeTitle.asStateFlow()

    private val _activeCategory = MutableStateFlow("General")
    val activeCategory: StateFlow<String> = _activeCategory.asStateFlow()

    private val _activeColor = MutableStateFlow("#1C2128")
    val activeColor: StateFlow<String> = _activeColor.asStateFlow()

    private val _isPinned = MutableStateFlow(false)
    val isPinned: StateFlow<Boolean> = _isPinned.asStateFlow()

    private val _activeBlocks = MutableStateFlow<List<NoteBlock>>(emptyList())
    val activeBlocks: StateFlow<List<NoteBlock>> = _activeBlocks.asStateFlow()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    private val _isDirty = MutableStateFlow(false)
    val isDirty: StateFlow<Boolean> = _isDirty.asStateFlow()

    // ── Filter / Search Actions ───────────────────────────────────────────────
    fun selectCategory(category: String) { _selectedCategory.value = category }
    fun setSearchQuery(query: String) { _searchQuery.value = query }

    // ── Note CRUD Actions ─────────────────────────────────────────────────────
    fun createNewNote(initialCategory: String = "General") {
        val newId = UUID.randomUUID().toString()
        val defaultBlock = NoteBlock.Paragraph(text = "")
        val initialBlocks = listOf(defaultBlock)
        val initialCategoryToUse = if (initialCategory == "All") "General" else initialCategory

        val note = Note(
            id = newId,
            title = "",
            blocksJson = NoteBlock.serializeBlocks(initialBlocks),
            category = initialCategoryToUse,
            colorHex = "#1C2128",
            isPinned = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        _activeNote.value = note
        _activeTitle.value = ""
        _activeCategory.value = initialCategoryToUse
        _activeColor.value = "#1C2128"
        _isPinned.value = false
        _activeBlocks.value = initialBlocks
        _isDirty.value = true
        _saveState.value = SaveState.Idle
    }

    fun openNote(note: Note) {
        _activeNote.value = note
        _activeTitle.value = note.title
        _activeCategory.value = note.category
        _activeColor.value = note.colorHex
        _isPinned.value = note.isPinned
        val blocks = NoteBlock.deserializeBlocks(note.blocksJson)
        _activeBlocks.value = if (blocks.isEmpty()) listOf(NoteBlock.Paragraph(text = "")) else blocks
        _isDirty.value = false
        _saveState.value = SaveState.Idle
    }

    fun closeNote() {
        _activeNote.value = null
        _activeBlocks.value = emptyList()
        _activeTitle.value = ""
        _isDirty.value = false
        _saveState.value = SaveState.Idle
    }

    fun saveCurrentNote() {
        val current = _activeNote.value ?: return
        _saveState.value = SaveState.Saving
        viewModelScope.launch {
            val updated = current.copy(
                title = _activeTitle.value.trim(),
                blocksJson = NoteBlock.serializeBlocks(_activeBlocks.value),
                category = _activeCategory.value,
                colorHex = _activeColor.value,
                isPinned = _isPinned.value,
                updatedAt = System.currentTimeMillis()
            )
            repository.saveNote(updated)
            _activeNote.value = updated
            _isDirty.value = false
            _saveState.value = SaveState.Saved
        }
    }

    fun deleteCurrentNote() {
        val currentId = _activeNote.value?.id ?: return
        viewModelScope.launch {
            repository.deleteNote(currentId)
            _activeNote.value = null
            _activeBlocks.value = emptyList()
            _isDirty.value = false
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            repository.deleteNote(noteId)
            if (_activeNote.value?.id == noteId) {
                _activeNote.value = null
                _activeBlocks.value = emptyList()
            }
        }
    }

    fun togglePinNote(noteId: String, currentPin: Boolean) {
        viewModelScope.launch {
            repository.togglePinNote(noteId, !currentPin)
        }
    }

    // ── Editor Field Updates ──────────────────────────────────────────────────
    fun updateTitle(newTitle: String) {
        _activeTitle.value = newTitle
        _isDirty.value = true
        _saveState.value = SaveState.Idle
    }

    fun updateCategory(newCategory: String) {
        _activeCategory.value = newCategory
        _isDirty.value = true
        _saveState.value = SaveState.Idle
    }

    fun updateColor(colorHex: String) {
        _activeColor.value = colorHex
        _isDirty.value = true
        _saveState.value = SaveState.Idle
    }

    fun toggleCurrentPin() {
        _isPinned.value = !_isPinned.value
        _isDirty.value = true
        _saveState.value = SaveState.Idle
    }

    // ── Block Manipulation ────────────────────────────────────────────────────
    fun updateBlock(updatedBlock: NoteBlock) {
        _activeBlocks.value = _activeBlocks.value.map {
            if (it.id == updatedBlock.id) updatedBlock else it
        }
        _isDirty.value = true
        _saveState.value = SaveState.Idle
    }

    fun addBlock(type: BlockType, afterBlockId: String? = null) {
        val current = _activeBlocks.value.toMutableList()
        val newBlock: NoteBlock = when (type) {
            BlockType.PARAGRAPH -> NoteBlock.Paragraph()
            BlockType.HEADING_1 -> NoteBlock.Heading(level = 1)
            BlockType.HEADING_2 -> NoteBlock.Heading(level = 2)
            BlockType.HEADING_3 -> NoteBlock.Heading(level = 3)
            BlockType.TODO -> NoteBlock.Todo()
            BlockType.TABLE -> NoteBlock.Table()
            BlockType.BULLET -> NoteBlock.Bullet()
            BlockType.NUMBERED -> NoteBlock.Numbered(number = (current.count { it is NoteBlock.Numbered } + 1))
            BlockType.QUOTE -> NoteBlock.Quote()
            BlockType.DIVIDER -> NoteBlock.Divider()
        }

        if (afterBlockId != null) {
            val idx = current.indexOfFirst { it.id == afterBlockId }
            if (idx != -1) {
                current.add(idx + 1, newBlock)
            } else {
                current.add(newBlock)
            }
        } else {
            current.add(newBlock)
        }
        _activeBlocks.value = current
        _isDirty.value = true
        _saveState.value = SaveState.Idle
    }

    fun removeBlock(blockId: String) {
        val current = _activeBlocks.value.toMutableList()
        if (current.size <= 1) {
            _activeBlocks.value = listOf(NoteBlock.Paragraph())
        } else {
            current.removeAll { it.id == blockId }
            _activeBlocks.value = current
        }
        _isDirty.value = true
        _saveState.value = SaveState.Idle
    }

    fun toggleTodoBlock(blockId: String) {
        _activeBlocks.value = _activeBlocks.value.map { block ->
            if (block is NoteBlock.Todo && block.id == blockId) {
                block.copy(isChecked = !block.isChecked)
            } else {
                block
            }
        }
        _isDirty.value = true
        _saveState.value = SaveState.Idle
    }

    // ── Table Matrix Operations ───────────────────────────────────────────────
    fun tableToggleRowDone(blockId: String, rowIndex: Int) {
        _activeBlocks.value = _activeBlocks.value.map { block ->
            if (block is NoteBlock.Table && block.id == blockId) {
                val newChecked = block.rowChecked.toMutableList()
                while (newChecked.size <= rowIndex) {
                    newChecked.add(false)
                }
                newChecked[rowIndex] = !newChecked[rowIndex]
                block.copy(rowChecked = newChecked)
            } else {
                block
            }
        }
        _isDirty.value = true
        _saveState.value = SaveState.Idle
    }

    fun tableAddRow(blockId: String) {
        _activeBlocks.value = _activeBlocks.value.map { block ->
            if (block is NoteBlock.Table && block.id == blockId) {
                val newRows = block.rows.toMutableList()
                newRows.add(List(block.headers.size) { "" })
                val newChecked = block.rowChecked + false
                block.copy(rows = newRows, rowChecked = newChecked)
            } else {
                block
            }
        }
        _isDirty.value = true
        _saveState.value = SaveState.Idle
    }

    fun tableRemoveRow(blockId: String, rowIndex: Int) {
        _activeBlocks.value = _activeBlocks.value.map { block ->
            if (block is NoteBlock.Table && block.id == blockId) {
                if (block.rows.size > 1) {
                    val newRows = block.rows.toMutableList().apply { removeAt(rowIndex) }
                    val newChecked = block.rowChecked.toMutableList().apply {
                        if (rowIndex < size) removeAt(rowIndex)
                    }
                    block.copy(rows = newRows, rowChecked = newChecked)
                } else block
            } else {
                block
            }
        }
        _isDirty.value = true
        _saveState.value = SaveState.Idle
    }

    fun tableAddColumn(blockId: String, headerName: String? = null) {
        _activeBlocks.value = _activeBlocks.value.map { block ->
            if (block is NoteBlock.Table && block.id == blockId) {
                val colNum = block.headers.size + 1
                val newHeaders = block.headers + (headerName ?: "Col $colNum")
                val newRows = block.rows.map { row -> row + "" }
                block.copy(headers = newHeaders, rows = newRows)
            } else {
                block
            }
        }
        _isDirty.value = true
        _saveState.value = SaveState.Idle
    }

    fun tableRemoveColumn(blockId: String, colIndex: Int) {
        _activeBlocks.value = _activeBlocks.value.map { block ->
            if (block is NoteBlock.Table && block.id == blockId) {
                if (block.headers.size > 1) {
                    val newHeaders = block.headers.toMutableList().apply { removeAt(colIndex) }
                    val newRows = block.rows.map { row ->
                        row.toMutableList().apply { if (colIndex < size) removeAt(colIndex) }
                    }
                    block.copy(headers = newHeaders, rows = newRows)
                } else block
            } else {
                block
            }
        }
        _isDirty.value = true
        _saveState.value = SaveState.Idle
    }

    fun tableUpdateCell(blockId: String, rowIndex: Int, colIndex: Int, value: String) {
        _activeBlocks.value = _activeBlocks.value.map { block ->
            if (block is NoteBlock.Table && block.id == blockId) {
                val newRows = block.rows.mapIndexed { r, row ->
                    if (r == rowIndex) {
                        row.mapIndexed { c, cell -> if (c == colIndex) value else cell }
                    } else row
                }
                block.copy(rows = newRows)
            } else {
                block
            }
        }
        _isDirty.value = true
        _saveState.value = SaveState.Idle
    }

    fun tableUpdateHeader(blockId: String, colIndex: Int, value: String) {
        _activeBlocks.value = _activeBlocks.value.map { block ->
            if (block is NoteBlock.Table && block.id == blockId) {
                val newHeaders = block.headers.mapIndexed { c, header ->
                    if (c == colIndex) value else header
                }
                block.copy(headers = newHeaders)
            } else {
                block
            }
        }
        _isDirty.value = true
        _saveState.value = SaveState.Idle
    }
}
