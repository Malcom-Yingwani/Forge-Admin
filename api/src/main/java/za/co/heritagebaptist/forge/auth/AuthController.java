package za.co.heritagebaptist.forge.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.Duration;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService auth;
    private final JwtService jwt;
    private final RefreshTokenService refresh;
    private final PasswordResetService resets;
    private final boolean secure;
    public AuthController(AuthService auth, JwtService jwt, RefreshTokenService refresh, PasswordResetService resets,
            @Value("${app.refresh-cookie-secure:true}") boolean secure) {
        this.auth = auth; this.jwt = jwt; this.refresh = refresh; this.resets = resets; this.secure = secure;
    }
    @PostMapping("/login")
    public ResponseEntity<Session> login(@Valid @RequestBody Login request, HttpServletRequest http) {
        var user = auth.login(request.email(), request.password(), http.getRemoteAddr());
        return session(user, refresh.issue(user));
    }
    @PostMapping("/refresh")
    public ResponseEntity<Session> refresh(@CookieValue(name = "refresh_token", required = false) String token) {
        var rotation = refresh.rotate(token); return session(rotation.user(), rotation.token());
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(name = "refresh_token", required = false) String token) {
        refresh.logout(token);
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookie("", Duration.ZERO)).build();
    }
    @GetMapping("/me")
    public User me(@AuthenticationPrincipal AuthPrincipal principal) { return User.of(auth.me(principal.id())); }
    @PutMapping("/me")
    public User update(@AuthenticationPrincipal AuthPrincipal principal, @Valid @RequestBody Profile request) {
        return User.of(auth.update(principal.id(), request));
    }
    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void forgot(@Valid @RequestBody Forgot request) { resets.forgot(request.email()); }
    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reset(@Valid @RequestBody Reset request) { resets.reset(request.token(), request.password()); }
    private ResponseEntity<Session> session(AppUser user, String token) {
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie(token, Duration.ofDays(7)))
            .header(HttpHeaders.CACHE_CONTROL, "no-store").body(new Session(jwt.issue(user), User.of(user)));
    }
    private String cookie(String token, Duration age) {
        return ResponseCookie.from("refresh_token", token).httpOnly(true).secure(secure).sameSite("Strict")
            .path("/api/auth").maxAge(age).build().toString();
    }
    public record Login(@NotBlank @Email @Size(max=255) String email, @NotBlank @Size(max=72) String password) {}
    public record Forgot(@NotBlank @Email @Size(max=255) String email) {}
    public record Reset(@NotBlank @Size(max=100) String token, @NotBlank @Size(min=12, max=72) String password) {}
    public record Profile(@NotBlank @Size(max=255) String name, @NotBlank @Email @Size(max=255) String email,
                          @NotBlank @Size(max=72) String currentPassword, @Size(min=12, max=72) String password) {}
    public record Session(String accessToken, User user) {}
    public record User(UUID id, String name, String email, Role role) {
        static User of(AppUser user) { return new User(user.getId(), user.getDisplayName(), user.getEmail(), user.getRole()); }
    }
}
