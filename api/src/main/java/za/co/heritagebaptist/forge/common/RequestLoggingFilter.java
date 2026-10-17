package za.co.heritagebaptist.forge.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger LOG = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String supplied = request.getHeader("X-Request-Id");
        String requestId = supplied != null && supplied.matches("[A-Za-z0-9._-]{1,100}")
            ? supplied : UUID.randomUUID().toString();
        String previous = MDC.get("requestId");
        MDC.put("requestId", requestId);
        response.setHeader("X-Request-Id", requestId);
        long started = System.nanoTime();
        try {
            chain.doFilter(request, response);
        } finally {
            LOG.info("method={} path={} status={} durationMs={}", request.getMethod(),
                request.getRequestURI().replaceAll("[\\r\\n]", "_"), response.getStatus(),
                (System.nanoTime() - started) / 1_000_000);
            if (previous == null) {
                MDC.remove("requestId");
            } else {
                MDC.put("requestId", previous);
            }
        }
    }
}
