package ba.unsa.etf.nwt.common.notifier;

import ba.unsa.etf.nwt.common.notifier.email.EmailNotifierConfiguration;
import ba.unsa.etf.nwt.common.notifier.mock.MockNotifierConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@Import({
		EmailNotifierConfiguration.class,
		MockNotifierConfiguration.class
})
public class NotifierAutoConfiguration {
}
