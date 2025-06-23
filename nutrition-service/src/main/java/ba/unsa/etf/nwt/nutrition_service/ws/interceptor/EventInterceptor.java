package ba.unsa.etf.nwt.nutrition_service.ws.interceptor;

import ba.unsa.etf.nwt.events.EventRequest;
import ba.unsa.etf.nwt.events.EventServiceGrpc;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

public class EventInterceptor implements HandlerInterceptor {
    private final EurekaClient eurekaClient;

    public EventInterceptor(final EurekaClient eurekaClient) {
        this.eurekaClient = eurekaClient;
    }

    @Override
    public void afterCompletion(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final Object handler,
            final Exception ex
    ) {
        final String host;
        final int port;
        try {
            final InstanceInfo instanceInfo = eurekaClient.getApplication("SYSTEM_EVENTS").getInstances().getFirst();
            host = instanceInfo.getIPAddr();
            port = instanceInfo.getPort();
            System.out.println(host + " " + port);
        } catch (final Throwable e) {
            return;
        }

        try {
            final ManagedChannel channel= ManagedChannelBuilder
                    .forAddress(host, port)
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
        } catch (final Throwable ignored) {}
    }
}

