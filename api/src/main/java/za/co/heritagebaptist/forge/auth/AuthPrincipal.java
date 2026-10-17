package za.co.heritagebaptist.forge.auth;
import java.util.UUID;
public record AuthPrincipal(UUID id, String name, Role role) implements java.security.Principal {
    @Override public String getName() { return id.toString(); }
}
