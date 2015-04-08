package com.dev.callofbeer.models.authentication;

import java.util.Date;

/**
 * Created by martin on 16/02/15.
 */

/**
 * Class Authentication
 *     Model of the client authentication to the server
 *
 */
public class Authentication {
    private String access_token;
    private String refresh_token;
    private Date expiration;
    private String type;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
