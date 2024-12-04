package com.example.beskbd.dto.object;




import com.example.beskbd.entities.User;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
@Data
@AllArgsConstructor
@Getter
@Setter
public class CartDTO {
    private List<CartItemDTO> items;

    public CartDTO() {
        this.items = new ArrayList<>();
    }

    public CartDTO(String id, ArrayList<Object> objects) {
        this.items = new ArrayList<>();
        for (Object object : objects) {
            if (object instanceof CartItemDTO) {
                items.add((CartItemDTO) object);
            }
        }

    }

    public List<CartItemDTO> getItems() {
        return items;
    }
    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }
    public void getCart(User UserId) {

        List<CartItemDTO> cartItems = getCartItemsForUser(UserId);
    }

    private List<CartItemDTO> getCartItemsForUser(User userId) {
        return List.of(items.toArray(new CartItemDTO[0]));
    }

    public void clear() {
        items.clear();
    }
}
