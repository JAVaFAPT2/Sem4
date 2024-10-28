package OrderService.Domain;

import AccountService.Domain.User;

import java.util.ArrayList;
import java.util.List;

public class OrderHistory {
    private final List<Order> orders;

    public OrderHistory() {
        orders = new ArrayList<>();
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public List<Order> getOrdersByCustomer(User user) {
        return orders.stream()
                .filter(order -> order.getUser().equals(user))
                .toList();
    }
}
