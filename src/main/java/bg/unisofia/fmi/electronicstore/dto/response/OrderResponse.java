package bg.unisofia.fmi.electronicstore.dto.response;

import bg.unisofia.fmi.electronicstore.entity.OrderStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
}
