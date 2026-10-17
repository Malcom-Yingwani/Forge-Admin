package za.co.heritagebaptist.forge.auth;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwt;
    private final AppUserRepository users;
    public JwtAuthFilter(JwtService jwt, AppUserRepository users) { this.jwt = jwt; this.users = users; }
    @Override protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                var claims = jwt.parse(header.substring(7));
                var user = users.findById(claims.id()).filter(AppUser::isEnabled).orElseThrow(AuthFailure::new);
                var principal = new AuthPrincipal(user.getId(), user.getDisplayName(), user.getRole());
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                    principal, null, List.of(new SimpleGrantedAuthority("ROLE_" + principal.role()))));
            } catch (io.jsonwebtoken.JwtException | IllegalArgumentException | AuthFailure exception) {
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }
}
