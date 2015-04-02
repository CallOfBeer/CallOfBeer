package com.dev.callofbeer.models.authentication;

import com.dev.callofbeer.models.authentication.ClientCob;

import java.util.List;

/**
 * Created by martin on 02/04/15.
 */
public class User {
    private int id;
    private String username;
    private String email;
    private List<ClientCob> authorized_clients;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<ClientCob> getAuthorized_clients() {
        return authorized_clients;
    }

    public void setAuthorized_clients(List<ClientCob> authorized_clients) {
        this.authorized_clients = authorized_clients;
    }
}
