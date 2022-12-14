package com.tamp_backend.model.station;

import com.tamp_backend.entity.StationEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StationModel {

    private UUID id;

    private String name;

    private Double longitude;

    private Double latitude;

    private String address;

    private String detailAddress;

    private LocalDateTime createAt;

    private int status;

}
