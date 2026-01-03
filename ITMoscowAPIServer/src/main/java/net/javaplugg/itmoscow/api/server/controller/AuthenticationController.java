package net.javaplugg.itmoscow.api.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javaplugg.itmoscow.api.dto.ErrorResponse;
import net.javaplugg.itmoscow.api.dto.auth.RequestOTPRequest;
import net.javaplugg.itmoscow.api.dto.auth.RequestTokenRequest;
import net.javaplugg.itmoscow.api.server.exception.MailAlreadyHasTokenException;
import net.javaplugg.itmoscow.api.server.exception.MailException;
import net.javaplugg.itmoscow.api.server.exception.OTPAlreadyRequestedException;
import net.javaplugg.itmoscow.api.server.exception.OTPDoesNotExistException;
import net.javaplugg.itmoscow.api.server.service.AuthenticationService;
import net.javaplugg.itmoscow.api.server.service.MailService;
import net.javaplugg.itmoscow.api.server.service.TokenPersistenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/itmoscow/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    public static final String OTP_MESSAGE_SUBJECT = "ITMoscow API Token";
    public static final String OTP_MESSAGE_TEXT = """
            Вы получили это письмо, поскольку был отправлен запрос на получение одноразового пароля для выдачи токена.
            Проигнорируйте это письмо, если запрос был отправлен не вами.
                        
            Одноразовый пароль:
            %s
            """;
    public static final String TOKEN_MESSAGE_SUBJECT = "ITMoscow API Token";
    public static final String TOKEN_MESSAGE_TEXT = """
            Вы получили это письмо, поскольку был отправлен запрос на получение токена.
            Проигнорируйте это письмо, если запрос был отправлен не вами.
                        
            Токен:
            %s
            """;
    public static final String ERROR_GENERAL = "Возникла внутренняя ошибка сервера";

    private final AuthenticationService authenticationService;
    private final TokenPersistenceService tokenPersistenceService;
    private final MailService mailService;

    @PostMapping("/otp")
    public ResponseEntity<?> otp(@RequestBody RequestOTPRequest request) {
        String email = request.email().toLowerCase().trim();
        try {
            String otp = authenticationService.generateOTP(email);
            mailService.send(email, OTP_MESSAGE_SUBJECT, OTP_MESSAGE_TEXT.formatted(otp));
            return ResponseEntity.ok().build();
        } catch (OTPAlreadyRequestedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("""
                    Одноразовый пароль уже был запрошен для этой почты. Повторите попытку позже
                    """));
        } catch (MailException e) {
            log.error("Failed to send an email", e);
            return ResponseEntity.internalServerError().body(new ErrorResponse("""
                    Не удалось отправить электронное письмо с одноразовым паролем. Повторите попытку позже
                    """));
        } catch (Exception e) {
            log.error("Failed to generate OTP", e);
            return ResponseEntity.internalServerError().body(new ErrorResponse(ERROR_GENERAL));
        }
    }

    @PostMapping("/token")
    public ResponseEntity<?> token(@RequestBody RequestTokenRequest request) {
        String otp = request.otp();
        String token = "";
        try {
            token = authenticationService.generateToken(otp);
            String email = tokenPersistenceService
                    .getTokenByRawToken(token)
                    .orElseThrow()
                    .email();
            mailService.send(email, TOKEN_MESSAGE_SUBJECT, TOKEN_MESSAGE_TEXT.formatted(token));
            return ResponseEntity.ok().build();
        } catch (MailException e) {
            log.error("Failed to send an email", e);
            authenticationService.revokeToken(token);
            return ResponseEntity.internalServerError().body(new ErrorResponse("""
                    Не удалось отправить письмо с токеном. Токен был отозван
                    """));
        } catch (OTPDoesNotExistException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse("""
                    Неверный одноразовый пароль
                    """));
        } catch (MailAlreadyHasTokenException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("""
                    Токен для указанной почты уже существует
                    """));
        } catch (Exception e) {
            log.error("Failed to generate token", e);
            return ResponseEntity.internalServerError().body(new ErrorResponse(ERROR_GENERAL));
        }
    }
}
