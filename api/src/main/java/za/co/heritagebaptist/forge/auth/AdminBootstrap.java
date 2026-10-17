package za.co.heritagebaptist.forge.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminBootstrap implements ApplicationRunner {
    private final AppUserRepository users;
    private final PasswordEncoder passwords;
    private final String email;
    private final String password;
    public AdminBootstrap(AppUserRepository users, PasswordEncoder passwords,
            @Value("${app.admin-email:}") String email, @Value("${app.admin-password:}") String password) {
        this.users = users; this.passwords = passwords; this.email = email; this.password = password;
    }
    @Override @Transactional
    public void run(ApplicationArguments args) {
        if (users.count() != 0 || email.isBlank() || password.isBlank()) return;
        var user = new AppUser();
        user.setEmail(email.trim().toLowerCase(java.util.Locale.ROOT)); user.setDisplayName("Admin");
        user.setPasswordHash(passwords.encode(password)); user.setRole(Role.ADMIN); user.setEnabled(true);
        users.save(user);
    }
}
