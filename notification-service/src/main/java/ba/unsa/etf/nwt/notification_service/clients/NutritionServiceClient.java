package ba.unsa.etf.nwt.notification_service.clients;

import ba.unsa.etf.nwt.notification_service.models.Meal;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("nutrition-service")
public interface NutritionServiceClient {

	@RequestMapping(method = RequestMethod.GET, value = "/api/v1/meal/by-user-and-date")
	List<Meal> getMealsByUserAndDate(@RequestParam Long userId, @RequestParam String from, @RequestParam String to);

}
