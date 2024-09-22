package com.example.durbanfirst;

public class HelperClass {
    // 1. Below i declare all the variables i will be storing in firebase realtime Database.
    String name, email, username, password;

    //4. This are get & setter methods, created by right clicking on the 'Declared variables' for
    //      the firebase database, created it i selected all the declared attributes used for the
    //      firebase database.
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    //3. This is a created constructor, created by right clicking on the 'Declared variables' for
    //      the firebase database. This is not an empty constructor as when i created it i selected
    //      all the declared attributes used for the firebase database.
    public HelperClass(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    // 2. This is a created constructor, created by right clicking on the 'Declared variables' for
    //      The firebase database. This is an empty constructor created by only selecting only the
    //      class name.
    public HelperClass() {
    }
}
