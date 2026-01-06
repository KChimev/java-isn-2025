package bg.unisofia.fmi.electronicstore.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReviewRequest {
    @NotNull
    private Long userId;

    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    private String comment;
}
