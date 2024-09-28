package com.example.durbanfirst;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminActivity extends AppCompatActivity {

    // Image Buttons
    ImageView ViewUserBookings, ViewExistingUsers;

    // Regular Button
    Button AdminLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // IMage Buttons
        ViewUserBookings = findViewById(R.id.users_appointments);
        ViewExistingUsers = findViewById(R.id.users_history);

        // Buttons
        AdminLogout = findViewById(R.id.admin_logout);


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
