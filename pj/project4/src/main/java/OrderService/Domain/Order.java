package OrderService.Domain;

import CommonLib.Entities;
import AccountService.Domain.User;
import DeliveryService.Domain.Delivery;
import PromotionService.Domain.Promotion;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
public class Order extends Entities {

    @OneToOne
    @JoinColumn(name = "customer_id")
    private User user;
    @OneToMany
    @JoinColumn(name = "delivery_info_id")
    private List<OrderItems> products;
    @OneToOne
    @JoinColumn(name = "delivery_info_id")
    private Delivery deliveryInfo;
    @OneToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    public Order
            (int id, LocalDateTime createDate, LocalDateTime updateDate, User user,
             List<OrderItems> products, Delivery deliveryInfo, Promotion promotion) {
        super(id, createDate, updateDate);
        this.user = user;
        this.products = products;
        this.deliveryInfo = deliveryInfo;
        this.promotion = promotion;
    }

    public String getOrderSummary() {
        return "Order ID: " + getId() + ", Customer: " + user.getName() + ", Total Amount: " + calculateTotal();
    }

    public double calculateTotal() {
        double total = products.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        if (promotion != null) {
            total = promotion.applyDiscount(total);
        }
        return total;
    }

}




