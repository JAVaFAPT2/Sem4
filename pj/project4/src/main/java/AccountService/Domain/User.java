package AccountService.Domain;

import CommonLib.Entities;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.sql.Date;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "User")

public class User extends Entities {

    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;

    public User
            (int id, LocalDateTime createDate, LocalDateTime updateDate, String name, String address) {
        super(id, createDate, updateDate);
        this.name = name;
        this.address = address;
    }

    public User
            (int id, String name, String address) {
        super(id);
        this.name = name;
        this.address = address;
    }

}
