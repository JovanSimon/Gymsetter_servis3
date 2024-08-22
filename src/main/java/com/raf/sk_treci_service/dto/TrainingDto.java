package com.raf.sk_treci_service.dto;

import com.raf.sk_treci_service.domain.Gym;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Setter
@Getter

public class TrainingDto {
    private Long id;
    private String typeOfTrening, individualOrGroup, name;
    private Integer price;
    private String userList;
    private String date;
    private Gym gym;
}
