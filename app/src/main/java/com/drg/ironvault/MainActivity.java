package com.drg.ironvault;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import com.drg.ironvault.database.PwdDatabase;
import com.drg.ironvault.database.UserDatabase;
import com.drg.ironvault.entity.Pwd;
import com.drg.ironvault.entity.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 1001;
    private static final int REQUEST_CODE_PICK_FILE = 1002;

    private int userId;
    private User user;
    private TextView welcomeText;
    private RecyclerView recyclerView;
    private FloatingActionButton addButton;
    private List<Pwd> pwds = new ArrayList<>();
    private CustomAdapter customAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userId = getIntent().getIntExtra("userId", -1);

        if (userId == -1) {
            Toast.makeText(this, "User ID not passed properly", Toast.LENGTH_SHORT).show();
            finish();
        }

        welcomeText = findViewById(R.id.welcomeText);
        getUser();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        addButton = findViewById(R.id.addButton);

        customAdapter = new CustomAdapter(pwds);
        recyclerView.setAdapter(customAdapter);

        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, PwdActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPwds();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset search query and update the UI
        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.clearFocus();
            searchView.setIconified(true);
            welcomeText.setText("Welcome, " + user.getUsername() + "!");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView from the menu
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        // Configure the search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                welcomeText.setText("Search Result: ");
                // Perform the final search
                filterRecyclerView(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                welcomeText.setText("Search Result: ");
                // Handle the search text change
                filterRecyclerView(newText);
                return true;
            }
        });

        // Handle close event of SearchView
        searchView.setOnCloseListener(() -> {
            welcomeText.setText("Welcome, " + user.getUsername() + "!");
            return false;
        });

        return true;
    }

    // Method to filter RecyclerView based on search query
    private void filterRecyclerView(String query) {
        new Thread(() -> {
            List<Pwd> filteredList = PwdDatabase.getINSTANCE(getApplicationContext()).pwdDao()
                    .search(userId, query);

            runOnUiThread(() -> {
                pwds.clear();
                pwds.addAll(filteredList);
                customAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    void getUser() {
        new Thread(() -> {
            user = UserDatabase.getINSTANCE(getApplicationContext())
                    .userDao()
                    .getUserById(userId);

            runOnUiThread(() -> welcomeText.setText("Welcome, " + user.getUsername() + "!"));
        }).start();
    }

    void getPwds() {
        new Thread(() -> {
            List<Pwd> pwdsFromDB = PwdDatabase.getINSTANCE(getApplicationContext())
                    .pwdDao()
                    .getPwdList(userId);


            runOnUiThread(() -> {
                pwds.clear();
                pwds.addAll(pwdsFromDB);
                customAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            // Handle logout action
            logout();
            return true;
        } else if (id == R.id.action_import_file) {
            // Handle import action
            pickFile();
            return true;
        } else if (id == R.id.action_download_file) {
            exportTxt();
            return true;
        } else if (id == R.id.action_profile) {
            showProfile();
            return true;
        } else if (id == R.id.action_about) {
            showAbout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProfile() {
        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    private void showAbout() {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        startActivity(intent);
    }

    private void logout() {
        // Implement your logout logic here
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();

        // Clear userId from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("loginInfo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("loggedUser");
        editor.apply();

        // Navigate back to LoginActivity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity
    }

    private void exportTxt() {
        new Thread(() -> {
            List<Pwd> pwdsFromDB = PwdDatabase.getINSTANCE(getApplicationContext())
                    .pwdDao()
                    .getPwdList(userId);

            String txtData = convertToTxt(pwdsFromDB);
            saveTxtToExternalFile(txtData);
        }).start();
    }

    private String convertToTxt(List<Pwd> pwds) {
        StringBuilder txtBuilder = new StringBuilder();
        for (Pwd pwd : pwds) {
            txtBuilder.append("Title: ").append(pwd.getTitle()).append("\n")
                    .append("Username: ").append(pwd.getUsername()).append("\n")
                    .append("Password: ").append(pwd.getPassword()).append("\n")
                    .append("Notes: ").append(pwd.getNotes()).append("\n\n");
        }
        return txtBuilder.toString();
    }

    private void saveTxtToExternalFile(String txtData) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveTxtToMediaStore(txtData);
        } else {
            saveTxtToLegacyExternalFile(txtData);
        }
    }

    private void saveTxtToLegacyExternalFile(String txtData) {
        if (isExternalStorageWritable()) {
            File externalStorage = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
            if (!externalStorage.exists()) {
                if (!externalStorage.mkdirs()) {
                    runOnUiThread(() -> Toast.makeText(this, "Failed to create directory", Toast.LENGTH_SHORT).show());
                    return;
                }
            }
            File txtFile = new File(externalStorage, "passwords.txt");
            try (FileWriter writer = new FileWriter(txtFile)) {
                writer.write(txtData);
                runOnUiThread(() -> Toast.makeText(this, "Text file saved: " + txtFile.getAbsolutePath(), Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(this, "Failed to save text file", Toast.LENGTH_SHORT).show());
            }
        } else {
            runOnUiThread(() -> Toast.makeText(this, "External storage not writable", Toast.LENGTH_SHORT).show());
        }
    }

    private void saveTxtToMediaStore(String txtData) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "passwords.txt");
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS);

        Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), contentValues);

        try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
            outputStream.write(txtData.getBytes());
            runOnUiThread(() -> Toast.makeText(this, "Text file saved to Documents", Toast.LENGTH_SHORT).show());
        } catch (IOException e) {
            runOnUiThread(() -> Toast.makeText(this, "Failed to save text file", Toast.LENGTH_SHORT).show());
        }
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("text/plain"); // Allow any file type
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_PICK_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                if (isTxtFileNamedPasswords(uri)) {
                    importTxtFile(uri);
                } else {
                    Toast.makeText(this, "Please select a 'passwords.txt' file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean isTxtFileNamedPasswords(Uri uri) {
        String displayName = getFileName(uri);
        return displayName != null && displayName.equals("passwords.txt");
    }

    private String getFileName(Uri uri) {
        String result = null;
        Cursor cursor = null;

        try {
            String[] projection = {OpenableColumns.DISPLAY_NAME};
            cursor = getContentResolver().query(uri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (columnIndex != -1) {
                    result = cursor.getString(columnIndex);
                } else {
                    // Handle case where DISPLAY_NAME column is not found
                    result = uri.getLastPathSegment(); // Fallback to the last path segment
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }


    private void importTxtFile(Uri uri) {
        String txtData = readTxtFile(uri);
        if (txtData != null) {
            List<Pwd> pwdList = parseTxtData(txtData);
            importTxtData(pwdList);
        } else {
            Toast.makeText(this, "Failed to read text file", Toast.LENGTH_SHORT).show();
        }
    }

    private String readTxtFile(Uri uri) {
        StringBuilder text = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor()))) {
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return text.toString();
    }

    private List<Pwd> parseTxtData(String txtData) {
        List<Pwd> pwdList = new ArrayList<>();
        String[] entries = txtData.split("\n\n");

        for (String entry : entries) {
            String[] lines = entry.split("\n");
            if (lines.length >= 4) {
                String title = lines[0].replace("Title: ", "");
                String username = lines[1].replace("Username: ", "");
                String password = lines[2].replace("Password: ", "");
                String notes = lines[3].replace("Notes: ", "");

                Pwd pwd = new Pwd(title, username, password, notes, userId);
                pwdList.add(pwd);
            }
        }
        return pwdList;
    }

    private void importTxtData(List<Pwd> pwdList) {
        new Thread(() -> {
            PwdDatabase db = PwdDatabase.getINSTANCE(getApplicationContext());
            for (Pwd pwd : pwdList) {
                db.pwdDao().upsert(pwd);
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "Passwords imported successfully", Toast.LENGTH_SHORT).show();
                // Update UI or perform any necessary actions after import
                getPwds(); // Refresh the RecyclerView with updated data
            });
        }).start();
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}
