package bg.unisofia.fmi.electronicstore.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull
    private Long userId;

    @NotEmpty
    private List<OrderItemRequest> items;
}
