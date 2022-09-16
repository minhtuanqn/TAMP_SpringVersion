package com.tamp_backend.model.systemaccount;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tamp_backend.entity.AccountEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AccountModel {

    private UUID id;

    private String username;

    private String email;

    @JsonIgnore
    private String password;

    private String role;

    @JsonProperty("accStatus")
    private int status;
}
