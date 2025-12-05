package org.fanteract_user.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.fanteract_user.client.BoardClient
import org.fanteract_user.client.ChatClient
import org.fanteract_user.client.CommentClient
import org.fanteract_user.domain.UserReader
import org.fanteract_user.domain.UserWriter
import org.fanteract_user.dto.inner.*
import org.fanteract_user.dto.outer.*
import org.fanteract_user.entity.User
import org.fanteract_user.enumerate.RiskLevel
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.Long
import kotlin.collections.map
import kotlin.text.toByteArray

@Transactional
@Service
class UserService(
    private val userReader: UserReader,
    private val userWriter: UserWriter,
    private val boardClient: BoardClient,
    private val commentClient: CommentClient,
    private val chatClient: ChatClient,
    @Value($$"${jwt.secret}") private val jwtSecret: String,
) {
    fun signIn(readUserSignInOuterRequest: ReadUserSignInOuterRequest): ReadUserSignInOuterResponse {
        val user = userReader.findByEmail(readUserSignInOuterRequest.email)

        if (user.password != readUserSignInOuterRequest.password){
            throw kotlin.NoSuchElementException("조건에 맞는 사용자가 존재하지 않습니다")
        }

        val secretKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
        val token = Jwts.builder().subject(user.userId.toString()).signWith(secretKey).compact()

        return ReadUserSignInOuterResponse(token)
    }

    fun signUp(createUserSignUpOuterRequest: CreateUserSignUpOuterRequest): User {
        val user =
            userWriter.create(
                email = createUserSignUpOuterRequest.email,
                password = createUserSignUpOuterRequest.password,
                name = createUserSignUpOuterRequest.name,
            )

        return user
    }

    fun readMyPage(userId: Long): ReadUserMyPageOuterResponse {
        val user = userReader.findById(userId)

        val chatroomCount = chatClient.countChatroomByUserId(user.userId)
        val chatCount = chatClient.countChatByUserId(user.userId)
        val boardCount = boardClient.countByUserId(user.userId)
        val commentCount = commentClient.countByUserId(user.userId)

        val restrictedChatCount = chatClient.countChatByUserIdAndRiskLevel(user.userId, RiskLevel.BLOCK)
        val restrictedBoardCount = boardClient.countByUserIdAndRiskLevel(user.userId, RiskLevel.BLOCK)
        val restrictedCommentCount = commentClient.countByUserIdAndRiskLevel(user.userId, RiskLevel.BLOCK)

        val activityStats =
            ActivityStats(
                totalChatRoomCount = chatroomCount,
                totalChatCount = chatCount,
                totalBoardCount = boardCount,
                totalCommentCount = commentCount,
            )
        val restrictionStats =
            RestrictionStats(
                totalRestrictedChatCount = restrictedChatCount,
                totalRestrictedBoardCount = restrictedBoardCount,
                totalRestrictedCommentCount = restrictedCommentCount,
            )
        val userScore = 
            UserScore(
                activePoint = user.activePoint,
                abusePoint = user.abusePoint,
                balance = user.balance,
        )

        return ReadUserMyPageOuterResponse(
            email = user.email,
            name = user.name,
            activityStats = activityStats,
            restrictionStats = restrictionStats,
            userScore = userScore
        )
    }

    fun readRestrictedBoard(userId: Long, page: Int, size: Int): ReadRestrictedBoardPageOuterResponse {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))

        val boardPage = boardClient.findByUserIdAndRiskLevel(userId, RiskLevel.BLOCK, pageable)

        val contents = boardPage.contents.map { board ->
            ReadRestrictedBoardOuterResponse(
                boardId = board.boardId,
                title = board.title,
                content = board.content,
                riskLevel = board.riskLevel
            )
        }

        return ReadRestrictedBoardPageOuterResponse(
            contents = contents,
            page = page,
            size = size,
            totalElements = boardPage.totalElements,
            totalPages = boardPage.totalPages,
            hasNext = boardPage.hasNext
        )
    }
    fun readRestrictedComment(userId: Long, page: Int, size: Int): ReadRestrictedCommentPageOuterResponse {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))

        val commentPage = commentClient.findByUserIdAndRiskLevel(userId, RiskLevel.BLOCK, pageable)

        val contents = commentPage.contents.map { comment ->
            ReadRestrictedCommentOuterResponse(
                commentId = comment.commentId,
                content = comment.content,
                riskLevel = comment.riskLevel
            )
        }

        return ReadRestrictedCommentPageOuterResponse(
            contents = contents,
            page = page,
            size = size,
            totalElements = commentPage.totalElements,
            totalPages = commentPage.totalPages,
            hasNext = commentPage.hasNext
        )
    }
    fun readRestrictedChat(userId: Long, page: Int, size: Int): ReadRestrictedChatPageOuterResponse {
        val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        val chatPage = chatClient.findChatByUserIdAndRiskLevel(userId, RiskLevel.BLOCK, pageable)

        val contents = chatPage.contents.map { chat ->
            ReadRestrictedChatOuterResponse(
                chatId = chat.chatId,
                content = chat.content,
                riskLevel = chat.riskLevel
            )
        }

        return ReadRestrictedChatPageOuterResponse(
            contents = contents,
            page = page,
            size = size,
            totalElements = chatPage.totalElements,
            totalPages = chatPage.totalPages,
            hasNext = chatPage.hasNext
        )
    }

    fun existsById(userId: Long): Boolean {
        userReader.existsById(userId)

        return true
    }
    fun findById(userId: Long): ReadUserInnerResponse {
        val user = userReader.findById(userId)

        return ReadUserInnerResponse(
            userId = user.userId,
            email = user.email,
            password = user.password,
            name = user.name,
            balance = user.balance,
            activePoint = user.activePoint,
            abusePoint = user.abusePoint,
            passExpiredAt = user.passExpiredAt
        )
    }
    fun updateBalance(userId: Long, balance: Int) {
        userWriter.updateBalance(userId, balance)
    }
    fun updateActivePoint(userId: Long, activePoint: Int) {
        userWriter.updateActivePoint(userId, activePoint)
    }

    fun updateAbusePoint(userId: Long, abusePoint: Int) {
        userWriter.updateAbusePoint(userId, abusePoint)
    }
    fun findByIdIn(userIds: List<Long>): ReadUserListInnerResponse {
        val userList = userReader.findByIdIn(userIds)

        val payload = userList.map{ user ->
            ReadUserInnerResponse(
                userId = user.userId,
                email = user.email,
                password = user.password,
                name = user.name,
                balance = 0,
                activePoint = 0,
                abusePoint = 0,
                passExpiredAt = user.passExpiredAt,
            )
        }

        return ReadUserListInnerResponse(payload)
    }
}