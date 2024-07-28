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

import com.drg.ironvault.database.PwdDatabase;
import com.drg.ironvault.entity.Pwd;

public class PwdActivity extends AppCompatActivity {

    private int userId;
    private EditText newTitle;
    private EditText newUsername;
    private EditText newPassword;
    private EditText notesText;
    private Button savePwdButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pwd);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = getIntent().getIntExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "User ID not passed properly", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        newTitle = findViewById(R.id.newTitle);
        newUsername = findViewById(R.id.newUsername);
        newPassword = findViewById(R.id.newPwd);
        notesText = findViewById(R.id.notesText);
        savePwdButton = findViewById(R.id.saveEditPwdButton);

        savePwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = newTitle.getText().toString();
                String username = newUsername.getText().toString();
                String password = newPassword.getText().toString();
                String notes = notesText.getText().toString();

                if (title.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(PwdActivity.this, "Title / Username / Password is required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Pwd newPwd = new Pwd(title, username, password, notes, userId);
                saveNewPwd(newPwd);
                Toast.makeText(PwdActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    void saveNewPwd(Pwd newPwd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PwdDatabase.getINSTANCE(getApplicationContext()).pwdDao().upsert(newPwd);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish(); // Finish the activity once the data is saved
                    }
                });
            }
        }).start();
    }
}