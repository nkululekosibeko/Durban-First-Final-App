package com.example.durbanfirst;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminActivity extends AppCompatActivity {

    // Image Buttons
    ImageView ViewUserBookings, ViewExistingUsers;

    // Regular Button
    Button AdminLogout;

    // Dashboard Name
    TextView DashboardName;

    // The Database
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // IMage Buttons
        ViewUserBookings = findViewById(R.id.users_appointments);
        ViewExistingUsers = findViewById(R.id.users_history);
        // Buttons
        AdminLogout = findViewById(R.id.admin_logout);

        // The Dashboard Name
        DashboardName = findViewById(R.id.dashboard_name);

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            // Initialize Firebase Database reference
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

            // Retrieve and display user's full name from the database
            mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String fullName = dataSnapshot.child("full_name").getValue(String.class);
                        if (fullName != null) {
                            DashboardName.setText(fullName);  // Display user's name
                        } else {
                            DashboardName.setText("User");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors
                }
            });
        }




        AdminLogout.setOnClickListener(v -> {
            Intent intent = new Intent(AdminActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        });


        // Enable edge-to-edge layout
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.admin_dash), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
