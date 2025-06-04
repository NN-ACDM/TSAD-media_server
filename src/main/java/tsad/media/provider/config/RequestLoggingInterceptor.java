package tsad.media.provider.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {
    private final Logger log = LoggerFactory.getLogger(RequestLoggingInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @Nullable HttpServletResponse response,
                             @Nullable Object handler) {
        String ip = request.getHeader("X-Forwarded-For"); // for proxies/load balancers
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        request.setAttribute("clientIp", ip);
        log.info("preHandle() ... Incoming request from IP: {}", ip);
        return true; // continue to controller
    }
}
