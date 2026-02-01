package com.wdreslerski.ecommerce.shipping;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ShippingAddressDTO(
        @NotBlank(message = "Address is required") String address,

        @NotBlank(message = "City is required") String city,

        @NotBlank(message = "Postal code is required") String postalCode,

        @NotBlank(message = "Country is required") String country) {
}
