package com.raf.sk_treci_service.service.impl;

import com.raf.sk_treci_service.domain.Discount;
import com.raf.sk_treci_service.dto.DiscountCreateDto;
import com.raf.sk_treci_service.dto.DiscountDto;
import com.raf.sk_treci_service.exception.NotFoundException;
import com.raf.sk_treci_service.mapper.DiscountMapper;
import com.raf.sk_treci_service.repository.DiscountRepository;
import com.raf.sk_treci_service.service.DiscountService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class DiscountServiceImpl implements DiscountService {

    private DiscountRepository discountRepository;
    private DiscountMapper discountMapper;

    public DiscountServiceImpl(DiscountRepository discountRepository, DiscountMapper discountMapper) {
        this.discountRepository = discountRepository;
        this.discountMapper = discountMapper;
    }

    @Override
    public DiscountDto add(DiscountCreateDto discountCreateDto) {
        Discount discount = discountMapper.discountDtoToDiscount(discountCreateDto);
        discountRepository.save(discount);
        return discountMapper.discountToDiscountDto(discount);
    }

    @Override
    public DiscountDto update(Long id, DiscountDto discountDto) {
        Discount discount = discountRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Client with id: " + id + " not found.")
        );

        discount.setDiscount(discountDto.getDiscount());
        if(discountDto.getBenefit() != null)
            discount.setBenefit(discountDto.getBenefit());
        discount.setLimitTrainings(discountDto.getLimitTrainings());

        return discountMapper.discountToDiscountDto(discount);
    }

    @Override
    public void deleteById(Long id) {
        discountRepository.deleteById(id);
    }
}
