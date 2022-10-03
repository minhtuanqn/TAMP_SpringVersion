package com.tamp_backend.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Model for pagination request from client
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationRequestModel {
    private int curPage;
    private int pageSize;
    private String sortBy;
    private String sortType;
}
