package com.raf.sk_treci_service.dto;

import com.raf.sk_treci_service.domain.Training;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class GymCreateDto {
    private String name;
    private String description;
    private List<Training> trenings = new ArrayList<>();
    private Long managerId;
}
