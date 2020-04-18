package com.example.myproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView signUpTextView;
    private Button signInButton;
    private EditText emailEditText, passEditText;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //hide title
//        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("USERS");

        signUpTextView = findViewById(R.id.signUpTextId);
        signInButton = findViewById(R.id.signInButtonId);
        emailEditText = findViewById(R.id.emailEditTextId);
        passEditText = findViewById(R.id.passEditTextId);

        signUpTextView.setOnClickListener(this);
        signInButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.signUpTextId:
                Intent signUpActivity = new Intent(SignInActivity.this, SignUpActivity.class);
                signUpActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(signUpActivity);
                break;

            case R.id.signInButtonId:
                String email = emailEditText.getText().toString().trim();
                String password = passEditText.getText().toString().trim();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
                    signInMethod(email, password);
                else
                    Toast.makeText(getApplicationContext(), "Field is empty", Toast.LENGTH_SHORT).show();

        }
    }
    //sign in
    private void signInMethod(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                    checkCurrentUser();
                else
                    Toast.makeText(getApplicationContext(), "Error in Sign in", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkCurrentUser() {
        final String user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        //Uid read from real time database
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id))
                {
                    Intent mainIntent = new Intent(SignInActivity.this, MainActivity.class);
                    mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to read data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
