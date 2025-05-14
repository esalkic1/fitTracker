package ba.unsa.etf.nwt.system_events.services;

import ba.unsa.etf.nwt.events.EventRequest;
import ba.unsa.etf.nwt.events.EventResponse;
import ba.unsa.etf.nwt.events.EventServiceGrpc;
import ba.unsa.etf.nwt.system_events.domain.Event;
import ba.unsa.etf.nwt.system_events.repositories.EventRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class EventsService extends EventServiceGrpc.EventServiceImplBase {
	private static final Logger LOGGER = LoggerFactory.getLogger(EventsService.class);

	private final EventRepository eventRepository;

	public EventsService(final EventRepository eventRepository) {
		this.eventRepository = eventRepository;
	}

	@Override
	public void logEvent(final EventRequest request, final StreamObserver<EventResponse> responseObserver) {
		final String timestamp = request.getTimestamp();
		final String microserviceName = request.getMicroserviceName();
		final String user = request.getUser();
		final String action = request.getAction();
		final String resource = request.getResource();
		final String responseType = request.getResponseType();

		LOGGER.info(
				"Received event: Action={}, Resource={}, Service={}, User={}, Timestamp={}, ResponseType={}",
				action, resource, microserviceName, user, timestamp, responseType
		);

		eventRepository.save(new Event(timestamp, microserviceName, user, action, resource, responseType));

		final EventResponse response = EventResponse.newBuilder()
				.setResponse(responseType)
				.build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}
}
