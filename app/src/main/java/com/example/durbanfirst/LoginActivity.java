package com.example.durbanfirst;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    //10. First we declare all the necessary variables.
    EditText loginUsername, loginPassword;
    Button loginButton;
    TextView signupRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //11. Here we initialise them all.
        loginUsername = findViewById(R.id.login_username);
        loginPassword = findViewById(R.id.login_password);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        loginButton = findViewById(R.id.login_button);

        //17. Adding an onclick listener where we call all methods.
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateUsername()) {
                    loginUsername.requestFocus(); // Set focus to username field if it's invalid
                } else if (!validatePassword()) {
                    loginPassword.requestFocus(); // Set focus to password field if it's invalid
                } else {
                    checkUser(); // Proceed to check user credentials if both are valid
                }
            }
        });



        //18. We create the intent that will lead the user from login page to signup page.
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }


    //12. For the authentication part, we are going to use the userName and password. So we need to
    // do the following.

    //12.1 Validate the userName.
    public Boolean validateUsername(){
        //12.1.1 We take the input value from the input 'EditText' and store it on the 'val' variable.
        String val = loginUsername.getText().toString();
        if (val.isEmpty()) //12.1.2 So we check if the 'val' is empty, should it be, we trow an error
        // since the userName cannot be empty for the user to login.
        {
            loginUsername.setError("Username cannot be empty");
            return false;
        }
        else //12.1.3 It will provide any other error which will be null.
        {
            loginUsername.setError(null);
            return true;
        }
    }

    //13 The same as said in No.'12' applies here.`
    public Boolean validatePassword(){
        String val = loginPassword.getText().toString();
        if (val.isEmpty()){
            loginPassword.setError("Password cannot be empty");
            return false;
        }
        else
        {
            loginPassword.setError(null);
            return true;
        }
    }


    //14. This method checks whether the user is in the database or not.
    public void checkUser(){
        //14.1 Here we create the below variables that are responsible for retrieving and processing
        //      user input from EditText fields in a login form.
        String userUsername = loginUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        //14.2 We reference the exact parent view that we created at the time the user was signing
        //      up which we names as users.
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");

        //14.3 With the help of 'Query' we will compare the username & password the user entered
        //      with the one that exist in the database. The output is stored in the
        //      'checkUserDatabase' variable.
        Query checkUserDatabase = reference.orderByChild("username").equalTo(userUsername);

        //15. Attaching a listener that reads data from the database once (single event) to check if the user exists
        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // 15.1 Checking if the snapshot (data at the specified database reference) exists
                if (snapshot.exists()) {
                    //15.2 Clear any previous errors on the username field
                    loginUsername.setError(null);

                    //15.3 Retrieve the password stored in the database for the provided username
                    String passwordFromDB = snapshot.child(userUsername).child("password").getValue(String.class);

                    //15.4.1 Check if the password retrieved from the database matches the user's input
                    if (passwordFromDB.equals(userPassword)) {
                        //15.4.2 If the password matches, clear any previous errors on the username field
                        loginUsername.setError(null);

                        //15.4.3 Create an Intent to navigate from LoginActivity to MainActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                        //15.4.4 Start MainActivity, completing the login process
                        startActivity(intent);
                    } else {
                        //15.4.5 If the password doesn't match, show an error message on the password field
                        loginPassword.setError("Invalid Credentials");

                        //15.4.6 Set focus to the password field for the user to correct their input
                        loginPassword.requestFocus();
                    }
                } else {
                    //15.4.5 If the snapshot doesn't exist, it means the username is not found in the database
                    // Show an error message on the username field indicating that the user does not exist
                    loginUsername.setError("User does not exist");

                    //15.4.6 Set focus to the username field for the user to correct their input
                    loginUsername.requestFocus();
                }
            }

            //16. This method is required to handle potential errors when retrieving data from the database
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //16.1 Handle potential database errors here (e.g., log the error or display a message to the user)
                Toast.makeText(LoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}