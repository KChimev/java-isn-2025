package bg.unisofia.fmi.electronicstore.mapper;

import bg.unisofia.fmi.electronicstore.dto.request.CreateCategoryRequest;
import bg.unisofia.fmi.electronicstore.dto.response.CategoryResponse;
import bg.unisofia.fmi.electronicstore.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse toResponse(Category category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CreateCategoryRequest request);
}
