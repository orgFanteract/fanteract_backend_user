package org.fanteract_user.dto.client

data class Board(
    val boardId: Long = 0L,
    val title: String,
    val content: String,
    val userId: Long,
)