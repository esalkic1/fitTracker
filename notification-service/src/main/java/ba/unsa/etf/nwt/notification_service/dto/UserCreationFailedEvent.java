package ba.unsa.etf.nwt.notification_service.dto;

import java.util.UUID;

public class UserCreationFailedEvent {
    private UUID handle;

    public UserCreationFailedEvent() {}

    public UserCreationFailedEvent(UUID handle) {
        this.handle = handle;
    }

    public UUID getHandle() {
        return handle;
    }

    public void setHandle(UUID handle) {
        this.handle = handle;
    }
}
