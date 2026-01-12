package bg.unisofia.fmi.electronicstore.service;

import bg.unisofia.fmi.electronicstore.dto.request.CreateProductRequest;
import bg.unisofia.fmi.electronicstore.dto.request.UpdateProductRequest;
import bg.unisofia.fmi.electronicstore.dto.response.ProductResponse;
import bg.unisofia.fmi.electronicstore.entity.Category;
import bg.unisofia.fmi.electronicstore.entity.Product;
import bg.unisofia.fmi.electronicstore.exception.ResourceNotFoundException;
import bg.unisofia.fmi.electronicstore.mapper.ProductMapper;
import bg.unisofia.fmi.electronicstore.repository.CategoryRepository;
import bg.unisofia.fmi.electronicstore.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
            .map(this::mapWithRating)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapWithRating(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
            .map(this::mapWithRating)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice).stream()
            .map(this::mapWithRating)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
            .map(this::mapWithRating)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAvailable() {
        return productRepository.findAvailableProducts().stream()
            .map(this::mapWithRating)
            .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = productMapper.toEntity(request);

        if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.getCategoryIds()));
            product.setCategories(categories);
        }

        Product saved = productRepository.save(product);
        return mapWithRating(saved);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }
        if (request.getCategoryIds() != null) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.getCategoryIds()));
            product.setCategories(categories);
        }

        Product saved = productRepository.save(product);
        return mapWithRating(saved);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    private ProductResponse mapWithRating(Product product) {
        ProductResponse response = productMapper.toResponse(product);
        Double avgRating = productRepository.getAverageRating(product.getId());
        response.setAverageRating(avgRating);
        return response;
    }
}
