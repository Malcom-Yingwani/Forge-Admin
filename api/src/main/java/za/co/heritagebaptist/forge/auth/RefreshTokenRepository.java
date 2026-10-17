package za.co.heritagebaptist.forge.auth;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    java.util.Optional<RefreshToken> findByTokenHash(String hash);
    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("update RefreshToken t set t.revoked = true where t.user.id = :userId")
    void revokeAll(UUID userId);
}
