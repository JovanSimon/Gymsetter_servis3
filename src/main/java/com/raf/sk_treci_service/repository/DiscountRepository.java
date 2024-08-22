package com.raf.sk_treci_service.repository;

import com.raf.sk_treci_service.domain.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    Discount findDiscountByGymId(Long gymId);
}
