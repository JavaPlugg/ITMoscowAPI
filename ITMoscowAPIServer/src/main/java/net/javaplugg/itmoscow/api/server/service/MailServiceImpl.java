package net.javaplugg.itmoscow.api.server.service;

import lombok.RequiredArgsConstructor;
import net.javaplugg.itmoscow.api.server.exception.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mail;

    @Override
    public void send(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mail.send(message);
        } catch (Exception e) {
            throw new MailException(e);
        }
    }
}
