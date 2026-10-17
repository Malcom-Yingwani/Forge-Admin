package za.co.heritagebaptist.forge.auth;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository tokens;
    private final AppUserRepository users;
    @jakarta.persistence.PersistenceContext private jakarta.persistence.EntityManager entityManager;
    public RefreshTokenService(RefreshTokenRepository tokens, AppUserRepository users) { this.tokens = tokens; this.users = users; }
    @Transactional
    public String issue(AppUser user) {
        String secret = TokenSecrets.random();
        var token = new RefreshToken(); token.setUser(user); token.setTokenHash(TokenSecrets.hash(secret));
        token.setExpiresAt(Instant.now().plusSeconds(604800)); tokens.save(token); return secret;
    }
    @Transactional(noRollbackFor = AuthFailure.class)
    public Rotation rotate(String secret) {
        if (secret == null) throw new AuthFailure();
        var token = tokens.findByTokenHash(TokenSecrets.hash(secret)).orElseThrow(AuthFailure::new);
        var user = users.lockById(token.getUser().getId()).orElseThrow(AuthFailure::new);
        entityManager.refresh(token);
        if (token.isRevoked()) { tokens.revokeAll(user.getId()); throw new AuthFailure(); }
        if (!user.isEnabled() || !token.getExpiresAt().isAfter(Instant.now())) throw new AuthFailure();
        token.setRevoked(true); return new Rotation(user, issue(user));
    }
    @Transactional
    public void logout(String secret) {
        if (secret != null) tokens.findByTokenHash(TokenSecrets.hash(secret)).ifPresent(t -> t.setRevoked(true));
    }
    public record Rotation(AppUser user, String token) {}
}
