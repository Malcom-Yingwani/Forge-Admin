package za.co.heritagebaptist.forge.auth;
import com.github.benmanes.caffeine.cache.*;
import java.time.*;
import org.springframework.stereotype.Component;
@Component
public class LoginThrottle {
    private final Cache<String, Attempts> attempts = Caffeine.newBuilder().maximumSize(100000)
        .expireAfterWrite(Duration.ofMinutes(15)).build();
    public synchronized void check(String email, String ip) { checkKey("email:" + email, 5); checkKey("ip:" + ip, 20); }
    private void checkKey(String key, int limit) {
        var value = attempts.getIfPresent(key);
        if (value != null && value.until().isAfter(Instant.now()) && value.count() >= limit)
            throw new Limited(Math.max(1, Duration.between(Instant.now(), value.until()).toSeconds()));
    }
    public synchronized void failed(String email, String ip) { increment("email:" + email); increment("ip:" + ip); }
    public synchronized void success(String email) { attempts.invalidate("email:" + email); }
    private void increment(String key) {
        var previous = attempts.getIfPresent(key);
        if (previous == null || !previous.until().isAfter(Instant.now())) attempts.put(key, new Attempts(1, Instant.now().plusSeconds(900)));
        else attempts.put(key, new Attempts(previous.count() + 1, previous.until()));
    }
    private record Attempts(int count, Instant until) {}
    public static class Limited extends RuntimeException {
        public final long retryAfter;
        Limited(long retryAfter) { super("Too many attempts. Try again later."); this.retryAfter = retryAfter; }
    }
}
