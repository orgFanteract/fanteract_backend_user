package org.fanteract_user.client

import org.fanteract_user.dto.client.Comment
import org.fanteract_user.enumerate.RiskLevel
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class CommentClient(
    @Value($$"${client.comment-service.url}") commentServiceUrl: String,
    private val restClient: RestClient = RestClient.builder()
        .baseUrl(commentServiceUrl)
        .build(),
) {
    fun countCommentByUserId(userId: Long,): Long{

    }

    fun countCommentByUserIdAndRiskLevel(userId: Long, riskLevel: RiskLevel,): Long{

    }

    fun findCommentByUserIdAndRiskLevel(userId: Long, riskLevel: RiskLevel,): Page<Comment>{

    }
}