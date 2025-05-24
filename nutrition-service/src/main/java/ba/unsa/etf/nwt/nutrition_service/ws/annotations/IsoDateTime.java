package ba.unsa.etf.nwt.nutrition_service.ws.annotations;

import org.springframework.format.annotation.DateTimeFormat;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
public @interface IsoDateTime {
	// Convenient wrapper around iso format annotation
}
