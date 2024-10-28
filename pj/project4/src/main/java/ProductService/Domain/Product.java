package ProductService.Domain;

import CommonLib.Entities;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Product extends Entities {
    private String name;
    private String description;
    private double price;
    private int stockQuantity;


    public Product
            (int id, LocalDateTime createDate, LocalDateTime updateDate,
             String name, String description, double price, int stockQuantity) {
        super(id, createDate, updateDate);
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public void updateStock(int quantity) {
        this.stockQuantity -= quantity;
    }

    public boolean isAvailable() {
        return stockQuantity > 0;
    }

}
