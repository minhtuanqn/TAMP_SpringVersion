package com.tamp_backend.model.campaign;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCampaignModel {
    @NotNull(message = "{campaign_name.null}")
    private String name;
    @NotNull(message = "{campaign_description.null}")
    private String description;
    @NotNull(message = "{campaign_start_time.null}")
    private LocalDateTime startTime;
    @NotNull(message = "{campaign_end_time.null}")
    private LocalDateTime endTime;
}
