package ir.maktab127.homeservicessystem.entity;

import ir.maktab127.homeservicessystem.entity.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "CustomerOrder")
public class CustomerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal proposedPrice;

    @Column(nullable = false)
    private String description;

    private LocalDateTime orderDate = LocalDateTime.now();

    private LocalDateTime requestedStartDate;

    private String address;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.WAITING_FOR_SUGGESTIONS;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subservice_id", nullable = false)
    private SubService subService;

    @ManyToOne(fetch = FetchType.LAZY)
    private Specialist selectedSpecialist;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<Suggestion> suggestions;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Comment comment;
}