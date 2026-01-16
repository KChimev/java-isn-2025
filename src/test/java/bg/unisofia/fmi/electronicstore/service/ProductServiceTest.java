package bg.unisofia.fmi.electronicstore.service;

import bg.unisofia.fmi.electronicstore.dto.request.CreateProductRequest;
import bg.unisofia.fmi.electronicstore.dto.response.ProductResponse;
import bg.unisofia.fmi.electronicstore.entity.Product;
import bg.unisofia.fmi.electronicstore.exception.ResourceNotFoundException;
import bg.unisofia.fmi.electronicstore.mapper.ProductMapper;
import bg.unisofia.fmi.electronicstore.repository.CategoryRepository;
import bg.unisofia.fmi.electronicstore.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductResponse productResponse;
    private CreateProductRequest createRequest;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(999.99));
        product.setStockQuantity(10);

        productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("Laptop");
        productResponse.setPrice(BigDecimal.valueOf(999.99));
        productResponse.setStockQuantity(10);

        createRequest = new CreateProductRequest();
        createRequest.setName("Laptop");
        createRequest.setPrice(BigDecimal.valueOf(999.99));
        createRequest.setStockQuantity(10);
    }

    @Test
    void getAllProducts_ShouldReturnList() {
        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toResponse(any(Product.class))).thenReturn(productResponse);
        when(productRepository.getAverageRating(1L)).thenReturn(4.5);

        List<ProductResponse> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_WhenExists_ShouldReturnProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);
        when(productRepository.getAverageRating(1L)).thenReturn(4.5);

        ProductResponse result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
    }

    @Test
    void getProductById_WhenNotExists_ShouldThrowException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void createProduct_ShouldSaveAndReturn() {
        when(productMapper.toEntity(createRequest)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toResponse(product)).thenReturn(productResponse);
        when(productRepository.getAverageRating(1L)).thenReturn(null);

        ProductResponse result = productService.createProduct(createRequest);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void searchByName_ShouldReturnMatchingProducts() {
        when(productRepository.findByNameContainingIgnoreCase("lap")).thenReturn(List.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);
        when(productRepository.getAverageRating(1L)).thenReturn(4.0);

        List<ProductResponse> result = productService.searchByName("lap");

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void findByPriceRange_ShouldReturnMatchingProducts() {
        when(productRepository.findByPriceRange(BigDecimal.valueOf(500), BigDecimal.valueOf(1500)))
            .thenReturn(List.of(product));
        when(productMapper.toResponse(product)).thenReturn(productResponse);
        when(productRepository.getAverageRating(1L)).thenReturn(4.0);

        List<ProductResponse> result = productService.findByPriceRange(
            BigDecimal.valueOf(500), BigDecimal.valueOf(1500));

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void deleteProduct_WhenExists_ShouldDelete() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteProduct_WhenNotExists_ShouldThrowException() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(99L));
    }
}
