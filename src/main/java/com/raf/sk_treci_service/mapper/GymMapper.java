package com.raf.sk_treci_service.mapper;

import com.raf.sk_treci_service.domain.Gym;
import com.raf.sk_treci_service.dto.GymCreateDto;
import com.raf.sk_treci_service.dto.GymDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class GymMapper {
    public GymDto gymToGymDto(Gym gym){
        GymDto gymDto = new GymDto();
        gymDto.setId(gym.getId());
        gymDto.setNumOfCoach(gym.getNumOfCoach());
        gymDto.setName(gym.getName());
        gymDto.setDescription(gym.getDescription());
        gymDto.setTrenings(gym.getTrenings());
        return gymDto;
    }

    public Gym gymDtoToDym(GymCreateDto gymCreateDto){
        Gym gym = new Gym();
        gym.setManagerId(gymCreateDto.getManagerId());
        gym.setName(gymCreateDto.getName());
        gym.setDescription(gymCreateDto.getDescription());
        gym.setTrenings(new ArrayList<>());
        return gym;
    }
}
