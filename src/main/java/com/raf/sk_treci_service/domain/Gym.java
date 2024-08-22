package com.raf.sk_treci_service.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Gym {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name, description;
    private Long managerId;

    private Integer numOfCoach;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "gym", orphanRemoval = true) //ovo znaci da ce da bude mapirano na atribut u klasi koji se zove gym
    private List<Training> trenings = new ArrayList<>();

    @CreatedDate
    private Instant createdDate;

    @LastModifiedDate
    private Instant updatedDate;
}

