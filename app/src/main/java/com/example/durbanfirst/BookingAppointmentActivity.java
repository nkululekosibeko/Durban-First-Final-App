package com.example.durbanfirst;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class BookingAppointmentActivity extends AppCompatActivity {
    private EditText dateEditText, reasonEditText;
    private Spinner timeSlotSpinner;
    private Button bookNowButton, backToDash;
    private DatabaseReference appointmentsRef;
    private FirebaseAuth mAuth;  // Ensure FirebaseAuth is initialized
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_appointment);

        // Initialize UI components
        dateEditText = findViewById(R.id.dateEditText);
        reasonEditText = findViewById(R.id.reasonEditText);
        timeSlotSpinner = findViewById(R.id.timeSlotSpinner);
        bookNowButton = findViewById(R.id.bookNowButton);
        backToDash = findViewById(R.id.to_dash);
        progressBar = findViewById(R.id.progressBar);

        // Initialize Firebase Authentication and Database
        mAuth = FirebaseAuth.getInstance();
        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");

        // Set up time slot spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.time_slots, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSlotSpinner.setAdapter(adapter);

        // Date formatting
        dateEditText.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String cleanString = s.toString().replaceAll("[^\\d]", "");
                    String formatted = formatDateString(cleanString);
                    current = formatted;
                    dateEditText.setText(formatted);
                    dateEditText.setSelection(formatted.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Book appointment button listener
        bookNowButton.setOnClickListener(v -> bookAppointment());

        backToDash.setOnClickListener(v -> {
            Intent intent = new Intent(BookingAppointmentActivity.this, ApplicantActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Format date (MM/dd/yyyy)
    private String formatDateString(String input) {
        if (input.length() <= 2) {
            return input;
        } else if (input.length() <= 4) {
            return input.substring(0, 2) + "/" + input.substring(2);
        } else {
            return input.substring(0, 2) + "/" + input.substring(2, 4) + "/" + input.substring(4);
        }
    }

    private void bookAppointment() {
        progressBar.setVisibility(View.VISIBLE);
        String date = dateEditText.getText().toString();
        String reason = reasonEditText.getText().toString();
        String time = timeSlotSpinner.getSelectedItem().toString();

        // Ensure fields are filled
        if (reason.isEmpty() || date.isEmpty() || time.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate date
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        dateFormat.setLenient(false);
        try {
            Date parsedDate = dateFormat.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);
            if (calendar.get(Calendar.YEAR) < 2024) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Please enter a valid date in 2024", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ParseException e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please enter a valid date (MM/dd/yyyy)", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String id = appointmentsRef.push().getKey();

        // Check time slot availability
        isSlotAvailable(date, time, new SlotAvailabilityCallback() {
            @Override
            public void onSlotAvailable() {
                Appointment appointment = new Appointment(id, userId, date, time, reason, "pending");
                appointmentsRef.child(id).setValue(appointment).addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        sendConfirmationEmail(currentUser.getEmail(), date, time, reason);
                        Toast.makeText(BookingAppointmentActivity.this, "Appointment booked successfully", Toast.LENGTH_SHORT).show();
                        // Redirect to ApplicantActivity
                        Intent intent = new Intent(BookingAppointmentActivity.this, ApplicantActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(BookingAppointmentActivity.this, "Failed to book appointment", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onSlotUnavailable() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BookingAppointmentActivity.this, "Time slot unavailable", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Slot availability logic
    private void isSlotAvailable(String date, String time, SlotAvailabilityCallback callback) {
        appointmentsRef.orderByChild("date").equalTo(date).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String appointmentTime = snapshot.child("time").getValue(String.class);
                    String appointmentStatus = snapshot.child("status").getValue(String.class);
                    if (appointmentTime.equals(time) && appointmentStatus.equals("pending")) {
                        callback.onSlotUnavailable();
                        return;
                    }
                }
                callback.onSlotAvailable();
            } else {
                callback.onSlotAvailable();
            }
        });
    }

    interface SlotAvailabilityCallback {
        void onSlotAvailable();
        void onSlotUnavailable();
    }

    // Email confirmation logic
    private void sendConfirmationEmail(String recipient, String date, String time, String reason) {
        new SendEmailTask(recipient, date, time, reason).execute();
    }

    private class SendEmailTask extends AsyncTask<Void, Void, Boolean> {
        private String recipient, date, time, reason;

        public SendEmailTask(String recipient, String date, String time, String reason) {
            this.recipient = recipient;
            this.date = date;
            this.time = time;
            this.reason = reason;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.gmail.com");
                props.put("mail.smtp.port", "587");

                Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("your-email@gmail.com", "your-password");
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("sibekonkululeko706@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject("Appointment Confirmation");
                message.setText("Your appointment has been successfully booked.\n\nDate: " + date + "\nTime: " + time + "\nReason: " + reason);
                Transport.send(message);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                Toast.makeText(BookingAppointmentActivity.this, "Failed to send confirmation email", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
