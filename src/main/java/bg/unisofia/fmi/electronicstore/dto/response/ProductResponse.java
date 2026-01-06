package bg.unisofia.fmi.electronicstore.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private LocalDateTime createdAt;
    private Set<CategoryResponse> categories;
    private Double averageRating;
}
