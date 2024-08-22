package com.raf.sk_treci_service.service;

import com.raf.sk_treci_service.dto.DiscountCreateDto;
import com.raf.sk_treci_service.dto.DiscountDto;

public interface DiscountService {
    DiscountDto add(DiscountCreateDto discountCreateDto);
    DiscountDto update(Long id, DiscountDto discountDto);
    void deleteById(Long id);
}
