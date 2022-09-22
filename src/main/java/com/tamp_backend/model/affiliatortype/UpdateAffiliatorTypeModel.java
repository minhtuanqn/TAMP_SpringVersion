package com.tamp_backend.model.affiliatortype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAffiliatorTypeModel {
    @NotNull(message = "{affiliatortype_id.null}")
    private UUID id;

    @NotNull(message = "{affiliatortype_name.null}")
    @Length(message = "{affiliatortype.length}", min = 1, max = 100)
    private String typeName;

    private String description;

    @Range(message = "{affiliatortype_commission.percent}", min = 0, max = 100)
    private Double commissionRate;

    @NotNull(message = "{affiliatortype_status.null}")
    private Integer status;
}
