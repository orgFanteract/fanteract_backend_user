package org.fanteract_user.client

import org.fanteract_user.dto.client.Board
import org.fanteract_user.enumerate.RiskLevel
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class BoardClient(
    @Value($$"${client.board-service.url}") boardServiceUrl: String,
    private val restClient: RestClient = RestClient.builder()
        .baseUrl(boardServiceUrl)
        .build(),
){
    fun countBoardByUserId(userId: Long,): Long{

    }

    fun countBoardByUserIdAndRiskLevel(userId: Long, riskLevel: RiskLevel,): Long{

    }

    fun findBoardByUserIdAndRiskLevel(userId: Long, riskLevel: RiskLevel,): Page<Board>{

    }
}