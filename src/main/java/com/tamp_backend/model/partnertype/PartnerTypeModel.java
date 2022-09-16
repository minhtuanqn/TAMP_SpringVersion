package com.tamp_backend.model.partnertype;

import com.tamp_backend.entity.PartnerTypeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartnerTypeModel {

    public PartnerTypeModel(PartnerTypeEntity entity) {
        this.id = entity.getId();
        this.typeName = entity.getTypeName();
        this.description = entity.getDescription();
        this.commissionRate = entity.getCommissionRate();
        this.status = entity.getStatus();
    }

    private UUID id;

    private String typeName;

    private String description;

    private Double commissionRate;

    private Integer status;

}
