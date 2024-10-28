package ReviewService.Domain;


import CommonLib.Entities;
import AccountService.Domain.User;
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
@Entity
@NoArgsConstructor
public class Review extends Entities {

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @OneToOne
    @JoinColumn(name = "customer_id")
    private User user;
    private int rating; // Từ 1 đến 5
    private String comment;


    public Review
            (int id, LocalDateTime createDate, LocalDateTime updateDate,
             Product product, User user, int rating, String comment) {
        super(id, createDate, updateDate);
        this.product = product;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
    }

}
