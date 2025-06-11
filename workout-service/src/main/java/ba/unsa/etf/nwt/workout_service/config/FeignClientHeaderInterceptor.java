package ba.unsa.etf.nwt.workout_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignClientHeaderInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs != null) {
            HttpServletRequest request = attrs.getRequest();
            String xHandle = request.getHeader("X-handle");
            String xRole = request.getHeader("X-role");

            if (xHandle != null) {
                template.header("X-handle", xHandle);
            }
            if (xRole != null) {
                template.header("X-role", xRole);
            }
        }
    }
}

