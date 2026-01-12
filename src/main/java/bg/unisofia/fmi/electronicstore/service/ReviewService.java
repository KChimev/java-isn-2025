package bg.unisofia.fmi.electronicstore.service;

import bg.unisofia.fmi.electronicstore.dto.request.CreateReviewRequest;
import bg.unisofia.fmi.electronicstore.dto.response.ReviewResponse;
import bg.unisofia.fmi.electronicstore.entity.Product;
import bg.unisofia.fmi.electronicstore.entity.Review;
import bg.unisofia.fmi.electronicstore.entity.User;
import bg.unisofia.fmi.electronicstore.exception.DuplicateResourceException;
import bg.unisofia.fmi.electronicstore.exception.ResourceNotFoundException;
import bg.unisofia.fmi.electronicstore.mapper.ReviewMapper;
import bg.unisofia.fmi.electronicstore.repository.ProductRepository;
import bg.unisofia.fmi.electronicstore.repository.ReviewRepository;
import bg.unisofia.fmi.electronicstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewMapper reviewMapper;

    @Transactional(readOnly = true)
    public List<ReviewResponse> getAllReviews() {
        return reviewRepository.findAll().stream()
            .map(reviewMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReviewResponse getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + id));
        return reviewMapper.toResponse(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByProduct(Long productId) {
        return reviewRepository.findByProductId(productId).stream()
            .map(reviewMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId).stream()
            .map(reviewMapper::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) {
        if (reviewRepository.existsByUserIdAndProductId(request.getUserId(), request.getProductId())) {
            throw new DuplicateResourceException("User already reviewed this product");
        }

        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review saved = reviewRepository.save(review);
        return reviewMapper.toResponse(saved);
    }

    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new ResourceNotFoundException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }
}
