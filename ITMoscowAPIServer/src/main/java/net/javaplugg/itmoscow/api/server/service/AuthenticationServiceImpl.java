package net.javaplugg.itmoscow.api.server.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.javaplugg.itmoscow.api.server.exception.MailAlreadyHasTokenException;
import net.javaplugg.itmoscow.api.server.exception.OTPAlreadyRequestedException;
import net.javaplugg.itmoscow.api.server.exception.OTPDoesNotExistException;
import net.javaplugg.itmoscow.api.server.properties.ITMoscowAPIServerProperties;
import net.javaplugg.itmoscow.api.server.util.SetCache;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final TokenPersistenceService tokenPersistenceService;
    private final HashingService hashingService;
    private final Cache<String, String> otpCache;
    private final SetCache<String> tokenCache;

    public AuthenticationServiceImpl(TokenPersistenceService tokenPersistenceService, HashingService hashingService, ITMoscowAPIServerProperties properties) {
        this.tokenPersistenceService = tokenPersistenceService;
        this.hashingService = hashingService;
        this.otpCache = Caffeine
                .newBuilder()
                .expireAfterWrite(properties.getOtpCacheLifetimeMinutes(), TimeUnit.MINUTES)
                .build();
        this.tokenCache = new SetCache<>(Caffeine
                .newBuilder()
                .expireAfterAccess(properties.getTokenCacheLifetimeMinutes(), TimeUnit.MINUTES)
                .build()
        );
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
        otpCache.invalidate(otp);
        if (tokenPersistenceService.getTokenByEmail(mail).isPresent()) {
            throw new MailAlreadyHasTokenException();
        }
        String tokenId = UUID.randomUUID().toString().replace("-", "");
        String random = (UUID.randomUUID().toString() + UUID.randomUUID()).replace("-", "");
        tokenPersistenceService.createToken(tokenId, mail, random);
        return tokenId + "/" + random;
    }

    @Override
    public Optional<String> extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }
        return Optional.of(authHeader.substring("Bearer ".length()));
    }

    @Override
    public boolean validateToken(String token) {
        if (tokenCache.contains(token)) {
            return true;
        }
        int separator = token.indexOf('/');
        if (separator == -1) {
            return false;
        }
        String random = token.substring(separator + 1);
        boolean valid = tokenPersistenceService
                .getTokenByRawToken(token)
                .map(tokenDao -> hashingService.verify(random, tokenDao.hash()))
                .orElse(false);
        if (valid) {
            tokenCache.add(token);
        }
        return valid;
    }

    @Override
    public void revokeToken(String token) {
        tokenCache.remove(token);
        int separator = token.indexOf('/');
        if (separator == -1) {
            return;
        }
        String tokenId = token.substring(0, separator);
        tokenPersistenceService.deleteTokenById(tokenId);
    }
}
