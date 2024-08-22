package com.raf.sk_treci_service.service;

import com.raf.sk_treci_service.dto.TrainingCreateDto;
import com.raf.sk_treci_service.dto.TrainingDto;
import com.raf.sk_treci_service.sortIt.SortIt;

import java.util.List;

public interface TrainingService {
    List<TrainingDto> findAll();

    List<TrainingDto> findTrainingsForUser(Long userId);

    List<TrainingDto> sortByTime(SortIt sort);
    List<TrainingDto> sortByType(SortIt sort,Long id);
    List<TrainingDto> sortByIndividualOrGroup(SortIt sortIt,Long id);
    List<TrainingDto> sortByDay(SortIt sort);
    TrainingDto add(TrainingCreateDto trainingCreateDto);
    TrainingDto update(Long id, TrainingDto trainingDto);
    void deleteById(Long id);
    TrainingDto zakazi(Long userId, Long trainingId);
    List<TrainingDto> findAllFreeByGymId(Long id);
    TrainingDto otkazi(Long userId, Long trainingId);
    TrainingDto otkaziManager(Long userId, Long trainingId);


}
