package org.fanteract_user.service

import org.fanteract_user.domain.PaymentHistoryWriter
import org.fanteract_user.domain.ProductReader
import org.fanteract_user.domain.UserReader
import org.fanteract_user.domain.UserWriter
import org.fanteract_user.dto.PurchaseProductResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class PaymentService(
    private val userReader: UserReader,
    private val userWriter: UserWriter,
    private val paymentHistoryWriter: PaymentHistoryWriter,
    private val productReader: ProductReader,
) {
    fun purchaseProduct(productId: Long, userId: Long): PurchaseProductResponse {
        val product = productReader.findById(productId)

        // user balance 갱신
        val user = userReader.findById(userId)

        userWriter.updateBalance(
            userId = user.userId,
            balance = product.cost
        )

        // payment 기록
        val response =
            paymentHistoryWriter.create(
                userId = userId,
                productId = productId,
            )

        return PurchaseProductResponse(paymentHistoryId = response.paymentHistoryId)
    }
}