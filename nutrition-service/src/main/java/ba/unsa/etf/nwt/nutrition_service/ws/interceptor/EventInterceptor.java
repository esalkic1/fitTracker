package ba.unsa.etf.nwt.nutrition_service.ws.interceptor;

import ba.unsa.etf.nwt.events.EventRequest;
import ba.unsa.etf.nwt.events.EventServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

public class EventInterceptor implements HandlerInterceptor {

    @Override
    public void afterCompletion(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            final Exception ex
    ) {
        // this should be discovered by eureka instead
        final ManagedChannel channel= ManagedChannelBuilder
                .forAddress("localhost",9060)
                .usePlaintext()
                .build();

        final EventServiceGrpc.EventServiceBlockingStub stub = EventServiceGrpc.newBlockingStub(channel);
        stub.logEvent(
                EventRequest.newBuilder()
                        .setTimestamp(LocalDateTime.now().toString())
                        .setMicroserviceName("nutrition-service")
                        .setUser("temp")
                        .setAction(request.getMethod())
                        .setResource(request.getRequestURI())
                        .setResponseType(Integer.toString(response.getStatus()))
                        .build()
        );
        channel.shutdown();
    }
}

