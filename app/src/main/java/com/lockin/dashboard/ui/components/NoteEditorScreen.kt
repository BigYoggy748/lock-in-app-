package com.lockin.dashboard.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lockin.dashboard.data.model.BlockType
import com.lockin.dashboard.data.model.NoteBlock
import com.lockin.dashboard.ui.theme.LockIn
import com.lockin.dashboard.viewmodel.NotesViewModel
import com.lockin.dashboard.viewmodel.SaveState

private val NOTE_COLORS = listOf(
    "#1C2128", // Default Dark
    "#2D1C1C", // Subtle Red
    "#1C2D24", // Subtle Green
    "#1C242D", // Subtle Blue
    "#281C2D", // Subtle Purple
    "#2D261C"  // Subtle Amber
)

@Composable
fun NoteEditorScreen(
    vm: NotesViewModel,
    onBack: () -> Unit
) {
    val title by vm.activeTitle.collectAsStateWithLifecycle()
    val category by vm.activeCategory.collectAsStateWithLifecycle()
    val colorHex by vm.activeColor.collectAsStateWithLifecycle()
    val isPinned by vm.isPinned.collectAsStateWithLifecycle()
    val blocks by vm.activeBlocks.collectAsStateWithLifecycle()
    val saveState by vm.saveState.collectAsStateWithLifecycle()
    val isDirty by vm.isDirty.collectAsStateWithLifecycle()

    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showUnsavedConfirm by remember { mutableStateOf(false) }
    var showColorMenu by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()

    val handleBack = {
        if (isDirty) {
            showUnsavedConfirm = true
        } else {
            vm.closeNote()
            onBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LockIn.Background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp) // Space for docked bottom toolbar
        ) {
            // ── Top Navigation Bar ──────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = handleBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = LockIn.TextPrimary)
                    }
                    Spacer(Modifier.width(4.dp))
                    // Dirty / Save indicator
                    Text(
                        text = when {
                            saveState is SaveState.Saving -> "Saving..."
                            saveState is SaveState.Saved -> "Saved ✓"
                            isDirty -> "Unsaved *"
                            else -> ""
                        },
                        fontSize = 12.sp,
                        fontWeight = if (isDirty) FontWeight.Bold else FontWeight.Normal,
                        color = if (isDirty) LockIn.PriorityMed else LockIn.TextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Category selector chip
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(LockIn.Card)
                                .clickable { showCategoryMenu = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(category, fontSize = 12.sp, color = LockIn.TextPrimary, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = LockIn.TextSecondary, modifier = Modifier.size(16.dp))
                        }
                        DropdownMenu(
                            expanded = showCategoryMenu,
                            onDismissRequest = { showCategoryMenu = false },
                            modifier = Modifier.background(LockIn.Surface)
                        ) {
                            vm.availableCategories.filter { it != "All" }.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat, color = LockIn.TextPrimary) },
                                    onClick = {
                                        vm.updateCategory(cat)
                                        showCategoryMenu = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.width(6.dp))

                    // Color picker
                    Box {
                        IconButton(onClick = { showColorMenu = true }, modifier = Modifier.size(36.dp)) {
                            Icon(Icons.Default.Palette, contentDescription = "Color", tint = LockIn.TextSecondary, modifier = Modifier.size(20.dp))
                        }
                        DropdownMenu(
                            expanded = showColorMenu,
                            onDismissRequest = { showColorMenu = false },
                            modifier = Modifier.background(LockIn.Surface)
                        ) {
                            Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                NOTE_COLORS.forEach { hex ->
                                    val c = try { Color(android.graphics.Color.parseColor(hex)) } catch (e: Exception) { LockIn.Card }
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(c)
                                            .border(
                                                width = if (hex == colorHex) 2.dp else 1.dp,
                                                color = if (hex == colorHex) LockIn.White else LockIn.Border,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                vm.updateColor(hex)
                                                showColorMenu = false
                                            }
                                    )
                                }
                            }
                        }
                    }

                    // Pin toggle
                    IconButton(onClick = { vm.toggleCurrentPin() }, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.PushPin,
                            contentDescription = "Pin",
                            tint = if (isPinned) LockIn.White else LockIn.TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Delete
                    IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = LockIn.PriorityHigh.copy(alpha = 0.8f), modifier = Modifier.size(20.dp))
                    }

                    Spacer(Modifier.width(8.dp))

                    // PROMINENT EXPLICIT SAVE BUTTON
                    Button(
                        onClick = { vm.saveCurrentNote() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDirty) LockIn.White else LockIn.Card,
                            contentColor = if (isDirty) LockIn.Background else LockIn.TextSecondary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Save",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isDirty) "Save" else "Saved",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // ── Scrollable Blocks Content Canvas ────────────────────────────
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Notion-style Large Title
                item(key = "note_title") {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        if (title.isEmpty()) {
                            Text(
                                text = "Untitled Note",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = LockIn.TextSecondary.copy(alpha = 0.4f)
                            )
                        }
                        BasicTextField(
                            value = title,
                            onValueChange = { vm.updateTitle(it) },
                            textStyle = TextStyle(
                                color = LockIn.TextPrimary,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 34.sp
                            ),
                            cursorBrush = SolidColor(LockIn.White),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Render each dynamic block
                itemsIndexed(blocks, key = { _, block -> block.id }) { index, block ->
                    BlockItemRow(
                        block = block,
                        onUpdate = { updated -> vm.updateBlock(updated) },
                        onRemove = { vm.removeBlock(block.id) },
                        onToggleTodo = { vm.toggleTodoBlock(block.id) },
                        onTableToggleRowDone = { r -> vm.tableToggleRowDone(block.id, r) },
                        onTableAddRow = { vm.tableAddRow(block.id) },
                        onTableRemoveRow = { r -> vm.tableRemoveRow(block.id, r) },
                        onTableAddCol = { vm.tableAddColumn(block.id) },
                        onTableRemoveCol = { c -> vm.tableRemoveColumn(block.id, c) },
                        onTableCellUpdate = { r, c, txt -> vm.tableUpdateCell(block.id, r, c, txt) },
                        onTableHeaderUpdate = { c, txt -> vm.tableUpdateHeader(block.id, c, txt) }
                    )
                }

                // Bottom extra spacing
                item {
                    Spacer(Modifier.height(80.dp))
                }
            }
        }

        // ── Samsung Notes / Notion Style Docked Bottom Toolbar ──────────────
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding(),
            color = LockIn.Surface,
            tonalElevation = 6.dp,
            border = BorderStroke(1.dp, LockIn.Border)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ToolbarButton(icon = Icons.Default.CheckBox, label = "To-Do") {
                    vm.addBlock(BlockType.TODO)
                }
                ToolbarButton(icon = Icons.Default.GridOn, label = "Table") {
                    vm.addBlock(BlockType.TABLE)
                }
                ToolbarButton(icon = Icons.Default.Title, label = "H1") {
                    vm.addBlock(BlockType.HEADING_1)
                }
                ToolbarButton(icon = Icons.Default.FormatSize, label = "H2") {
                    vm.addBlock(BlockType.HEADING_2)
                }
                ToolbarButton(icon = Icons.Default.FormatListBulleted, label = "Bullet") {
                    vm.addBlock(BlockType.BULLET)
                }
                ToolbarButton(icon = Icons.Default.FormatListNumbered, label = "1. List") {
                    vm.addBlock(BlockType.NUMBERED)
                }
                ToolbarButton(icon = Icons.Default.FormatQuote, label = "Quote") {
                    vm.addBlock(BlockType.QUOTE)
                }
                ToolbarButton(icon = Icons.Default.HorizontalRule, label = "Divider") {
                    vm.addBlock(BlockType.DIVIDER)
                }
                ToolbarButton(icon = Icons.Default.TextFields, label = "Text") {
                    vm.addBlock(BlockType.PARAGRAPH)
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Note", color = LockIn.TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to permanently delete this note?", color = LockIn.TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        vm.deleteCurrentNote()
                        onBack()
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

    // Unsaved changes confirmation dialog
    if (showUnsavedConfirm) {
        AlertDialog(
            onDismissRequest = { showUnsavedConfirm = false },
            title = { Text("Unsaved Changes", color = LockIn.TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("You have unsaved changes. Do you want to save before leaving?", color = LockIn.TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showUnsavedConfirm = false
                        vm.saveCurrentNote()
                        vm.closeNote()
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LockIn.White, contentColor = LockIn.Background)
                ) {
                    Text("Save & Exit", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        showUnsavedConfirm = false
                        vm.closeNote()
                        onBack()
                    }) {
                        Text("Discard", color = LockIn.PriorityHigh)
                    }
                    TextButton(onClick = { showUnsavedConfirm = false }) {
                        Text("Cancel", color = LockIn.TextSecondary)
                    }
                }
            },
            containerColor = LockIn.Surface
        )
    }
}

@Composable
private fun ToolbarButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(LockIn.Card)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = label, tint = LockIn.White, modifier = Modifier.size(16.dp))
        Text(label, fontSize = 12.sp, color = LockIn.TextPrimary, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun BlockItemRow(
    block: NoteBlock,
    onUpdate: (NoteBlock) -> Unit,
    onRemove: () -> Unit,
    onToggleTodo: () -> Unit,
    onTableToggleRowDone: (Int) -> Unit,
    onTableAddRow: () -> Unit,
    onTableRemoveRow: (Int) -> Unit,
    onTableAddCol: () -> Unit,
    onTableRemoveCol: (Int) -> Unit,
    onTableCellUpdate: (Int, Int, String) -> Unit,
    onTableHeaderUpdate: (Int, String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(modifier = Modifier.weight(1f)) {
            when (block) {
                is NoteBlock.Paragraph -> {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (block.text.isEmpty()) {
                            Text(
                                "Type text or add blocks...",
                                fontSize = 15.sp,
                                color = LockIn.TextSecondary.copy(alpha = 0.35f)
                            )
                        }
                        BasicTextField(
                            value = block.text,
                            onValueChange = { onUpdate(block.copy(text = it)) },
                            textStyle = TextStyle(color = LockIn.TextPrimary, fontSize = 15.sp, lineHeight = 22.sp),
                            cursorBrush = SolidColor(LockIn.White),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                is NoteBlock.Heading -> {
                    val (fSize, lHeight) = when (block.level) {
                        2 -> Pair(20.sp, 26.sp)
                        3 -> Pair(17.sp, 22.sp)
                        else -> Pair(24.sp, 30.sp)
                    }
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (block.text.isEmpty()) {
                            Text(
                                "Heading ${block.level}",
                                fontSize = fSize,
                                fontWeight = FontWeight.Bold,
                                color = LockIn.TextSecondary.copy(alpha = 0.35f)
                            )
                        }
                        BasicTextField(
                            value = block.text,
                            onValueChange = { onUpdate(block.copy(text = it)) },
                            textStyle = TextStyle(
                                color = LockIn.TextPrimary,
                                fontSize = fSize,
                                fontWeight = FontWeight.Bold,
                                lineHeight = lHeight
                            ),
                            cursorBrush = SolidColor(LockIn.White),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                is NoteBlock.Todo -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = block.isChecked,
                            onCheckedChange = { onToggleTodo() },
                            colors = CheckboxDefaults.colors(
                                checkedColor = LockIn.White,
                                uncheckedColor = LockIn.Border,
                                checkmarkColor = LockIn.Background
                            ),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (block.text.isEmpty()) {
                                Text(
                                    "To-do item...",
                                    fontSize = 15.sp,
                                    color = LockIn.TextSecondary.copy(alpha = 0.35f)
                                )
                            }
                            BasicTextField(
                                value = block.text,
                                onValueChange = { onUpdate(block.copy(text = it)) },
                                textStyle = TextStyle(
                                    color = if (block.isChecked) LockIn.TextSecondary.copy(alpha = 0.5f) else LockIn.TextPrimary,
                                    fontSize = 15.sp,
                                    textDecoration = if (block.isChecked) TextDecoration.LineThrough else TextDecoration.None
                                ),
                                cursorBrush = SolidColor(LockIn.White),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                is NoteBlock.Table -> {
                    TableBlockView(
                        table = block,
                        onToggleRowDone = onTableToggleRowDone,
                        onAddRow = onTableAddRow,
                        onRemoveRow = onTableRemoveRow,
                        onAddCol = onTableAddCol,
                        onRemoveCol = onTableRemoveCol,
                        onCellUpdate = onTableCellUpdate,
                        onHeaderUpdate = onTableHeaderUpdate
                    )
                }

                is NoteBlock.Bullet -> {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                        Text("•", fontSize = 18.sp, color = LockIn.White, modifier = Modifier.padding(horizontal = 6.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (block.text.isEmpty()) {
                                Text("List item...", fontSize = 15.sp, color = LockIn.TextSecondary.copy(alpha = 0.35f))
                            }
                            BasicTextField(
                                value = block.text,
                                onValueChange = { onUpdate(block.copy(text = it)) },
                                textStyle = TextStyle(color = LockIn.TextPrimary, fontSize = 15.sp),
                                cursorBrush = SolidColor(LockIn.White),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                is NoteBlock.Numbered -> {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                        Text("${block.number}.", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = LockIn.TextSecondary, modifier = Modifier.padding(end = 8.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (block.text.isEmpty()) {
                                Text("Numbered item...", fontSize = 15.sp, color = LockIn.TextSecondary.copy(alpha = 0.35f))
                            }
                            BasicTextField(
                                value = block.text,
                                onValueChange = { onUpdate(block.copy(text = it)) },
                                textStyle = TextStyle(color = LockIn.TextPrimary, fontSize = 15.sp),
                                cursorBrush = SolidColor(LockIn.White),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                is NoteBlock.Quote -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(LockIn.Card)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .fillMaxHeight()
                                .background(LockIn.White, RoundedCornerShape(2.dp))
                        )
                        Spacer(Modifier.width(10.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (block.text.isEmpty()) {
                                Text("Quote or highlight...", fontSize = 14.sp, fontStyle = FontStyle.Italic, color = LockIn.TextSecondary.copy(alpha = 0.4f))
                            }
                            BasicTextField(
                                value = block.text,
                                onValueChange = { onUpdate(block.copy(text = it)) },
                                textStyle = TextStyle(color = LockIn.TextPrimary, fontSize = 14.sp, fontStyle = FontStyle.Italic),
                                cursorBrush = SolidColor(LockIn.White),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                is NoteBlock.Divider -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                            .height(1.dp)
                            .background(LockIn.Border)
                    )
                }
            }
        }

        // Subtle delete button for non-divider blocks
        IconButton(
            onClick = onRemove,
            modifier = Modifier.size(24.dp).padding(start = 4.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Remove block", tint = LockIn.TextSecondary.copy(alpha = 0.3f), modifier = Modifier.size(14.dp))
        }
    }
}

@Composable
private fun TableBlockView(
    table: NoteBlock.Table,
    onToggleRowDone: (Int) -> Unit,
    onAddRow: () -> Unit,
    onRemoveRow: (Int) -> Unit,
    onAddCol: () -> Unit,
    onRemoveCol: (Int) -> Unit,
    onCellUpdate: (Int, Int, String) -> Unit,
    onHeaderUpdate: (Int, String) -> Unit
) {
    val colWidth = 150.dp
    val doneColWidth = 52.dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LockIn.Card)
            .border(1.dp, LockIn.Border, RoundedCornerShape(12.dp))
    ) {
        // Table Matrix with Horizontal Scrolling
        Box(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
            Column {
                // ── Clean Header Row ─────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .background(Color(0xFF161B22))
                        .border(width = 1.dp, color = LockIn.Border),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Header label for Done checkmark column
                    Box(
                        modifier = Modifier
                            .width(doneColWidth)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Done",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = LockIn.TextSecondary
                        )
                    }

                    // Divider after Done column
                    Box(modifier = Modifier.width(1.dp).height(44.dp).background(LockIn.Border))

                    // Column Headers
                    table.headers.forEachIndexed { colIdx, header ->
                        Row(
                            modifier = Modifier
                                .width(colWidth)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BasicTextField(
                                value = header,
                                onValueChange = { onHeaderUpdate(colIdx, it) },
                                textStyle = TextStyle(
                                    color = LockIn.TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                cursorBrush = SolidColor(LockIn.White),
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            if (table.headers.size > 1) {
                                IconButton(
                                    onClick = { onRemoveCol(colIdx) },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Del Col",
                                        tint = LockIn.TextSecondary.copy(alpha = 0.5f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        // Vertical gridline between columns
                        Box(modifier = Modifier.width(1.dp).height(44.dp).background(LockIn.Border))
                    }

                    // Add Column Button at end of header
                    Row(
                        modifier = Modifier
                            .clickable { onAddCol() }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Column", tint = LockIn.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add Col", fontSize = 12.sp, color = LockIn.White, fontWeight = FontWeight.SemiBold)
                    }
                }

                // ── Data Rows ───────────────────────────────────────────────
                table.rows.forEachIndexed { rowIdx, rowCells ->
                    val isDone = if (rowIdx < table.rowChecked.size) table.rowChecked[rowIdx] else false

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isDone) Color(0xFF0D1117).copy(alpha = 0.6f) else Color.Transparent)
                            .border(width = 0.5.dp, color = LockIn.Border),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Done Checkbox on the left
                        Box(
                            modifier = Modifier
                                .width(doneColWidth)
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Checkbox(
                                checked = isDone,
                                onCheckedChange = { onToggleRowDone(rowIdx) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = LockIn.White,
                                    uncheckedColor = LockIn.Border,
                                    checkmarkColor = LockIn.Background
                                ),
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // Vertical gridline
                        Box(modifier = Modifier.width(1.dp).height(48.dp).background(LockIn.Border))

                        // Cells without nested grey boxes
                        table.headers.forEachIndexed { colIdx, _ ->
                            val cellText = if (colIdx < rowCells.size) rowCells[colIdx] else ""
                            Box(
                                modifier = Modifier
                                    .width(colWidth)
                                    .padding(horizontal = 12.dp, vertical = 12.dp)
                            ) {
                                if (cellText.isEmpty()) {
                                    Text(
                                        "...",
                                        fontSize = 14.sp,
                                        color = LockIn.TextSecondary.copy(alpha = 0.25f)
                                    )
                                }
                                BasicTextField(
                                    value = cellText,
                                    onValueChange = { onCellUpdate(rowIdx, colIdx, it) },
                                    textStyle = TextStyle(
                                        color = if (isDone) LockIn.TextSecondary.copy(alpha = 0.45f) else LockIn.TextPrimary,
                                        fontSize = 14.sp,
                                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
                                    ),
                                    cursorBrush = SolidColor(LockIn.White),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }

                            // Vertical gridline
                            Box(modifier = Modifier.width(1.dp).height(48.dp).background(LockIn.Border))
                        }

                        // Delete Row Action
                        if (table.rows.size > 1) {
                            IconButton(
                                onClick = { onRemoveRow(rowIdx) },
                                modifier = Modifier.size(36.dp).padding(horizontal = 4.dp)
                            ) {
                                Icon(
                                    Icons.Default.DeleteOutline,
                                    contentDescription = "Del Row",
                                    tint = LockIn.PriorityHigh.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        } else {
                            Spacer(Modifier.width(36.dp))
                        }
                    }
                }
            }
        }

        // Add Row Footer Action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAddRow() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Row", tint = LockIn.TextSecondary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("+ Add Row", fontSize = 13.sp, color = LockIn.TextSecondary, fontWeight = FontWeight.SemiBold)
        }
    }
}

