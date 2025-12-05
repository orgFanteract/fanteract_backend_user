package org.fanteract_user.domain

import org.fanteract_user.entity.PaymentHistory
import org.fanteract_user.repo.PaymentHistoryRepo
import org.springframework.stereotype.Component

@Component
class PaymentHistoryWriter(
    private val paymentHistoryRepo: PaymentHistoryRepo
) {
    fun create(userId: Long, productId: Long): PaymentHistory {
        return paymentHistoryRepo.save(
            PaymentHistory(
                userId = userId,
                productId = productId,
            )
        )
    }

}