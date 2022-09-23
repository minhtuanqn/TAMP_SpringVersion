package com.tamp_backend.model.campaign;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampaignModel {
    private UUID id;
    private String name;
    private String description;
    private UUID createBy;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String coverPhoto;
    private int status;
}
