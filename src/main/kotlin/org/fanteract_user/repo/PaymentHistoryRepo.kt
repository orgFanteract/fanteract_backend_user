package org.fanteract_user.repo

import org.fanteract_user.entity.PaymentHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentHistoryRepo: JpaRepository<PaymentHistory, Long>