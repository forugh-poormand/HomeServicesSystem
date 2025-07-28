package ir.maktab127.homeservicessystem.service;

import ir.maktab127.homeservicessystem.entity.Person;
import ir.maktab127.homeservicessystem.entity.Specialist;
import ir.maktab127.homeservicessystem.entity.VerificationToken;
import ir.maktab127.homeservicessystem.entity.enums.SpecialistStatus;
import ir.maktab127.homeservicessystem.exceptions.InvalidOperationException;
import ir.maktab127.homeservicessystem.exceptions.ResourceNotFoundException;
import ir.maktab127.homeservicessystem.repository.PersonRepository;
import ir.maktab127.homeservicessystem.repository.SpecialistRepository;
import ir.maktab127.homeservicessystem.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {
    private final PersonRepository personRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;
    private final SpecialistRepository specialistRepository;

    @Override
    @Transactional
    public void createAndSendVerificationCode(Person person) {

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, person);
        verificationTokenRepository.save(verificationToken);
        emailService.sendVerificationEmail(person.getEmail(), token);
    }

    @Override
    @Transactional
    public String verifyUser(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired token"));

        Person person = verificationToken.getPerson();
        if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {

            verificationTokenRepository.delete(verificationToken);
            throw new InvalidOperationException("verification token expired");
        }
person.setEmailVerified(true);
        if (person instanceof Specialist) {
            Specialist specialist = (Specialist) person;
            if(specialist.getProfilePicture()!=null){
                specialist.setStatus(SpecialistStatus.AWAITING_CONFIRMATION);
                specialistRepository.save(specialist);
            }else {
                personRepository.save(person);
            }
        }else {
            personRepository.save(person);
        }
        verificationTokenRepository.delete(verificationToken);
        return "Email verified successfully. You can login now";
    }
}
