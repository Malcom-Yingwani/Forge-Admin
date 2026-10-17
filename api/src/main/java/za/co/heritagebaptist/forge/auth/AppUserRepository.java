package za.co.heritagebaptist.forge.auth;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    java.util.Optional<AppUser> findByEmailIgnoreCase(String email);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select u from AppUser u where u.id = :id")
    java.util.Optional<AppUser> lockById(UUID id);
}
