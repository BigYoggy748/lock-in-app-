package com.lockin.dashboard.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lockin.dashboard.data.model.Note
import com.lockin.dashboard.data.model.NoteBlock
import com.lockin.dashboard.ui.components.NoteEditorScreen
import com.lockin.dashboard.ui.theme.LockIn
import com.lockin.dashboard.viewmodel.NotesViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun NotesScreen(vm: NotesViewModel) {
    val activeNote by vm.activeNote.collectAsStateWithLifecycle()
    val notes by vm.notes.collectAsStateWithLifecycle()
    val selectedCategory by vm.selectedCategory.collectAsStateWithLifecycle()
    val searchQuery by vm.searchQuery.collectAsStateWithLifecycle()

    // If a note is active in editor mode, display the full editor
    if (activeNote != null) {
        NoteEditorScreen(
            vm = vm,
            onBack = { vm.closeNote() }
        )
        return
    }

    // Otherwise show the Notion-style Upfront Notes Dashboard
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LockIn.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .padding(top = 56.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Notes", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = LockIn.TextPrimary)
                    Text(
                        "${notes.size} note${if (notes.size == 1) "" else "s"}",
                        fontSize = 13.sp,
                        color = LockIn.TextSecondary
                    )
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { vm.setSearchQuery(it) },
                placeholder = { Text("Search notes, checklists, tables...", color = LockIn.TextSecondary, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = LockIn.TextSecondary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { vm.setSearchQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = LockIn.TextSecondary)
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LockIn.White,
                    unfocusedBorderColor = LockIn.Border,
                    cursorColor = LockIn.White,
                    focusedContainerColor = LockIn.Card,
                    unfocusedContainerColor = LockIn.Card
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Category Filter Pills (Samsung Notes style tabs)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                vm.availableCategories.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) LockIn.White else LockIn.Card)
                            .clickable { vm.selectCategory(cat) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) LockIn.Background else LockIn.TextSecondary
                        )
                    }
                }
            }

            // Notes Grid
            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📝", fontSize = 40.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (searchQuery.isNotEmpty()) "No matching notes found" else "No notes in this category",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = LockIn.TextPrimary
                        )
                        Text(
                            "Tap + to write down ideas, todos, or tables",
                            fontSize = 13.sp,
                            color = LockIn.TextSecondary.copy(alpha = 0.6f)
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(notes, key = { it.id }) { note ->
                        NoteCardItem(
                            note = note,
                            onClick = { vm.openNote(note) },
                            onTogglePin = { vm.togglePinNote(note.id, note.isPinned) },
                            onDelete = { vm.deleteNote(note.id) }
                        )
                    }
                }
            }
        }

        // Floating Action Button to create note
        FloatingActionButton(
            onClick = { vm.createNewNote(selectedCategory) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 80.dp),
            containerColor = LockIn.White,
            contentColor = LockIn.Background,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "New note")
        }
    }
}

@Composable
private fun NoteCardItem(
    note: Note,
    onClick: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val blocks = remember(note.blocksJson) {
        NoteBlock.deserializeBlocks(note.blocksJson)
    }

    val previewText = remember(blocks) {
        val snippet = blocks.mapNotNull { block ->
            when (block) {
                is NoteBlock.Paragraph -> block.text.takeIf { it.isNotBlank() }
                is NoteBlock.Heading -> block.text.takeIf { it.isNotBlank() }
                is NoteBlock.Todo -> (if (block.isChecked) "☑ " else "☐ ") + block.text
                is NoteBlock.Quote -> "❝ " + block.text
                is NoteBlock.Bullet -> "• " + block.text
                is NoteBlock.Numbered -> "${block.number}. " + block.text
                is NoteBlock.Table -> "📊 Table (${block.rows.size} rows)"
                is NoteBlock.Divider -> null
            }
        }.joinToString("\n")
        snippet.ifBlank { "Empty note" }
    }

    val todoSummary = remember(blocks) {
        val todos = blocks.filterIsInstance<NoteBlock.Todo>()
        if (todos.isNotEmpty()) {
            val done = todos.count { it.isChecked }
            "${done}/${todos.size} tasks"
        } else null
    }

    val formattedDate = remember(note.updatedAt) {
        try {
            val date = Instant.ofEpochMilli(note.updatedAt).atZone(ZoneId.systemDefault()).toLocalDate()
            val today = LocalDate.now()
            when {
                date == today -> "Today"
                date == today.minusDays(1) -> "Yesterday"
                else -> date.format(DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH))
            }
        } catch (e: Exception) {
            ""
        }
    }

    val cardColor = remember(note.colorHex) {
        try {
            Color(android.graphics.Color.parseColor(note.colorHex))
        } catch (e: Exception) {
            LockIn.Card
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardColor)
            .border(
                1.dp,
                if (note.isPinned) LockIn.White.copy(alpha = 0.5f) else LockIn.Border,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top row: Category tag & Options menu
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(LockIn.Surface)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = note.category,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LockIn.TextSecondary
                    )
                }

                if (note.isPinned) {
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        Icons.Default.PushPin,
                        contentDescription = "Pinned",
                        tint = LockIn.White,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Box {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = LockIn.TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(LockIn.Surface)
                ) {
                    DropdownMenuItem(
                        text = { Text(if (note.isPinned) "Unpin" else "Pin to top", color = LockIn.TextPrimary) },
                        leadingIcon = { Icon(Icons.Default.PushPin, null, tint = LockIn.TextSecondary) },
                        onClick = {
                            showMenu = false
                            onTogglePin()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = LockIn.PriorityHigh) },
                        leadingIcon = { Icon(Icons.Default.DeleteOutline, null, tint = LockIn.PriorityHigh) },
                        onClick = {
                            showMenu = false
                            showDeleteConfirm = true
                        }
                    )
                }
            }
        }

        // Title
        Text(
            text = if (note.title.isNotBlank()) note.title else "Untitled",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = if (note.title.isNotBlank()) LockIn.TextPrimary else LockIn.TextSecondary.copy(alpha = 0.5f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Snippet preview
        Text(
            text = previewText,
            fontSize = 12.sp,
            color = LockIn.TextSecondary,
            lineHeight = 16.sp,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(Modifier.height(4.dp))

        // Bottom row: To-do summary or Table indicator & Updated date
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (todoSummary != null) {
                Text(
                    text = todoSummary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = LockIn.PriorityMed
                )
            } else {
                Spacer(Modifier.width(1.dp))
            }
            Text(
                text = formattedDate,
                fontSize = 10.sp,
                color = LockIn.TextSecondary.copy(alpha = 0.7f)
            )
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Note", color = LockIn.TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete this note?", color = LockIn.TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LockIn.PriorityHigh)
                ) {
                    Text("Delete", color = LockIn.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = LockIn.TextSecondary)
                }
            },
            containerColor = LockIn.Surface
        )
    }
}

