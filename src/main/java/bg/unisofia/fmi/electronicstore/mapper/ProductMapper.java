package bg.unisofia.fmi.electronicstore.mapper;

import bg.unisofia.fmi.electronicstore.dto.request.CreateProductRequest;
import bg.unisofia.fmi.electronicstore.dto.response.ProductResponse;
import bg.unisofia.fmi.electronicstore.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {

    @Mapping(target = "averageRating", ignore = true)
    ProductResponse toResponse(Product product);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    Product toEntity(CreateProductRequest request);
}
