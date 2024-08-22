package com.raf.sk_treci_service.mapper;

import com.raf.sk_treci_service.domain.Discount;
import com.raf.sk_treci_service.dto.DiscountCreateDto;
import com.raf.sk_treci_service.dto.DiscountDto;
import org.springframework.stereotype.Component;

@Component
public class DiscountMapper {
    public DiscountDto discountToDiscountDto(Discount discount){
        DiscountDto discountDto = new DiscountDto();
        discountDto.setId(discount.getId());
        discountDto.setLimitTrainings(discount.getLimitTrainings());
        discountDto.setBenefit(discount.getBenefit());
        discountDto.setDiscount(discount.getDiscount());
        return discountDto;
    }

    public Discount discountDtoToDiscount(DiscountCreateDto discountCreateDto){
        Discount discount = new Discount();
        discount.setGymId(discountCreateDto.getGymId());
        discount.setLimitTrainings(discountCreateDto.getLimitTrainings());
        discount.setBenefit(discountCreateDto.getBenefit());
        discount.setDiscount(discountCreateDto.getDiscount());
        return discount;
    }
}
