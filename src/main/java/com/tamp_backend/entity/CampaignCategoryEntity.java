package com.tamp_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "campaign_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CampaignCategoryEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id",nullable = false)
    private CampaignEntity campaignEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id",nullable = false)
    private CategoryEntity categoryEntity;

    @Column(name = "commission_rate")
    private double commissionRate;

    @Column(name = "status")
    private int status;
}
