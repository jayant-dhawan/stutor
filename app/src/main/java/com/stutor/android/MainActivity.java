package com.stutor.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.*;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //initializing firebase auth object
                mAuth = FirebaseAuth.getInstance();

        //if getCurrentUser does not returns null
        if(mAuth.getCurrentUser() != null){
            //that means user is already logged in
            //and open profile activity
            Toast.makeText(getApplicationContext(), "User Logged In", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Not Logged In", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        }
    }
}
