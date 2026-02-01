package com.wdreslerski.ecommerce.order;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT oi.product.id AS productId, oi.product.name AS productName, SUM(oi.quantity) AS totalSold " +
            "FROM OrderItem oi " +
            "GROUP BY oi.product.id, oi.product.name " +
            "ORDER BY totalSold DESC")
    List<Object[]> findBestSellingProducts(Pageable pageable);
}
