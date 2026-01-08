package bg.unisofia.fmi.electronicstore.mapper;

import bg.unisofia.fmi.electronicstore.dto.response.OrderItemResponse;
import bg.unisofia.fmi.electronicstore.dto.response.OrderResponse;
import bg.unisofia.fmi.electronicstore.entity.Order;
import bg.unisofia.fmi.electronicstore.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "userFullName")
    OrderResponse toResponse(Order order);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponse toOrderItemResponse(OrderItem orderItem);
}
