package ir.maktab127.homeservicessystem.entity;

import ir.maktab127.homeservicessystem.entity.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@PrimaryKeyJoinColumn(name = "id")
public class Customer extends Person {

    @OneToOne(cascade = CascadeType.ALL,orphanRemoval = true)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    private Wallet wallet;

    @OneToMany (mappedBy = "customer")
    private Set<CustomerOrder> orders = new HashSet<>();

    public Customer() {
        this.setRole(Role.ROLE_CUSTOMER);
    }
}
