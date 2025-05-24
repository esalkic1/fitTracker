package ba.unsa.etf.nwt.common.notifier.api;

public interface Notifier {
	boolean notify(final Notification<?> notification);
}
