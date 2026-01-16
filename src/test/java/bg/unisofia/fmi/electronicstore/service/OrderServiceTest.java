package bg.unisofia.fmi.electronicstore.service;

import bg.unisofia.fmi.electronicstore.dto.request.CreateOrderRequest;
import bg.unisofia.fmi.electronicstore.dto.request.OrderItemRequest;
import bg.unisofia.fmi.electronicstore.dto.response.OrderResponse;
import bg.unisofia.fmi.electronicstore.entity.*;
import bg.unisofia.fmi.electronicstore.exception.InsufficientStockException;
import bg.unisofia.fmi.electronicstore.exception.ResourceNotFoundException;
import bg.unisofia.fmi.electronicstore.mapper.OrderMapper;
import bg.unisofia.fmi.electronicstore.repository.OrderRepository;
import bg.unisofia.fmi.electronicstore.repository.ProductRepository;
import bg.unisofia.fmi.electronicstore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private User user;
    private Product product;
    private Order order;
    private OrderResponse orderResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setFullName("Test User");

        product = new Product();
        product.setId(1L);
        product.setName("Phone");
        product.setPrice(BigDecimal.valueOf(499.99));
        product.setStockQuantity(5);
        product.setVersion(0L);

        order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setItems(new ArrayList<>());

        orderResponse = new OrderResponse();
        orderResponse.setId(1L);
        orderResponse.setUserId(1L);
        orderResponse.setStatus(OrderStatus.PENDING);
    }

    @Test
    void createOrder_WithValidData_ShouldCreateOrder() {
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(2);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L);
        request.setItems(List.of(itemRequest));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

        OrderResponse result = orderService.createOrder(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(productRepository).save(any(Product.class));
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_WithInsufficientStock_ShouldThrowException() {
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(1L);
        itemRequest.setQuantity(10);

        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(1L);
        request.setItems(List.of(itemRequest));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(request));
    }

    @Test
    void createOrder_WithNonExistentUser_ShouldThrowException() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(99L);
        request.setItems(List.of(new OrderItemRequest()));

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.createOrder(request));
    }

    @Test
    void updateOrderStatus_ToCancelled_ShouldRestoreStock() {
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        order.getItems().add(orderItem);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toResponse(any(Order.class))).thenReturn(orderResponse);

        OrderResponse result = orderService.updateOrderStatus(1L, OrderStatus.CANCELLED);

        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void getOrderById_WhenExists_ShouldReturnOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);

        OrderResponse result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getOrderById_WhenNotExists_ShouldThrowException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(99L));
    }

    @Test
    void getOrdersByUser_ShouldReturnUserOrders() {
        when(orderRepository.findByUserId(1L)).thenReturn(List.of(order));
        when(orderMapper.toResponse(order)).thenReturn(orderResponse);

        List<OrderResponse> result = orderService.getOrdersByUser(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
