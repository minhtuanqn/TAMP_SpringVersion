package com.tamp_backend.model.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryModel {

    @NotNull(message = "{category_name.null}")
    @Length(message = "{category_name.length}", min = 1, max = 100)
    private String name;

    private String description;

    @Range(message = "{category_commission.percent}", min = 0, max = 100)
    private Double commissionRate;

}
