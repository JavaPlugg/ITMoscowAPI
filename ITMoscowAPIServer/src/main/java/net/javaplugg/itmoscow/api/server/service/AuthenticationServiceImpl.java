package net.javaplugg.itmoscow.api.server.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.javaplugg.itmoscow.api.server.exception.OTPAlreadyRequestedException;
import net.javaplugg.itmoscow.api.server.exception.OTPDoesNotExistException;
import net.javaplugg.itmoscow.api.server.properties.ITMoscowAPIServerProperties;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final TokenPersistenceService tokenPersistenceService;
    private final HashingService hashingService;
    private final Cache<String, String> otpCache;

    public AuthenticationServiceImpl(TokenPersistenceService tokenPersistenceService, HashingService hashingService, ITMoscowAPIServerProperties properties) {
        this.tokenPersistenceService = tokenPersistenceService;
        this.hashingService = hashingService;
        this.otpCache = Caffeine
                .newBuilder()
                .expireAfterWrite(properties.getOtpLifetimeMinutes(), TimeUnit.MINUTES)
                .build();
    }

    @Override
    public String generateOTP(String mail) {
        String formattedMail = mail.toLowerCase().trim();
        boolean exists = otpCache
                .asMap()
                .values()
                .stream()
                .anyMatch(email -> email.equals(formattedMail));
        if (exists) {
            throw new OTPAlreadyRequestedException();
        }
        String otp = UUID.randomUUID().toString().replace("-", "");
        otpCache.put(otp, formattedMail);
        return otp;
    }

    @Override
    public String generateToken(String otp) {
        String mail = otpCache.getIfPresent(otp);
        if (mail == null) {
            throw new OTPDoesNotExistException();
        }
        String tokenId = UUID.randomUUID().toString().replace("-", "");
        String random = (UUID.randomUUID().toString() + UUID.randomUUID() + UUID.randomUUID()).replace("-", "");
        tokenPersistenceService.createToken(tokenId, mail, random);
        return tokenId + "/" + random;
    }

    @Override
    public boolean validateToken(String token) {
        int separator = token.indexOf('/');
        if (separator == -1) {
            return false;
        }
        String tokenId = token.substring(0, separator);
        String random = token.substring(separator + 1);
        return tokenPersistenceService
                .getTokenById(tokenId)
                .map(tokenDao -> hashingService.verify(random, tokenDao.hash()))
                .orElse(false);
    }
}
