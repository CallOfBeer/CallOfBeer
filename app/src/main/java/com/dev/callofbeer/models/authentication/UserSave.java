package com.dev.callofbeer.models.authentication;

import com.dev.callofbeer.models.authentication.Authentication;
import com.dev.callofbeer.models.authentication.User;

/**
 * Created by martin on 02/04/15.
 */
public class UserSave {
    private User user;
    private Authentication authentication;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }
}
