package CommonLib;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;

import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public abstract class Entities {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "created_at")
    private LocalDateTime CreateDate;
    @Column(name = "updated_at")
    private LocalDateTime UpdateDate;

    protected Entities(int id) {
        this.id = id;
        this.CreateDate = LocalDateTime.now();
        this.UpdateDate = LocalDateTime.now();
    }

    public void setUpdateDate() {
        UpdateDate = LocalDateTime.now();
    }
}
