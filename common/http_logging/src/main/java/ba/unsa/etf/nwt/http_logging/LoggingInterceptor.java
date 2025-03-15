package ba.unsa.etf.nwt.http_logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoggingInterceptor implements HandlerInterceptor {
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggingInterceptor.class);
	private static final ThreadLocal<Long> startTime = new ThreadLocal<>();

	@Override
	public boolean preHandle(
			final HttpServletRequest requestServlet,
			final HttpServletResponse responseServlet,
			final Object handler
	) {

		startTime.set(System.currentTimeMillis());

		LOGGER.info(
				"Handling request url={}, method={}",
				requestServlet.getRequestURI(),
				requestServlet.getMethod()
		);

		return true;
	}

	@Override
	public void afterCompletion(
			final HttpServletRequest request,
			final HttpServletResponse response,
			final Object handler,
			final Exception ex
	) {
		long duration = System.currentTimeMillis() - startTime.get();
		startTime.remove();

		LOGGER.info(
				"Request ended url={}, method={}, duration={}, status={}",
				request.getRequestURI(),
				request.getMethod(),
				duration + "ms",
				response.getStatus()
		);
	}
}
