package com.tamp_backend.model.partnertype;

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
public class CreatePartnerTypeModel {

    @NotNull(message = "{partnertype_name.null}")
    @Length(message = "{partnertype.length}", min = 1, max = 100)
    private String typeName;

    private String description;

    @Range(message = "{partnertype_commission.percent}", min = 0, max = 100)
    private Double commissionRate;

}
