package net.javaplugg.itmoscow.api.server.service;

public interface HashingService {

    String hash(String password);

    boolean verify(String password, String hash);
}
