package net.javaplugg.itmoscow.api.server.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class HashingServiceImpl implements HashingService {

    private final BCrypt.Hasher hasher = BCrypt.withDefaults();
    private final BCrypt.Verifyer verifyer = BCrypt.verifyer();

    @Override
    public String hash(String password) {
        return hasher.hashToString(12, password.toCharArray());
    }

    @Override
    public boolean verify(String password, String hash) {
        return verifyer.verify(password.toCharArray(), hash).verified;
    }
}
