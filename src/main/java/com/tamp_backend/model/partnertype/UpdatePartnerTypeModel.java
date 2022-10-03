package com.tamp_backend.model.partnertype;

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
public class UpdatePartnerTypeModel {

    @NotNull(message = "{partnertype_id.null}")
    private UUID id;

    @NotNull(message = "{partnertype_name.null}")
    @Length(message = "{partnertype.length}", min = 1, max = 100)
    private String typeName;

    private String description;

    @Range(message = "{partnertype_commission.percent}", min = 0, max = 100)
    private Double commissionRate;

    @NotNull(message = "{partnertype_status.null}")
    private Integer status;
}
