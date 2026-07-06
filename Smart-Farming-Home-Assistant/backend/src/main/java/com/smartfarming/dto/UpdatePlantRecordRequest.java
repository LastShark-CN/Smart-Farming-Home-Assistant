package com.smartfarming.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePlantRecordRequest {

    private Long cropId;

    private String cropName;

    private String recordDate;

    private String content;

    private String images;
}
