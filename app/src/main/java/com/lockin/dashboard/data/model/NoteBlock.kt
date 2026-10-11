package com.lockin.dashboard.data.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

enum class BlockType {
    PARAGRAPH,
    HEADING_1,
    HEADING_2,
    HEADING_3,
    TODO,
    TABLE,
    BULLET,
    NUMBERED,
    QUOTE,
    DIVIDER
}

sealed class NoteBlock(
    val id: String,
    val type: BlockType
) {
    data class Paragraph(
        val blockId: String = UUID.randomUUID().toString(),
        val text: String = ""
    ) : NoteBlock(blockId, BlockType.PARAGRAPH)

    data class Heading(
        val blockId: String = UUID.randomUUID().toString(),
        val text: String = "",
        val level: Int = 1 // 1, 2, or 3
    ) : NoteBlock(
        blockId,
        when (level) {
            2 -> BlockType.HEADING_2
            3 -> BlockType.HEADING_3
            else -> BlockType.HEADING_1
        }
    )

    data class Todo(
        val blockId: String = UUID.randomUUID().toString(),
        val text: String = "",
        val isChecked: Boolean = false
    ) : NoteBlock(blockId, BlockType.TODO)

    data class Table(
        val blockId: String = UUID.randomUUID().toString(),
        val headers: List<String> = listOf("Column 1", "Column 2"),
        val rows: List<List<String>> = listOf(listOf("", ""), listOf("", "")),
        val rowChecked: List<Boolean> = listOf(false, false)
    ) : NoteBlock(blockId, BlockType.TABLE)

    data class Bullet(
        val blockId: String = UUID.randomUUID().toString(),
        val text: String = ""
    ) : NoteBlock(blockId, BlockType.BULLET)

    data class Numbered(
        val blockId: String = UUID.randomUUID().toString(),
        val text: String = "",
        val number: Int = 1
    ) : NoteBlock(blockId, BlockType.NUMBERED)

    data class Quote(
        val blockId: String = UUID.randomUUID().toString(),
        val text: String = ""
    ) : NoteBlock(blockId, BlockType.QUOTE)

    data class Divider(
        val blockId: String = UUID.randomUUID().toString()
    ) : NoteBlock(blockId, BlockType.DIVIDER)

    companion object {
        fun serializeBlocks(blocks: List<NoteBlock>): String {
            val jsonArray = JSONArray()
            for (b in blocks) {
                val obj = JSONObject()
                obj.put("id", b.id)
                obj.put("type", b.type.name)
                when (b) {
                    is Paragraph -> {
                        obj.put("text", b.text)
                    }
                    is Heading -> {
                        obj.put("text", b.text)
                        obj.put("level", b.level)
                    }
                    is Todo -> {
                        obj.put("text", b.text)
                        obj.put("isChecked", b.isChecked)
                    }
                    is Table -> {
                        val headerArr = JSONArray()
                        b.headers.forEach { headerArr.put(it) }
                        obj.put("headers", headerArr)

                        val rowsArr = JSONArray()
                        b.rows.forEach { row ->
                            val rowArr = JSONArray()
                            row.forEach { cell -> rowArr.put(cell) }
                            rowsArr.put(rowArr)
                        }
                        obj.put("rows", rowsArr)

                        val checkedArr = JSONArray()
                        b.rowChecked.forEach { checkedArr.put(it) }
                        obj.put("rowChecked", checkedArr)
                    }
                    is Bullet -> {
                        obj.put("text", b.text)
                    }
                    is Numbered -> {
                        obj.put("text", b.text)
                        obj.put("number", b.number)
                    }
                    is Quote -> {
                        obj.put("text", b.text)
                    }
                    is Divider -> {}
                }
                jsonArray.put(obj)
            }
            return jsonArray.toString()
        }

        fun deserializeBlocks(jsonString: String?): List<NoteBlock> {
            if (jsonString.isNullOrBlank()) {
                return listOf(Paragraph(text = ""))
            }
            return try {
                val jsonArray = JSONArray(jsonString)
                val list = mutableListOf<NoteBlock>()
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val id = obj.optString("id", UUID.randomUUID().toString())
                    val typeStr = obj.optString("type", BlockType.PARAGRAPH.name)
                    val type = try {
                        BlockType.valueOf(typeStr)
                    } catch (e: Exception) {
                        BlockType.PARAGRAPH
                    }

                    when (type) {
                        BlockType.PARAGRAPH -> {
                            list.add(Paragraph(id, obj.optString("text", "")))
                        }
                        BlockType.HEADING_1 -> {
                            list.add(Heading(id, obj.optString("text", ""), 1))
                        }
                        BlockType.HEADING_2 -> {
                            list.add(Heading(id, obj.optString("text", ""), 2))
                        }
                        BlockType.HEADING_3 -> {
                            list.add(Heading(id, obj.optString("text", ""), 3))
                        }
                        BlockType.TODO -> {
                            list.add(
                                Todo(
                                    blockId = id,
                                    text = obj.optString("text", ""),
                                    isChecked = obj.optBoolean("isChecked", false)
                                )
                            )
                        }
                        BlockType.TABLE -> {
                            val headers = mutableListOf<String>()
                            val headersArr = obj.optJSONArray("headers")
                            if (headersArr != null) {
                                for (h in 0 until headersArr.length()) {
                                    headers.add(headersArr.getString(h))
                                }
                            }
                            if (headers.isEmpty()) {
                                headers.addAll(listOf("Column 1", "Column 2"))
                            }

                            val rows = mutableListOf<List<String>>()
                            val rowsArr = obj.optJSONArray("rows")
                            if (rowsArr != null) {
                                for (r in 0 until rowsArr.length()) {
                                    val cellArr = rowsArr.getJSONArray(r)
                                    val rowCells = mutableListOf<String>()
                                    for (c in 0 until cellArr.length()) {
                                        rowCells.add(cellArr.getString(c))
                                    }
                                    rows.add(rowCells)
                                }
                            }
                            if (rows.isEmpty()) {
                                rows.add(List(headers.size) { "" })
                            }

                            val rowChecked = mutableListOf<Boolean>()
                            val rowCheckedArr = obj.optJSONArray("rowChecked")
                            if (rowCheckedArr != null) {
                                for (rc in 0 until rowCheckedArr.length()) {
                                    rowChecked.add(rowCheckedArr.optBoolean(rc, false))
                                }
                            }
                            while (rowChecked.size < rows.size) {
                                rowChecked.add(false)
                            }

                            list.add(Table(blockId = id, headers = headers, rows = rows, rowChecked = rowChecked))
                        }
                        BlockType.BULLET -> {
                            list.add(Bullet(id, obj.optString("text", "")))
                        }
                        BlockType.NUMBERED -> {
                            list.add(
                                Numbered(
                                    blockId = id,
                                    text = obj.optString("text", ""),
                                    number = obj.optInt("number", 1)
                                )
                            )
                        }
                        BlockType.QUOTE -> {
                            list.add(Quote(id, obj.optString("text", "")))
                        }
                        BlockType.DIVIDER -> {
                            list.add(Divider(id))
                        }
                    }
                }
                if (list.isEmpty()) listOf(Paragraph(text = "")) else list
            } catch (e: Exception) {
                listOf(Paragraph(text = jsonString))
            }
        }
    }
}

