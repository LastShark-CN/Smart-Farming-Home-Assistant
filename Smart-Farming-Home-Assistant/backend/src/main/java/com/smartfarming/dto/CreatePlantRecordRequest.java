package com.smartfarming.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePlantRecordRequest {

    private Long cropId;

    private String cropName;

    @NotBlank(message = "记录日期不能为空")
    private String recordDate;

    @NotBlank(message = "记录内容不能为空")
    private String content;

    private String images;
}
