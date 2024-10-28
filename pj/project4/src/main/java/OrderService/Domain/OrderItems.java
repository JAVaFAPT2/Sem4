package OrderService.Domain;

import CommonLib.Entities;
import ProductService.Domain.Product;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class OrderItems extends Entities {
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private int quantity;


    public OrderItems(int id, LocalDateTime createDate, LocalDateTime updateDate, Product product, int quantity) {
        if (quantity <= 0) {

            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }
        setId(id);
        setCreateDate(createDate);
        setUpdateDate(updateDate);
        this.product = product;
        this.quantity = quantity;
    }

}