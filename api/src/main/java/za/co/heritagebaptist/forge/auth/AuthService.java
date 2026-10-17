package za.co.heritagebaptist.forge.auth;
import java.time.Instant;
import java.util.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class AuthService {
    private final AppUserRepository users;
    private final RefreshTokenRepository tokens;
    private final PasswordEncoder passwords;
    private final LoginThrottle throttle;
    private final String dummyHash;
    public AuthService(AppUserRepository users, RefreshTokenRepository tokens, PasswordEncoder passwords, LoginThrottle throttle) {
        this.users = users; this.tokens = tokens; this.passwords = passwords; this.throttle = throttle;
        dummyHash = passwords.encode(TokenSecrets.random());
    }
    @Transactional
    public AppUser login(String email, String password, String ip) {
        email = email.trim().toLowerCase(Locale.ROOT); throttle.check(email, ip);
        var user = users.findByEmailIgnoreCase(email).orElse(null);
        boolean matches = passwords.matches(password, user == null ? dummyHash : user.getPasswordHash());
        if (user == null || !matches || !user.isEnabled()) { throttle.failed(email, ip); throw new AuthFailure(); }
        throttle.success(email); user.setLastLoginAt(Instant.now()); return user;
    }
    @Transactional(readOnly = true)
    public AppUser me(UUID id) { return users.findById(id).filter(AppUser::isEnabled).orElseThrow(AuthFailure::new); }
    @Transactional
    public AppUser update(UUID id, AuthController.Profile request) {
        var user = users.lockById(id).filter(AppUser::isEnabled).orElseThrow(AuthFailure::new);
        if (!passwords.matches(request.currentPassword(), user.getPasswordHash())) throw new AuthFailure();
        String email = request.email().trim().toLowerCase(Locale.ROOT);
        if (users.findByEmailIgnoreCase(email).filter(u -> !u.getId().equals(id)).isPresent())
            throw new org.springframework.dao.DataIntegrityViolationException("Email already in use");
        user.setDisplayName(request.name().trim()); user.setEmail(email);
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwords.encode(request.password())); tokens.revokeAll(id);
        }
        return user;
    }
}
