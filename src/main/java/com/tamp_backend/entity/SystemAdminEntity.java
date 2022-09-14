package com.tamp_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Date;
import java.util.UUID;

@Entity
@Table(name = "system_admin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemAdminEntity {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    @Type(type = "uuid-char")
    private UUID id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "address")
    private String address;

    @Column(name = "gender")
    private int gender;

    @Column(name = "phone")
    private String phone;

    @Column(name = "dob")
    private Date dob;

    @Column(name = "create_at")
    private Date createAt;

    @Column(name = "account_id")
    @Type(type = "uuid-char")
    private UUID accountId;

    @Column(name = "update_at")
    private Date updateAt;

    @Column(name = "avatar")
    private String avatar;

    @Column(name = "status")
    private int status;
}
