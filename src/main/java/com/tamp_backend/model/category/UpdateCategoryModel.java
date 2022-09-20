package com.tamp_backend.model.category;

import com.tamp_backend.constant.EntityStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryModel {

    @NotNull(message = "{category_id.null}")
    private UUID id;

    @NotNull(message = "{category_name.null}")
    @Length(message = "{category_name.length}", min = 1, max = 100)
    private String name;

    private String description;

    @Range(message = "{category_commission.percent}", min = 0, max = 100)
    private Double commissionRate;

    @NotNull(message = "{category_status.null}")
    private int status;
}
