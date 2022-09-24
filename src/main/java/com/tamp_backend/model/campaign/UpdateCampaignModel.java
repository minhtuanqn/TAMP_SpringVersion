package com.tamp_backend.model.campaign;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCampaignModel {

    @NotNull(message = "{campaign_id.null}")
    private UUID id;

    @NotNull(message = "{campaign_name.null}")
    private String name;

    @NotNull(message = "{campaign_description.null}")
    private String description;

    @NotNull(message = "{campaign_start_time.null}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @NotNull(message = "{campaign_end_time.null}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endTime;

}
