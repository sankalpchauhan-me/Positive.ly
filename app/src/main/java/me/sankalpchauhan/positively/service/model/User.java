package me.sankalpchauhan.positively.service.model;

import androidx.annotation.Keep;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

@Keep
public class User implements Serializable {
    public String uid;
    public String name;
    @SuppressWarnings("WeakerAccess")
    public String email;
    public String photoUrl;
    @Exclude
    public boolean isAuthenticated;
    @Exclude
    public boolean isNew, isCreated;
    @Exclude
    public boolean isAnonymous;

    public User() {}

    public User(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
    }

    public User(String uid, String name, String email, String photoUrl) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.photoUrl = photoUrl;
    }
}
