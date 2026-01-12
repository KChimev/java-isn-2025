package bg.unisofia.fmi.electronicstore.service;

import bg.unisofia.fmi.electronicstore.dto.request.CreateOrderRequest;
import bg.unisofia.fmi.electronicstore.dto.request.OrderItemRequest;
import bg.unisofia.fmi.electronicstore.dto.response.OrderResponse;
import bg.unisofia.fmi.electronicstore.entity.*;
import bg.unisofia.fmi.electronicstore.exception.ConcurrentPurchaseException;
import bg.unisofia.fmi.electronicstore.exception.InsufficientStockException;
import bg.unisofia.fmi.electronicstore.exception.ResourceNotFoundException;
import bg.unisofia.fmi.electronicstore.mapper.OrderMapper;
import bg.unisofia.fmi.electronicstore.repository.OrderRepository;
import bg.unisofia.fmi.electronicstore.repository.ProductRepository;
import bg.unisofia.fmi.electronicstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
            .map(orderMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return orderMapper.toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId).stream()
            .map(orderMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId()));

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                    product.getName(),
                    itemRequest.getQuantity(),
                    product.getStockQuantity()
                );
            }

            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());

            try {
                productRepository.save(product);
            } catch (ObjectOptimisticLockingFailureException e) {
                throw new ConcurrentPurchaseException(
                    "Product " + product.getName() + " was purchased by another customer. Please try again."
                );
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());
            order.getItems().add(orderItem);
        }

        order.recalculateTotal();
        Order saved = orderRepository.save(order);
        return orderMapper.toResponse(saved);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (status == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(status);
        Order saved = orderRepository.save(order);
        return orderMapper.toResponse(saved);
    }
}
