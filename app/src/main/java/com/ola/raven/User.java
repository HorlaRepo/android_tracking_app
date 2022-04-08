package com.ola.raven;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by hopeSMART on 8/3/2017.
 */

@IgnoreExtraProperties
public class User {

    public String sender;
    public String message;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }

}