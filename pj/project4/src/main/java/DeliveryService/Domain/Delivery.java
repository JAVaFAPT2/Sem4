package DeliveryService.Domain;

import CommonLib.Entities;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
public class Delivery extends Entities {

    private String trackingNumber;
    private String carrier;
    private String address;
    private String shippingStatus;


    public Delivery(int id, LocalDateTime createDate, LocalDateTime updateDate, String trackingNumber, String carrier, String address) {
        super(id, createDate, updateDate);
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
        this.address = address;
        this.shippingStatus = "Pending";
    }

    public void updateStatus(String status) {
        this.shippingStatus = status;
    }

}

