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

    @Lob
    private String text;


    @OneToOne
    @JoinColumn(name = "order_id",referencedColumnName = "id",updatable = false)
    private CustomerOrder order;

}
