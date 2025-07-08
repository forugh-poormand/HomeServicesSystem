package ir.maktab127.homeservicessystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer score;

    @Lob
    private String text;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id", unique = true)
    private CustomerOrder order;
}
