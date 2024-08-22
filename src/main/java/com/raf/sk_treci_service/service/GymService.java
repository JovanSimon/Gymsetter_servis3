package com.raf.sk_treci_service.service;

import com.raf.sk_treci_service.dto.GymCreateDto;
import com.raf.sk_treci_service.dto.GymDto;

import java.util.List;

public interface GymService {
    List<GymDto> findAll();
    GymDto findGymById(Long id);
    GymDto add(GymCreateDto gymCreateDto);
    GymDto update(Long id, GymDto gymDto);
    void deleteById(Long id);
}
