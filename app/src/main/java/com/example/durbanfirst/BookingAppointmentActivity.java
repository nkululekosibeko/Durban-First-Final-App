package com.example.durbanfirst;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BookingAppointmentActivity extends AppCompatActivity {
    private EditText dateEditText, reasonEditText;
    private Spinner timeSlotSpinner;
    private Button bookNowButton;
    private DatabaseReference appointmentsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_appointment);

        dateEditText = findViewById(R.id.dateEditText);
        reasonEditText = findViewById(R.id.reasonEditText);
        timeSlotSpinner = findViewById(R.id.timeSlotSpinner);
        bookNowButton = findViewById(R.id.bookNowButton);

        // Set up Firebase
        mAuth = FirebaseAuth.getInstance();
        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");

        // Set up the Spinner for time slots
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.time_slots, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSlotSpinner.setAdapter(adapter);

        // Set up the book button's click listener
        bookNowButton.setOnClickListener(v -> bookAppointment());
    }

    private void bookAppointment() {
        String date = dateEditText.getText().toString();
        String reason = reasonEditText.getText().toString();
        String time = timeSlotSpinner.getSelectedItem().toString();
        String userId = mAuth.getCurrentUser().getUid();
        String id = appointmentsRef.push().getKey();

        Appointment appointment = new Appointment(id, userId, date, time, reason, "pending");

        appointmentsRef.child(id).setValue(appointment).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(BookingAppointmentActivity.this, "Appointment booked successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(BookingAppointmentActivity.this, "Failed to book appointment", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
