package za.co.heritagebaptist.forge.auth;

import jakarta.persistence.*;
import java.time.Instant;
import za.co.heritagebaptist.forge.common.BaseEntity;

@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
    @Column(nullable = false, unique = true) private String tokenHash;
    @Column(nullable = false) private Instant expiresAt;
    @Column(nullable = false) private boolean used;
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String hash) { tokenHash = hash; }
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expires) { expiresAt = expires; }
    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}
