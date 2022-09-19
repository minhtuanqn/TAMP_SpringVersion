package com.tamp_backend.entity;

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
@Table(name = "partner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartnerEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "logo")
    private String logo;

    @Column(name = "description")
    private String description;

    @Column(name = "type_id")
    @Type(type = "uuid-char")
    private UUID typeId;

    @Column(name = "address")
    private String address;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "create_by")
    @Type(type = "uuid-char")
    private UUID createBy;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "account_id")
    @Type(type = "uuid-char")
    private UUID accountId;

    @Column(name = "status")
    private int status;

}
