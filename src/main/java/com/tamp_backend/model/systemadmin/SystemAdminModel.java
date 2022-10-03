package com.tamp_backend.model.systemadmin;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tamp_backend.model.systemaccount.AccountModel;
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

    @JsonIgnore
    private UUID accountId;

    private Date updateAt;

    private String avatar;

    @JsonProperty("adminStatus")
    private int status;

    @JsonProperty("accountDTO")
    private AccountModel accountModel;
}
