package com.tamp_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model for pagination request from client
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationRequestModel {
    private int pageIndex;
    private int limit;
    private String sortBy;
    private String sortType;
}
