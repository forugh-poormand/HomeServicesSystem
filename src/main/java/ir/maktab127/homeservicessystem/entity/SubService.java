package ir.maktab127.homeservicessystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "mainService_id"})
})
public class SubService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal basePrice;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mainService_id", nullable = false)
    private MainService mainService;

    @ManyToMany(mappedBy = "expertIn")
    private Set<Specialist> specialists;
}
