package com.tamp_backend.entity;

import com.tamp_backend.model.StationModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "station")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StationEntity {
    public StationEntity(StationModel model) {
        this.id = model.getId();
        this.name = model.getName();
        this.longitude = model.getLongitude();
        this.latitude = model.getLatitude();
        this.address = model.getAddress();
        this.detailAddress = model.getDetailAddress();
        this.createAt = model.getCreateAt();
        this.status = model.getStatus();
    }
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private UUID id;
    @Column(name = "name")
    private String name;
    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "address")
    private String address;

    @Column(name = "detail_address")
    private String detailAddress;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "status")
    private int status;



}
