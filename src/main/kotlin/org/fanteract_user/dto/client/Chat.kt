package org.fanteract_user.dto.client

import org.fanteract_user.enumerate.RiskLevel

data class Chat(
    val chatId: Long,
    val content: String,
    val chatroomId: Long,
    val userId: Long,
    val riskLevel: RiskLevel,
)