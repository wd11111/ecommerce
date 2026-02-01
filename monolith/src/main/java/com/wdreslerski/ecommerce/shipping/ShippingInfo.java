package com.wdreslerski.ecommerce.shipping;

import com.wdreslerski.ecommerce.common.BaseEntity;
import com.wdreslerski.ecommerce.order.Order;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ShippingInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private String country;

    private String trackingNumber;

    @Enumerated(EnumType.STRING)
    private ShippingStatus status;
}
