package com.tamp_backend.entity;

import com.tamp_backend.model.PartnerTypeModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "partner_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartnerTypeEntity {
    public PartnerTypeEntity(PartnerTypeModel model){
        this.id = model.getId();
        this.typeName = model.getTypeName();
        this.description = model.getDescription();
        this.commissionRate = model.getCommissionRate();
        this.status = model.getStatus();
    }
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;
    @Column(name = "type_name")
    private String typeName;
    @Column(name = "description")
    private String description;
    @Column(name = "commission_rate")
    private Double commissionRate;
    @Column(name = "status")
    private Integer status;
}
