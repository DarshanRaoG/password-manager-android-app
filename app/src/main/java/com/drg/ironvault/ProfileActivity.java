package com.drg.ironvault;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.drg.ironvault.database.UserDatabase;
import com.drg.ironvault.entity.User;

public class ProfileActivity extends AppCompatActivity {

    private EditText ivUsername;
    private EditText ivPassword;
    private Button ivButton;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        EdgeToEdge.enable(this); // Ensure EdgeToEdge library is correctly integrated

        // Handle system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = getIntent().getIntExtra("userId", -1);
        if(userId == -1) {
            Toast.makeText(this, "User ID not passed properly", Toast.LENGTH_SHORT).show();
            finish();
        }

        ivUsername = findViewById(R.id.ivUsername);
        ivPassword = findViewById(R.id.ivPassword);
        ivButton = findViewById(R.id.ivButton);

        fillData();

        ivButton.setOnClickListener(view -> {
            String username = ivUsername.getText().toString();
            String password = ivPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Username / Password is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if user exists asynchronously
            checkIfUserExists(username, password);
        });
    }

    private void fillData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = UserDatabase.getINSTANCE(getApplicationContext()).userDao().getUserById(userId);
                ivUsername.setText(user.getUsername());
                ivPassword.setText(user.getPassword());
            }
        }).start();
    }

    private void checkIfUserExists(String username, String password) {
        new Thread(() -> {
            User existingUser = UserDatabase.getINSTANCE(ProfileActivity.this).userDao()
                    .verifyUser(username);

            runOnUiThread(() -> {
                if(existingUser != null && !existingUser.getUsername().equalsIgnoreCase(username)) {
                    Toast.makeText(ProfileActivity.this, "Username already exists!", Toast.LENGTH_SHORT).show();
                } else {
                    User user = new User(userId, username, password);
                    updateUser(user);
                }
            });
        }).start();
    }

    private void updateUser(User user) {
        new Thread(() -> {
            UserDatabase.getINSTANCE(ProfileActivity.this).userDao()
                    .upsert(user);
            runOnUiThread(() -> {
                Toast.makeText(ProfileActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
                finish();
            });
        }).start();
    }
}