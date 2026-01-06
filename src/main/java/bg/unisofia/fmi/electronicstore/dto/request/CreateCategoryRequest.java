package bg.unisofia.fmi.electronicstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryRequest {
    @NotBlank
    private String name;

    private String description;
}
