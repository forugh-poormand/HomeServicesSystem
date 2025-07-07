package ir.maktab127.homeservicessystem.entity;

import ir.maktab127.homeservicessystem.entity.enums.SpecialistStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "specialist_subservice",
            joinColumns = @JoinColumn(name = "specialist_id"),
            inverseJoinColumns = @JoinColumn(name = "subservice_id")
    )
    private Set<SubService> expertIn;

    @OneToMany(mappedBy = "spetialist")
    private Set<Suggestion> suggestions;
}
