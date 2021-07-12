package edu.sharif.ce.apyugioh.model.networking.response;

import edu.sharif.ce.apyugioh.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginResponse {

    private boolean isSuccessful;
    private String message;
    private User user;

    public LoginResponse(boolean isSuccessful, String message, User user) {
        this.isSuccessful = isSuccessful;
        this.message = message;
        this.user = user;
    }
}
