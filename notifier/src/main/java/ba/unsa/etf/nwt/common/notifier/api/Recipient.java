package ba.unsa.etf.nwt.common.notifier.api;

public record Recipient(String email) {

	public static Recipient fromMail(String email) {
		return new Recipient(email);
	}
}
