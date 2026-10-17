package za.co.heritagebaptist.forge.auth;
public class AuthFailure extends RuntimeException {
    public AuthFailure() { super("Invalid credentials or expired session"); }
}
