package com.tamp_backend.model.account;

import com.tamp_backend.entity.AccountEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AccountModel {

    public AccountModel(AccountEntity entity) {
        this.id = entity.getId();
        this.username = entity.getUsername();
        this.email = entity.getEmail();
        this.password = entity.getPassword();
        this.role = entity.getRole();
        this.status = entity.getStatus();
    }

    private UUID id;

    private String username;

    private String email;

    private String password;

    private String role;

    private int status;
}
