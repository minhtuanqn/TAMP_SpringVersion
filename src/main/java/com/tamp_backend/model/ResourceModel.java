package com.tamp_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Model for response pagination
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResourceModel<T> {
    private String searchText;
    private int pageIndex;
    private int totalPage;
    private int limit;
    private String sortBy;
    private String sortType;
    private int totalResult;
    private List<T> data;
}
