package za.co.heritagebaptist.forge.auth;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
@Service
public class JwtService {
    private final SecretKey key;
    public JwtService(@Value("${app.jwt-secret}") String secret) {
        key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    public String issue(AppUser user) {
        Instant now = Instant.now();
        return Jwts.builder().subject(user.getId().toString()).issuer("forge")
            .claim("role", user.getRole().name()).claim("name", user.getDisplayName())
            .issuedAt(Date.from(now)).expiration(Date.from(now.plusSeconds(900)))
            .signWith(key, Jwts.SIG.HS256).compact();
    }
    public AuthPrincipal parse(String token) {
        var claims = Jwts.parser().verifyWith(key).requireIssuer("forge").build().parseSignedClaims(token).getPayload();
        if (claims.getExpiration() == null) throw new AuthFailure();
        return new AuthPrincipal(UUID.fromString(claims.getSubject()), claims.get("name", String.class),
            Role.valueOf(claims.get("role", String.class)));
    }
}
