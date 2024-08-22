package com.raf.sk_treci_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotiDto {
    private String email, type;
    private Long id;
    public NotiDto(String email, String type, Long id) {
        this.email = email;
        this.type = type;
        this.id = id;
    }
}


