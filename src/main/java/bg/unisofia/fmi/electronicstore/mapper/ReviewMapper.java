package bg.unisofia.fmi.electronicstore.mapper;

import bg.unisofia.fmi.electronicstore.dto.response.ReviewResponse;
import bg.unisofia.fmi.electronicstore.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "userFullName")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    ReviewResponse toResponse(Review review);
}
