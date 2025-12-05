package org.fanteract_user.repo

import org.fanteract_user.entity.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepo: JpaRepository<Product, Long>