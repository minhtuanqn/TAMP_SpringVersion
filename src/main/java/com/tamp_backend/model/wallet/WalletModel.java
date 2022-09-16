package com.tamp_backend.model.wallet;

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
public class WalletModel {
    private UUID id;

    private double amount;

    private LocalDateTime createAt;

    private String ownerType;

    private UUID accountId;

    private int status;
}
