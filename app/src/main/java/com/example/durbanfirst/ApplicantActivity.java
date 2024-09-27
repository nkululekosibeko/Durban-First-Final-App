package com.example.durbanfirst;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ApplicantActivity extends AppCompatActivity {

    ImageView BookAppointment, ViewAppointment;
    Button ApplicantLogout;
    TextView DashBoardName;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant);

        // Buttons
        BookAppointment = findViewById(R.id.book_appointment_btn);
        ApplicantLogout = findViewById(R.id.applicant_logout);
        ViewAppointment = findViewById(R.id.view_appointment_btn);

        // TextViews
        DashBoardName = findViewById(R.id.dashboard_name);

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
                            DashBoardName.setText(fullName);  // Display user's name
                        } else {
                            DashBoardName.setText("User");
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle any errors
                }
            });
        }

        // Book appointment button
        BookAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(ApplicantActivity.this, BookingAppointmentActivity.class);
            startActivity(intent);
        });

        ViewAppointment.setOnClickListener(v -> {
            Intent intent = new Intent(ApplicantActivity.this, ViewBookingsActivity.class);
            startActivity(intent);
        });

        // Logout button
        ApplicantLogout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(ApplicantActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
