package com.magnumgeek.examenuniversidad;

/**
 * Created by Jesus on 10/02/2018.
 */

public class DataBaseMultiplayer {

    public String userName;
    public String age;

    public DataBaseMultiplayer() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public DataBaseMultiplayer(String userName, String age) {
        this.userName = userName;
        this.age = age;
    }

    public String getUserName(){
        return userName;
    }
}
