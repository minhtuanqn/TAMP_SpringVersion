package com.tamp_backend.model.partner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tamp_backend.model.systemaccount.AccountModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartnerModel {
    private UUID id;

    @JsonProperty("accountDTO")
    private AccountModel accountModel;

    private String name;

    private String address;

    private String phone;

    private String logo;

    private String description;

    private UUID typeId;

    private LocalDateTime createAt;

    private UUID createBy;

    private LocalDateTime updateAt;

    @JsonIgnore
    private UUID accountId;

    @JsonProperty("partnerStatus")
    private int status;
}
