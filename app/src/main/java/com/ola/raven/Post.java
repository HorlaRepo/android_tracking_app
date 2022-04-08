package com.ola.raven;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hopeSMART on 8/3/2017.
 */
@IgnoreExtraProperties
public class Post {

    public String uid;
    public String sender;
    public String message;
    public int starCount = 0;
    public Map<String, Boolean> sms = new HashMap<>();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String sender, String message) {
        this.uid = uid;
        this.sender = sender;
        this.message = message;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("sender", sender);
        result.put("message", message);
        result.put("starCount", starCount);
        result.put("sms", sms);

        return result;
    }

}