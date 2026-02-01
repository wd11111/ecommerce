package com.wdreslerski.ecommerce.review;

import com.wdreslerski.ecommerce.common.BaseEntity;
import com.wdreslerski.ecommerce.product.Product;
import com.wdreslerski.ecommerce.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "product_id" }))
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @jakarta.validation.constraints.Min(1)
    @jakarta.validation.constraints.Max(5)
    @Column(nullable = false)
    private Integer rating;

    private String comment;
}
