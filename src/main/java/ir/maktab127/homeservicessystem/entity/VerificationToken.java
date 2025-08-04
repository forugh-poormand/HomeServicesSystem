package ir.maktab127.homeservicessystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false,name="person_id")
    private Person person;

    @Column(nullable = false)
    LocalDateTime expiryDate;

    public VerificationToken(String token, Person person) {
        this.token = token;
        this.person = person;
        this.expiryDate = LocalDateTime.now().plusMinutes(3);
    }
}
