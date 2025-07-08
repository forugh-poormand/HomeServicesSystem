package ir.maktab127.homeservicessystem.entity;

import ir.maktab127.homeservicessystem.entity.enums.SpecialistStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
public class Specialist extends Person {


    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    private Wallet wallet;

    @Enumerated(EnumType.STRING)
    private SpecialistStatus status = SpecialistStatus.AWAITING_CONFIRMATION;

    @Lob
    private byte[] profilePicture;

    private long totalScore = 0;
    private int reviewCount = 0;

    @ManyToMany
    @JoinTable(
            name = "specialist_subservice",
            joinColumns = @JoinColumn(name = "specialist_id"),
            inverseJoinColumns = @JoinColumn(name = "subservice_id")
    )
    private Set<SubService> expertIn = new HashSet<>();

    @OneToMany(mappedBy = "specialist")
    private Set<Suggestion> suggestions;

    @OneToMany(mappedBy = "selectedSpecialist")
    private Set<CustomerOrder> orders = new HashSet<>();

    public Double getScore() {
        if (reviewCount == 0) {
            return 0.0;
        }
        return (double) totalScore / reviewCount;
    }


}
