package com.example.durbanfirst;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class AdminViewUserActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<UserProfileInfo> userList;
    private DatabaseReference usersRef;

    // Go back to dash Admin
    Button ToDashAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view_user);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize list and adapter
        userList = new ArrayList<>();
        adapter = new UserAdapter(userList);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase Database reference
        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Load users
        loadUsers();

        ToDashAdmin = findViewById(R.id.back_to_dash_admin_Users);
        // To dash admin button
        ToDashAdmin.setOnClickListener(v -> {
            Intent intent = new Intent(AdminViewUserActivity.this, AdminActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadUsers() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserProfileInfo user = snapshot.getValue(UserProfileInfo.class);
                    userList.add(user);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(AdminViewUserActivity.this, "Failed to load users.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
