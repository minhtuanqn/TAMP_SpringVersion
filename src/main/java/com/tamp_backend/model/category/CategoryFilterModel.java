package com.tamp_backend.model.category;

import com.tamp_backend.constant.StatusSearchEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryFilterModel {

    private int statusType;

    private String categoryName;
}
