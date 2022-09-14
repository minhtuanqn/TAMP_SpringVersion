package com.tamp_backend.model.systemadmin;

import com.tamp_backend.entity.SystemAdminEntity;
import com.tamp_backend.model.account.AccountModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemAdminModel {

    private UUID id;

    private String fullName;

    private String address;

    private int gender;

    private String phone;

    private Date dob;

    private Date createAt;

    private UUID accountId;

    private Date updateAt;

    private String avatar;

    private int status;
}
