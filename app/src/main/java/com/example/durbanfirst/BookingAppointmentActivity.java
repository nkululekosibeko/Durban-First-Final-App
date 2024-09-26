package com.example.durbanfirst;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private Button bookNowButton;
    private DatabaseReference appointmentsRef;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_appointment);

        dateEditText = findViewById(R.id.dateEditText);
        reasonEditText = findViewById(R.id.reasonEditText);
        timeSlotSpinner = findViewById(R.id.timeSlotSpinner);
        bookNowButton = findViewById(R.id.bookNowButton);
        progressBar = findViewById(R.id.progressBar);

        // Set up Firebase
        mAuth = FirebaseAuth.getInstance();
        appointmentsRef = FirebaseDatabase.getInstance().getReference("appointments");

        // Set up the Spinner for time slots
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.time_slots, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSlotSpinner.setAdapter(adapter);

        // Set up date formatting
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

        // Set up the book button's click listener
        bookNowButton.setOnClickListener(v -> bookAppointment());
    }

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

        // Check if any of the fields are empty
        if (reason.isEmpty() || date.isEmpty() || time.isEmpty()) {
            progressBar.setVisibility(View.GONE);
            if (reason.isEmpty()) {
                Toast.makeText(this, "Please enter a reason for the appointment", Toast.LENGTH_SHORT).show();
            }
            if (date.isEmpty()) {
                Toast.makeText(this, "Please select a date for the appointment", Toast.LENGTH_SHORT).show();
            }
            if (time.isEmpty()) {
                Toast.makeText(this, "Please select a time for the appointment", Toast.LENGTH_SHORT).show();
            }
            return; // Exit method as fields are empty
        }

        // Validate date format (MM/dd/yyyy)
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        dateFormat.setLenient(false);
        try {
            Date parsedDate = dateFormat.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parsedDate);
            int year = calendar.get(Calendar.YEAR);
            if (year < 2024) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Please enter a valid date within the year 2024", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (ParseException e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Please enter a valid date in the format MM/dd/yyyy", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        String id = appointmentsRef.push().getKey();

        isSlotAvailable(date, time, new SlotAvailabilityCallback() {
            @Override
            public void onSlotAvailable() {
                Appointment appointment = new Appointment(id, userId, date, time, reason, "pending");
                appointmentsRef.child(id).setValue(appointment).addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        sendConfirmationEmail(currentUser.getEmail(), date, time, reason);
                        Toast.makeText(BookingAppointmentActivity.this, "Appointment booked successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(BookingAppointmentActivity.this, "Failed to book appointment", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onSlotUnavailable() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(BookingAppointmentActivity.this, "The selected time slot is unavailable. Please choose a different time.", Toast.LENGTH_SHORT).show();
            }
        });
    }

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

    private void sendConfirmationEmail(String assignedPerson, String date, String time, String reason) {
        new SendEmailTask(assignedPerson, date, time, reason).execute();
    }

    private class SendEmailTask extends AsyncTask<Void, Void, Boolean> {
        private String assignedPerson, date, time, reason;

        public SendEmailTask(String assignedPerson, String date, String time, String reason) {
            this.assignedPerson = assignedPerson;
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
                        return new PasswordAuthentication("fixitumndoni@gmail.com", "fnmcddqatwfrycuc");
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("fixitumndoni@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(assignedPerson));
                message.setSubject("Appointment Confirmation");
                message.setText("Hi!,\n\n" +
                        "We are pleased to inform you that your appointment has been successfully booked with us.\n\n" +
                        "Appointment Details:\n" +
                        "Date: " + date + "\n" +
                        "Time: " + time + "\n" +
                        "Reason: " + reason + "\n\n" +
                        "Thank you for choosing us!");

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
