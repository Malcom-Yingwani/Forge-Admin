package za.co.heritagebaptist.forge.auth;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByTokenHash(String hash);
    @Modifying
    @Query("update PasswordResetToken t set t.used = true where t.user.id = :id")
    void invalidateAll(UUID id);
}
