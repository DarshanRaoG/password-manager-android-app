package com.drg.ironvault;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.drg.ironvault.database.UserDatabase;
import com.drg.ironvault.entity.User;

public class LoginActivity extends AppCompatActivity {
    private int loggedUser;
    private EditText loginUsername;
    private EditText loginPassword;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        loginUsername = findViewById(R.id.ivUsername);
        loginPassword = findViewById(R.id.ivPassword);
        loginButton = findViewById(R.id.ivButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verify();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
        loggedUser = sharedPreferences.getInt("loggedUser", -1);

        if (loggedUser != -1) {
            // User is already logged in, navigate to MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("userId", loggedUser);
            startActivity(intent);
            finish();
        }
    }


    private void verify() {
        String username = loginUsername.getText().toString();
        String password = loginPassword.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Username and Password cannot be empty!", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                User existingUser = UserDatabase.getINSTANCE(getApplicationContext())
                        .userDao()
                        .verifyUser(username);

                if (existingUser != null) {
                    if (existingUser.getPassword().equals(password)) {
                        // Login successful
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Save logged in user id to shared preferences
                        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt("loggedUser", existingUser.getId());
                        editor.apply();

                        // Start MainActivity and finish LoginActivity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("userId", existingUser.getId());
                        startActivity(intent);
                        finish();
                    } else {
                        // Invalid password
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Invalid Password!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {

                    UserDatabase.getINSTANCE(getApplicationContext())
                            .userDao()
                            .upsert(new User(username, password));

                    User newUser = UserDatabase.getINSTANCE(getApplicationContext())
                            .userDao()
                            .verifyUser(username);

                    // Signup successful
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Signup Successful!", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Save logged in user id to shared preferences
                    SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("loggedUser", newUser.getId());
                    editor.apply();

                    // Start MainActivity and finish LoginActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userId", newUser.getId());
                    startActivity(intent);
                    finish();
                }
            }
        }).start();
    }

}