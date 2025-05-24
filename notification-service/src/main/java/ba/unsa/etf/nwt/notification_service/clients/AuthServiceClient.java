package ba.unsa.etf.nwt.notification_service.clients;

import ba.unsa.etf.nwt.notification_service.domain.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.UUID;

@FeignClient("auth")
public interface AuthServiceClient {

	@RequestMapping(method = RequestMethod.GET, value = "/api/v1/user/{uuid}")
	User getUser(@PathVariable UUID uuid);

}
