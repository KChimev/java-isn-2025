package bg.unisofia.fmi.electronicstore.dto.response;

import bg.unisofia.fmi.electronicstore.entity.UserRole;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private UserRole role;
    private LocalDateTime createdAt;
}
