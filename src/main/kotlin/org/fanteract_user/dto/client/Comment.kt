package org.fanteract_user.dto.client

import org.fanteract_user.enumerate.RiskLevel

data class Comment(
    val commentId: Long = 0L,
    var content: String,
    val boardId: Long,
    val userId: Long,
    val riskLevel: RiskLevel,
)