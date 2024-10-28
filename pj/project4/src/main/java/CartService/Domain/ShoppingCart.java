package CartService.Domain;

import CommonLib.Entities;
import ProductService.Domain.Product;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

import java.util.Map;


@NoArgsConstructor

public class ShoppingCart {
    private Map<Product, Integer> items;

    public void addProduct(Product product, int quantity) {
        if (product.isAvailable() && product.getStockQuantity() >= quantity) {
            items.put(product, items.getOrDefault(product, 0) + quantity);
            product.updateStock(quantity);
        } else {
            throw new IllegalArgumentException("Product is not available in the desired quantity.");
        }
    }

    public void removeProduct(Product product) {
        items.remove(product);
    }

    public Map<Product, Integer> viewCart() {
        return items;
    }

    public void clearCart() {
        items.clear();
    }
}
