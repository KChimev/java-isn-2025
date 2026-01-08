package bg.unisofia.fmi.electronicstore.mapper;

import bg.unisofia.fmi.electronicstore.dto.request.CreateUserRequest;
import bg.unisofia.fmi.electronicstore.dto.response.UserResponse;
import bg.unisofia.fmi.electronicstore.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    User toEntity(CreateUserRequest request);
}
