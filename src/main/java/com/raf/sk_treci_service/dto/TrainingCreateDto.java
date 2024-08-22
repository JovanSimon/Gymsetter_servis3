package com.raf.sk_treci_service.dto;

import com.raf.sk_treci_service.domain.Gym;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
@Getter
@Setter
public class TrainingCreateDto {
    private String name;
    private String typeOfTrening, individualOrGroup;
    private Integer price;
    private Long gymId;
    private Gym gym;

    private String date;

    private String userList;
}
