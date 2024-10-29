package test.project4v2.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import test.project4v2.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(String category);

    List<Product> findByPriceBetween(double minPrice, double maxPrice);

    @Query("SELECT p FROM Product p JOIN ProductCategory pc ON p.productId = pc.product.productId WHERE pc.category.categoryId = ?")
    List<Product> findProductsByCategoryId(Long categoryId);

    Optional<Product> findById(Long productId);
}