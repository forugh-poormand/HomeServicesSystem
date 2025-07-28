package ir.maktab127.homeservicessystem.service;

public interface EmailService {
    void sendVerificationEmail(String to,String token);
}
