package com.tamp_backend.model.supplier;

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
public class SupplierModel {
    private UUID id;

    private String name;

    private String phone;

    private String logo;

    private String description;

    private String address;

    private LocalDateTime createAt;

    private UUID createBy;

    private LocalDateTime updateAt;

    @JsonIgnore
    private UUID accountId;

    @JsonProperty("supplierStatus")
    private int status;

    @JsonProperty("accountDTO")
    private AccountModel accountModel;
}
