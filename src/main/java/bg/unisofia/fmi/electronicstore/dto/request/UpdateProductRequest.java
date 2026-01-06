package bg.unisofia.fmi.electronicstore.dto.request;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Set;

@Data
public class UpdateProductRequest {
    private String name;
    private String description;

    @Positive
    private BigDecimal price;

    @PositiveOrZero
    private Integer stockQuantity;

    private Set<Long> categoryIds;
}
