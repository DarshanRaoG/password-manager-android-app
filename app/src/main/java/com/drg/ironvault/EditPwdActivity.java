package com.drg.ironvault;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.drg.ironvault.database.PwdDatabase;
import com.drg.ironvault.entity.Pwd;

public class EditPwdActivity extends AppCompatActivity {

    private int pwdId;
    private EditText editTitle;
    private EditText editUsername;
    private EditText editPassword;
    private EditText editNotes;
    private Button savePwdButton;
    private Button deleteButton;
    private Pwd updatedPwd;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pwd);

        pwdId = getIntent().getIntExtra("pwdId", -1);
        if (pwdId == -1) {
            Toast.makeText(this, "Password ID not passed properly", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        editTitle = findViewById(R.id.editTitle);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        editNotes = findViewById(R.id.editNotes);
        savePwdButton = findViewById(R.id.saveEditPwdButton);
        deleteButton = findViewById(R.id.deleteButton);

        // Load the existing password details
        loadPwdDetails();

        savePwdButton.setOnClickListener(view -> {
            String title = editTitle.getText().toString();
            String username = editUsername.getText().toString();
            String password = editPassword.getText().toString();
            String notes = editNotes.getText().toString();

            if (title.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(EditPwdActivity.this, "Title / Username / Password is required!", Toast.LENGTH_SHORT).show();
                return;
            }

            updatedPwd = new Pwd(pwdId, title, username, password, notes, userId);
            updatePwd(updatedPwd);
            Toast.makeText(EditPwdActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deletePwd();
            }
        });
    }

    private void loadPwdDetails() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Pwd pwd = PwdDatabase.getINSTANCE(getApplicationContext()).pwdDao().getPwdById(pwdId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (pwd != null) {
                            editTitle.setText(pwd.getTitle());
                            editUsername.setText(pwd.getUsername());
                            editPassword.setText(pwd.getPassword());
                            editNotes.setText(pwd.getNotes());

                            userId = pwd.getUserId();

                        } else {
                            Toast.makeText(EditPwdActivity.this, "Password not found!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        }).start();
    }

    private void updatePwd(Pwd updatedPwd) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PwdDatabase.getINSTANCE(getApplicationContext()).pwdDao().upsert(updatedPwd);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish(); // Finish the activity once the data is updated
                    }
                });
            }
        }).start();
    }

    private void deletePwd() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PwdDatabase.getINSTANCE(getApplicationContext()).pwdDao().deleteById(pwdId);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EditPwdActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                        finish(); // Finish the activity once the data is updated
                    }
                });
            }
        }).start();
    }
}