package com.example.durbanfirst;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    EditText loginEmailUsername, loginPassword;
    Button loginButton;
    TextView signupRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Enable edge-to-edge layout
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loginEmailUsername = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        signupRedirectText = findViewById(R.id.signupRedirectText);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(view -> {
            if (!validateEmail()) {
                loginEmailUsername.requestFocus();
            } else if (!validatePassword()) {
                loginPassword.requestFocus();
            } else {
                checkUser();
            }
        });

        signupRedirectText.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    public Boolean validateEmail() {
        String val = loginEmailUsername.getText().toString();
        if (val.isEmpty()) {
            loginEmailUsername.setError("Email cannot be empty");
            return false;
        } else {
            loginEmailUsername.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Password cannot be empty");
            return false;
        } else {
            loginPassword.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String userEmail = loginEmailUsername.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("email").equalTo(userEmail);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    loginEmailUsername.setError(null);

                    String passwordFromDB = null;
                    String roleFromDB = null;

                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        passwordFromDB = userSnapshot.child("password").getValue(String.class);
                        roleFromDB = userSnapshot.child("role").getValue(String.class);
                    }

                    if (passwordFromDB != null && passwordFromDB.equals(userPassword)) {
                        Intent intent;
                        if (roleFromDB != null) {
                            if (roleFromDB.equals("Admin")) { // Change to match your role naming
                                intent = new Intent(LoginActivity.this, AdminActivity.class);
                            } else {
                                intent = new Intent(LoginActivity.this, ApplicantActivity.class);
                            }
                            startActivity(intent);
                            finish(); // Close login activity if needed
                        }
                    } else {
                        loginPassword.setError("Invalid Credentials");
                        loginPassword.requestFocus();
                    }
                } else {
                    loginEmailUsername.setError("User does not exist");
                    loginEmailUsername.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
