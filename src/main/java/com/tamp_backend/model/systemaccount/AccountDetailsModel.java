package com.tamp_backend.model.systemaccount;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AccountDetailsModel {

    public AccountDetailsModel(AccountModel accountModel, UUID userId) {
        this.id = accountModel.getId();
        this.email = accountModel.getEmail();
        this.password = accountModel.getPassword();
        this.role = accountModel.getRole();
        this.username = accountModel.getUsername();
        this.userId = userId;
    }

    public AccountDetailsModel id(UUID id) {
        this.id = id;
        return this;
    }

    public AccountDetailsModel email(String email) {
        this.email = email;
        return this;
    }

    public AccountDetailsModel password(String password) {
        this.password = password;
        return this;
    }

    public AccountDetailsModel role(String role) {
        this.role = role;
        return this;
    }

    public AccountDetailsModel username(String username) {
        this.username = username;
        return this;
    }

    public AccountDetailsModel userId(UUID userId) {
        this.userId = userId;
        return this;
    }

    private UUID id;

    private String username;

    private String email;

    private String password;

    private String role;

    private UUID userId;
}
