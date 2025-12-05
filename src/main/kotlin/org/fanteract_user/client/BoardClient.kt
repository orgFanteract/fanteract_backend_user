package org.fanteract_user.client

import org.fanteract_user.dto.client.*
import org.fanteract_user.enumerate.RiskLevel
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class BoardClient(
    @Value("\${client.board-service.url}") boardServiceUrl: String,
    private val restClient: RestClient = RestClient.builder()
        .baseUrl(boardServiceUrl)
        .build(),
) {

    fun countByUserId(userId: Long): Long {
        val response = restClient.get()
            .uri("/internal/boards/{userId}/user/count", userId)
            .retrieve()
            .body(ReadBoardCountInnerResponse::class.java)

        return response?.count ?: 0L
    }

    fun countByUserIdAndRiskLevel(userId: Long, riskLevel: RiskLevel): Long {
        val response = restClient.get()
            .uri { builder ->
                builder
                    .path("/internal/boards/{userId}/user-risk/count")
                    .queryParam("riskLevel", riskLevel)
                    .build(userId)
            }
            .retrieve()
            .body(ReadBoardCountInnerResponse::class.java)

        return response?.count ?: 0L
    }

    fun findByUserIdAndRiskLevel(
        userId: Long,
        riskLevel: RiskLevel,
        pageable: Pageable
    ): ReadBoardPageInnerResponse {
        val response = restClient.get()
            .uri { builder ->
                builder
                    .path("/internal/boards/{userId}/user")
                    .queryParam("page", pageable.pageNumber)
                    .queryParam("size", pageable.pageSize)
                    .queryParam("riskLevel", riskLevel)
                    .build(userId)
            }
            .retrieve()
            .body(object : ParameterizedTypeReference<ReadBoardPageInnerResponse>() {})

        return requireNotNull(response) {
            "Failed to load boards for userId=$userId, riskLevel=$riskLevel"
        }
    }
}