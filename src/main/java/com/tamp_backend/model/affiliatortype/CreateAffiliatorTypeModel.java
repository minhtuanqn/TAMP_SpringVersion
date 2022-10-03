package com.tamp_backend.model.affiliatortype;

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
public class CreateAffiliatorTypeModel {

    @NotNull(message = "{affiliatortype_name.null}")
    @Length(message = "{affiliatortype.length}", min = 1, max = 100)
    private String typeName;

    private String description;

    @Range(message = "{affiliatortype_commission.percent}", min = 0, max = 100)
    private Double commissionRate;

}
