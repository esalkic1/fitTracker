package ba.unsa.etf.nwt.common.jpa.uuid_generator;

import jakarta.persistence.PrePersist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.UUID;

public class UUIDGenerator {
	private static final Logger LOGGER = LoggerFactory.getLogger(UUIDGenerator.class);

	@PrePersist
	public void generateUUID(final Object entity) {
		for (final Field field : entity.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(AutoGenerateUUID.class)) {
				if (!field.getType().equals(UUID.class)) {
					LOGGER.warn(
							"Detected attempt to generate UUID for a field that is not UUID type. Entity = {}, Field {}",
							entity,
							field.getName()
					);
					continue;
				}

				field.setAccessible(true);

				try {
					if (field.get(entity) == null) {
						field.set(entity, UUID.randomUUID());
					}
				} catch (final IllegalAccessException e) {
					LOGGER.error(
							"Failed to generate UUID for field {} on entity {}. Exception: {}",
							field.getName(),
							entity,
							e.getMessage()
					);
				}
			}
		}
	}
}
