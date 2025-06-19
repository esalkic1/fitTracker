package ba.unsa.etf.nwt.auth.dto;

import ba.unsa.etf.nwt.auth.domain.Role;

public class UserUpdateRequest {
    private String email;
    private Role role;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
