package com.tamp_backend.model.affiliator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tamp_backend.model.systemaccount.AccountModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AffiliatorModel {
    private UUID id;

    @JsonProperty("accountDTO")
    private AccountModel accountModel;

    private String fullName;

    private Date dob;

    private String phone;

    private String avatar;

    private int gender;

    private String address;

    private LocalDateTime createAt;

    private String code;

    private LocalDateTime updateAt;

    private UUID partnerId;

    private UUID typeId;

    @JsonIgnore
    private UUID accountId;

    @JsonProperty("affStatus")
    private int status;


}
