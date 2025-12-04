package org.fanteract_user.client

import org.fanteract_user.dto.client.Chat
import org.fanteract_user.enumerate.RiskLevel
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class ChatClient(
    @Value($$"${client.chat-service.url}") chatServiceUrl: String,
    private val restClient: RestClient = RestClient.builder()
        .baseUrl(chatServiceUrl)
        .build(),
) {
    fun countChatroomByUserId(userId: Long,): Long{

    }

    fun countChatByUserId(userId: Long,): Long{

    }

    fun countChatByUserIdAndRiskLevel(userId: Long, riskLevel: RiskLevel,): Long{

    }

    fun findChatByUserIdAndRiskLevel(userId: Long, riskLevel: RiskLevel,): Page<Chat>{

    }

}