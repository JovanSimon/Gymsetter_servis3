package com.raf.sk_treci_service.repository;

import com.raf.sk_treci_service.domain.Training;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {
    Training findTrainingById(Long id);
    List<Training> findTrainingsByGymId(Long gymId);
}
