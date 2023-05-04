package metaverse.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "test")
public class Test {
    @Id
    private Long no;
    private String id;
    private String pw;
}
