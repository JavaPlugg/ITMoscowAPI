package net.javaplugg.itmoscow.api.server.dao;

import java.time.LocalDateTime;

public record TokenDao(String tokenId, LocalDateTime createdAt, String email, String hash) {
}
