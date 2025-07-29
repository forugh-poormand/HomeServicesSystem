package ir.maktab127.homeservicessystem.service;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class ConsoleEmailVerificationImpl implements EmailService{
    private static final Logger logger= (Logger) LoggerFactory.getLogger(ConsoleEmailVerificationImpl.class);
    @Override
    public void sendVerificationEmail(String to, String token) {
     String verificationLink ="http://localhost:8080/auth/verify?token="+token;
     logger.info("=========================================");
     logger.info("SIMULATING eMAIL SEND");
     logger.info("To: {}", to);
        logger.info("Subject: Please verify your email address");
        logger.info("Click the link to verify: {}", verificationLink);
        logger.info("======================================================");
    }
    }
