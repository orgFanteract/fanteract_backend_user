package org.fanteract_user.client

import org.fanteract_user.dto.client.*
import org.fanteract_user.enumerate.RiskLevel
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class CommentClient(
    @Value("\${client.comment-service.url}") commentServiceUrl: String,
    private val restClient: RestClient = RestClient.builder()
        .baseUrl(commentServiceUrl)
        .build(),
) {

    fun countByUserId(userId: Long): Long {
        val response = restClient.get()
            .uri("/internal/comments/{userId}/user/count", userId)
            .retrieve()
            .body(ReadCommentCountInnerResponse::class.java)

        return response?.count ?: 0L
    }

    fun countByUserIdAndRiskLevel(userId: Long, riskLevel: RiskLevel): Long {
        val response = restClient.get()
            .uri { builder ->
                builder
                    .path("/internal/comments/{userId}/user-risk/count")
                    .queryParam("riskLevel", riskLevel)
                    .build(userId)
            }
            .retrieve()
            .body(ReadCommentCountInnerResponse::class.java)

        return response?.count ?: 0L
    }

    fun findByUserIdAndRiskLevel(
        userId: Long,
        riskLevel: RiskLevel,
        pageable: Pageable
    ): ReadCommentPageInnerResponse {
        val response = restClient.get()
            .uri { builder ->
                builder
                    .path("/internal/comments/{userId}/user")
                    .queryParam("page", pageable.pageNumber)
                    .queryParam("size", pageable.pageSize)
                    .queryParam("riskLevel", riskLevel)
                    .build(userId)
            }
            .retrieve()
            .body(object : ParameterizedTypeReference<ReadCommentPageInnerResponse>() {})

        return requireNotNull(response) {
            "Failed to load comments for userId=$userId, riskLevel=$riskLevel"
        }
    }
}