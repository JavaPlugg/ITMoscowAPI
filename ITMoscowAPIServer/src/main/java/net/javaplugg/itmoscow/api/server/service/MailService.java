package net.javaplugg.itmoscow.api.server.service;

public interface MailService {

    /**
     * Отправляет письмо на электронную почту
     * @param to получатель
     * @param subject тема
     * @param text текст
     */
    void send(String to, String subject, String text);
}
