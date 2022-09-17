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
    private int pageIndex;
    private int totalPage;
    private int limit;
    private int totalRecord;
    private List<T> data;
}
