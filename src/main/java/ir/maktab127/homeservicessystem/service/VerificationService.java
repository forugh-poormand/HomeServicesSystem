package ir.maktab127.homeservicessystem.service;

import ir.maktab127.homeservicessystem.entity.Person;

public interface VerificationService {

    void createAndSendVerificationCode(Person person);
    String verifyUser(String token);
}
