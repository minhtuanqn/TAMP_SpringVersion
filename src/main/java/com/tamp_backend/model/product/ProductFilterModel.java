package com.tamp_backend.model.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilterModel {

    private String productName;

    private UUID categoryId;

    private UUID supplierId;

    private double minPrice;

    private double maxPrice;

    private boolean priceFilter;

    private int productSearchStatusEnum;
}
