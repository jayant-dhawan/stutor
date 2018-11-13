package com.stutor.android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

class User {

    public String name;
    public String email;
    public String username;
    public String phone;
    public String role;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public User() {
    }

    User(String name, String email, String username, String phone, String role) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.phone = phone;
        this.role = role;
    }
}

public class SignUpActivity extends AppCompatActivity {

    private Spinner spinner;
    private FirebaseAuth firebaseAuth;
    private EditText editTextEmail, editTextPassword, editTextName, editTextMobile, editTextUserName;
    private Button buttonSignup;
    private ProgressDialog progressDialog;
    private DatabaseReference database;
    private String roleSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        spinner = findViewById(R.id.roleSpinner);
        List<String> list = new ArrayList<>();
        list.add("Select");
        list.add("Student");
        list.add("Teacher");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                roleSelected = parent.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference("users");

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextName = findViewById(R.id.name);
        editTextMobile = findViewById(R.id.phone);
        editTextUserName = findViewById(R.id.usernameEditText);
        buttonSignup = findViewById(R.id.signUpButton);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        progressDialog = new ProgressDialog(this);
    }

    private void registerUser() {

        //getting email and password from edit texts
        final String inputEmail = editTextEmail.getText().toString().trim();
        final String inputPassword = editTextPassword.getText().toString().trim();

        //checking if email and passwords are empty
        if (TextUtils.isEmpty(inputEmail)) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_LONG).show();
            return;
        }

        if (roleSelected.equals("Select")) {
            Toast.makeText(this, "Please enter role", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(inputPassword)) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_LONG).show();
            return;
        }

        //if the email and password are not empty
        //displaying a progress dialog

        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();

        //creating a new user
        firebaseAuth.createUserWithEmailAndPassword(inputEmail, inputPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if (task.isSuccessful()) {
                            //display some message here
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(editTextName.getText().toString())
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(SignUpActivity.this, "User profile updated.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            String userId = database.push().getKey();
                            User users = new User(editTextName.getText().toString().trim(),
                                    inputEmail, editTextUserName.getText().toString().trim(), editTextMobile.getText().toString().trim(), roleSelected);
                            database.child(userId).setValue(users);
                            Toast.makeText(SignUpActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                        } else {
                            //display some message here
                            Toast.makeText(SignUpActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }
}
