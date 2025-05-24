package ba.unsa.etf.nwt.notification_service.utils;

public final class StringUtils {

	public static String capitalize(final String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
	}

}
