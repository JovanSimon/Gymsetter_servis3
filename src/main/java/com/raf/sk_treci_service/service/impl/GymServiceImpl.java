package com.raf.sk_treci_service.service.impl;

import com.raf.sk_treci_service.domain.Gym;
import com.raf.sk_treci_service.dto.GymCreateDto;
import com.raf.sk_treci_service.dto.GymDto;
import com.raf.sk_treci_service.exception.NotFoundException;
import com.raf.sk_treci_service.mapper.GymMapper;
import com.raf.sk_treci_service.repository.GymRepository;
import com.raf.sk_treci_service.service.GymService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GymServiceImpl implements GymService {
    private GymRepository gymRepository;
    private GymMapper gymMapper;

    public GymServiceImpl(GymRepository gymRepository, GymMapper gymMapper) {
        this.gymRepository = gymRepository;
        this.gymMapper = gymMapper;
    }

    @Override
    public List<GymDto> findAll() {
        return gymRepository.findAll().stream()
                .map(gymMapper::gymToGymDto)
                .collect(Collectors.toList());
    }

    @Override
    public GymDto findGymById(Long id) {
        return gymRepository.findById(id).map(gymMapper::gymToGymDto).orElseThrow(()->
                new NotFoundException("Client with id: " + id + " not found.")
        );
    }

    @Override
    public GymDto add(GymCreateDto gymCreateDto) {
        Gym gym = gymMapper.gymDtoToDym(gymCreateDto);
        gymRepository.save(gym);
        return gymMapper.gymToGymDto(gym);
    }

    @Override
    public GymDto update(Long id, GymDto gymDto) {
        Gym gym = gymRepository.findById(id).orElseThrow(()->
                new NotFoundException("Client with id: " + id + " not found.")
        );

        if(gymDto.getName() != null)
            gym.setName(gymDto.getName());
        if(gymDto.getDescription() != null)
            gym.setDescription(gymDto.getDescription());
        if(gymDto.getNumOfCoach() != null)
            gym.setNumOfCoach(gymDto.getNumOfCoach());
        if(!gymDto.getTrenings().isEmpty())
            gym.setTrenings(gymDto.getTrenings());

        return gymMapper.gymToGymDto(gymRepository.save(gym));
    }

    @Override
    public void deleteById(Long id) {
        gymRepository.deleteById(id);
    }
}
