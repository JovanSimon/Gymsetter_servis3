package com.raf.sk_treci_service.repository;

import com.raf.sk_treci_service.domain.Gym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GymRepository extends JpaRepository<Gym, Long> {
    Gym findGymById(Long id);
    Gym findGymByManagerId(Long managerId);
    List<Gym> findAllByManagerId(Long managerId);
}
