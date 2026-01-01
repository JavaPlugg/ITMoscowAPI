package net.javaplugg.itmoscow.api.server.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import net.javaplugg.itmoscow.api.server.dao.TokenDao;
import net.javaplugg.itmoscow.api.server.entity.TokenEntity;
import net.javaplugg.itmoscow.api.server.repository.TokenRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenPersistenceServiceImpl implements TokenPersistenceService {

    private final HashingService hashingService;
    private final TokenRepository tokenRepository;

    @Override
    public TokenDao createToken(String tokenId, String email, String random) {
        String hash = hashingService.hash(random);
        TokenEntity tokenEntity = new TokenEntity(tokenId, email, hash);
        TokenEntity savedToken = tokenRepository.save(tokenEntity);
        return mapToDao(savedToken);
    }

    @Override
    public Optional<TokenDao> getTokenById(String tokenId) {
        return tokenRepository.findByTokenId(tokenId).map(this::mapToDao);
    }

    private TokenDao mapToDao(TokenEntity tokenEntity) {
        return new TokenDao(tokenEntity.getTokenId(), tokenEntity.getCreatedAt(), tokenEntity.getEmail(), tokenEntity.getHash());
    }
}
