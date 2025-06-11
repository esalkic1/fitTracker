package ba.unsa.etf.nwt.notification_service.dto;

import java.util.UUID;

public class UserCreatedEvent {
    private UUID handle;
    private String email;

    public UserCreatedEvent() {
    }

    public UserCreatedEvent(UUID handle, String email) {
        this.handle = handle;
        this.email = email;
    }

    public UserCreatedEvent(UUID handle) {
        this.handle = handle;
    }

    public UUID getHandle() {
        return handle;
    }

    public void setHandle(UUID handle) {
        this.handle = handle;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserCreatedEvent{" +
                "handle=" + handle +
                ", email='" + email + '\'' +
                '}';
    }
}