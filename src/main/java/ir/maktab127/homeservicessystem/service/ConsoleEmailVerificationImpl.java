package ir.maktab127.homeservicessystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConsoleEmailVerificationImpl implements EmailService {

    private static final Logger logger =
            LoggerFactory.getLogger(ConsoleEmailVerificationImpl.class);

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @Override
    public void sendVerificationEmail(String to, String token) {
        String verificationLink =
                baseUrl + "/api/auth/verify?token=" + token;

        logger.info("=========================================");
        logger.info("SIMULATING EMAIL SEND");
        logger.info("To: {}", to);
        logger.info("Subject: Please verify your email address");
        logger.info("Click the link to verify: {}", verificationLink);
        logger.info("=========================================");
    }
}