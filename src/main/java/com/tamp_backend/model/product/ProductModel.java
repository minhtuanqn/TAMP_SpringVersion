package com.tamp_backend.model.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductModel {
    private UUID id;

    private UUID supplierId;

    private UUID categoryId;

    private String name;

    private String description;

    private double defaultPrice;

    private String imgList;

    private LocalDateTime createAt;

    private LocalDateTime updateAt;

    @Nullable
    private UUID approvedBy;

    private int status;
}
