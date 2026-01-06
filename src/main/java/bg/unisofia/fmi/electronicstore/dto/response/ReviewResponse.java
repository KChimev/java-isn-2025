package bg.unisofia.fmi.electronicstore.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private Long productId;
    private String productName;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
