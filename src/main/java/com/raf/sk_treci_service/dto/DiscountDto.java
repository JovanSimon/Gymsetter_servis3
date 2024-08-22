package com.raf.sk_treci_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class DiscountDto {
    private Long id;
    private int limitTrainings;
    private String benefit;
    private Integer discount;
}
