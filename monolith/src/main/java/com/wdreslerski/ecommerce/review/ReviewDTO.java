package com.wdreslerski.ecommerce.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewDTO {
    private Long id;
    private Long productId;
    private String userName;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    private String comment;
}
