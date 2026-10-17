package za.co.heritagebaptist.forge.auth;

import java.time.Instant;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetService {
    private final AppUserRepository users;
    private final PasswordResetTokenRepository resets;
    private final RefreshTokenRepository refresh;
    private final PasswordEncoder passwords;
    private final JavaMailSender mail;
    private final String resetUrl;
    private final String from;
    public PasswordResetService(AppUserRepository users, PasswordResetTokenRepository resets,
            RefreshTokenRepository refresh, PasswordEncoder passwords, JavaMailSender mail,
            @Value("${app.password-reset-url}") String resetUrl, @Value("${app.mail-from}") String from) {
        this.users = users; this.resets = resets; this.refresh = refresh;
        this.passwords = passwords; this.mail = mail; this.resetUrl = resetUrl; this.from = from;
    }
    @Transactional
    public void forgot(String email) {
        users.findByEmailIgnoreCase(email.trim().toLowerCase(Locale.ROOT)).filter(AppUser::isEnabled).ifPresent(found -> {
            var user = users.lockById(found.getId()).orElseThrow(AuthFailure::new);
            resets.invalidateAll(user.getId());
            String secret = TokenSecrets.random();
            var token = new PasswordResetToken();
            token.setUser(user); token.setTokenHash(TokenSecrets.hash(secret));
            token.setExpiresAt(Instant.now().plusSeconds(3600)); resets.save(token);
            var message = new SimpleMailMessage();
            message.setFrom(from); message.setTo(user.getEmail()); message.setSubject("Reset your Forge password");
            message.setText("Reset your password using this single-use link (valid for one hour):\n" + resetUrl + "?token=" + secret);
            // SMTP failures must not reveal whether an account exists.
            try { mail.send(message); } catch (org.springframework.mail.MailException ignored) { }
        });
    }
    @jakarta.persistence.PersistenceContext private jakarta.persistence.EntityManager entityManager;
    @Transactional
    public void reset(String secret, String password) {
        var token = resets.findByTokenHash(TokenSecrets.hash(secret)).orElseThrow(AuthFailure::new);
        var user = users.lockById(token.getUser().getId()).filter(AppUser::isEnabled).orElseThrow(AuthFailure::new);
        entityManager.refresh(token);
        if (token.isUsed() || !token.getExpiresAt().isAfter(Instant.now())) throw new AuthFailure();
        user.setPasswordHash(passwords.encode(password));
        resets.invalidateAll(user.getId()); refresh.revokeAll(user.getId());
    }
}
