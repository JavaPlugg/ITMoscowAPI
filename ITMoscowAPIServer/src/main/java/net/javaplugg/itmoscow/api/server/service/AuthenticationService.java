package net.javaplugg.itmoscow.api.server.service;

public interface AuthenticationService {

    String generateOTP(String mail);

    String generateToken(String otp);

    boolean validateToken(String token);
}
