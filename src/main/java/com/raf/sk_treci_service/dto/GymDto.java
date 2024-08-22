package com.raf.sk_treci_service.dto;

import com.raf.sk_treci_service.domain.Training;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GymDto {
    private Long id;
    private String name;
    private String description;
    private Integer numOfCoach;
    private List<Training> trenings = new ArrayList<>();
}
