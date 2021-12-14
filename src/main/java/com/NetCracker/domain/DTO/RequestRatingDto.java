package com.NetCracker.domain.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestRatingDto {

    private String feedback;
    private Integer rating;
    private Long doctorId;
    private Integer userId;
}
