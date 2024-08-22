package com.raf.sk_treci_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
public class DiscountCreateDto {
    private int limitTrainings;
    private String benefit;
    private Integer discount;
    private Long gymId;
}
