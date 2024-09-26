package com.example.durbanfirst;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;  // Add TextView for displaying user details

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

public class ApplicantActivity extends AppCompatActivity {

    ImageView BookAppointment;
    Button ApplicantLogout;
    TextView DashBoardName;  // TextView for user's full name
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant);

        BookAppointment = findViewById(R.id.book_appointment_btn);
        ApplicantLogout = findViewById(R.id.applicant_logout);
        DashBoardName = findViewById(R.id.dashboard_name);  // Ensure TextView exists in your XML layout

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid();

            // Initialize Firebase Database reference to 'users' node
            mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

            // Retrieve user information from the database using userId
            mDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Get user profile information
                        String fullName = dataSnapshot.child("full_name").getValue(String.class);
                        if (fullName != null) {
                            DashBoardName.setText(fullName);  // Display the full name on the dashboard
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors during retrieval
                }
            });
        }

        // Enable edge-to-edge layout
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.applicant_dash), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Book appointment button listener
        BookAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(ApplicantActivity.this, BookingAppointmentActivity.class);
            startActivity(intent);
        });

        // Logout button listener
        ApplicantLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ApplicantActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
