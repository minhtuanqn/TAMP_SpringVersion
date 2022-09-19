package com.tamp_backend.model.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductModel {

    private UUID supplierId;

    private UUID categoryId;

    private String name;

    private String description;

    private double defaultPrice;

    private List<String> productImgs;
}
