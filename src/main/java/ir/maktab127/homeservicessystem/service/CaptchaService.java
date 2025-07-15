package ir.maktab127.homeservicessystem.service;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class CaptchaService {
    public String generateCaptcha() {
        Random random = new Random();
        int number = 10000 + random.nextInt(90000); // Generate a 5-digit number
        return String.valueOf(number);
    }
}
