package fr.wcs.atelierauth.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class UserLoginDto {

    @NotEmpty
    @Size(max = 50)
    private String username;

    @NotEmpty
    @Size(max = 150, min = 8)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
