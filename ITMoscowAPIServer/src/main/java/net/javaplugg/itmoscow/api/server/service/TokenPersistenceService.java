package net.javaplugg.itmoscow.api.server.service;

import java.util.Optional;
import net.javaplugg.itmoscow.api.server.dao.TokenDao;

public interface TokenPersistenceService {

    TokenDao createToken(String tokenId, String email, String random);

    Optional<TokenDao> getTokenById(String tokenId);
}
