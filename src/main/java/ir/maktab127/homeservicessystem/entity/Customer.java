package ir.maktab127.homeservicessystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Setter
@Getter
public class Customer extends Person {

    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    private Wallet wallet;

    @OneToMany (mappedBy = "customer")
    private Set<CustomerOrder> orders;

    private Set<Comment> comments;
}
