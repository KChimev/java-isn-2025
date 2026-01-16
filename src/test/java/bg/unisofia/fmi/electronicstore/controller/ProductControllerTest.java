package bg.unisofia.fmi.electronicstore.controller;

import bg.unisofia.fmi.electronicstore.dto.request.CreateProductRequest;
import bg.unisofia.fmi.electronicstore.dto.response.ProductResponse;
import bg.unisofia.fmi.electronicstore.exception.ResourceNotFoundException;
import bg.unisofia.fmi.electronicstore.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductResponse productResponse;
    private CreateProductRequest createRequest;

    @BeforeEach
    void setUp() {
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
    void getAllProducts_ShouldReturnList() throws Exception {
        when(productService.getAllProducts()).thenReturn(List.of(productResponse));

        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    void getProductById_WhenExists_ShouldReturnProduct() throws Exception {
        when(productService.getProductById(1L)).thenReturn(productResponse);

        mockMvc.perform(get("/api/products/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void getProductById_WhenNotExists_ShouldReturn404() throws Exception {
        when(productService.getProductById(99L))
            .thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(get("/api/products/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_WithValidData_ShouldReturn201() throws Exception {
        when(productService.createProduct(any(CreateProductRequest.class)))
            .thenReturn(productResponse);

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Laptop"));
    }

    @Test
    void createProduct_WithInvalidData_ShouldReturn400() throws Exception {
        CreateProductRequest invalidRequest = new CreateProductRequest();

        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void searchByName_ShouldReturnMatchingProducts() throws Exception {
        when(productService.searchByName("lap")).thenReturn(List.of(productResponse));

        mockMvc.perform(get("/api/products/search").param("name", "lap"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    void findByPriceRange_ShouldReturnMatchingProducts() throws Exception {
        when(productService.findByPriceRange(BigDecimal.valueOf(500), BigDecimal.valueOf(1500)))
            .thenReturn(List.of(productResponse));

        mockMvc.perform(get("/api/products/price-range")
                .param("minPrice", "500")
                .param("maxPrice", "1500"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Laptop"));
    }

    @Test
    void deleteProduct_ShouldReturn204() throws Exception {
        doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/products/1"))
            .andExpect(status().isNoContent());

        verify(productService, times(1)).deleteProduct(1L);
    }
}
