package test.project4v2.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "delivery")
public class Delivery extends Entities {

    private String trackingNumber;
    private String carrier;
    private String address;
    private String shippingStatus;

    public void updateStatus(String status) {
        this.shippingStatus = status;
    }

}

