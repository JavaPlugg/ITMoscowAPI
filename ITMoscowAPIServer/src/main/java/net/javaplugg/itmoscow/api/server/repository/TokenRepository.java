package net.javaplugg.itmoscow.api.server.repository;

import java.util.Optional;
import net.javaplugg.itmoscow.api.server.entity.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    Optional<TokenEntity> findByTokenId(String tokenId);
}
