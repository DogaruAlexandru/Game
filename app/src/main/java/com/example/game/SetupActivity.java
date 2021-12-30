package com.example.game;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetupActivity extends AppCompatActivity {

    private Button joinBtn;
    private Button createBtn;
    private ProgressBar loadingPB;
    private EditText codeEdt;
    private TextView titleTV;

    private boolean isCodeMaker = true;
    private boolean codeFound = false;
    private boolean checkTemp = true;
    private String code = "null";
    private String keyValue = "null";

    FirebaseDatabase database;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        auth = FirebaseAuth.getInstance();

        loadingPB = findViewById(R.id.loadingPB);
        codeEdt = findViewById(R.id.codeEdt);
        titleTV = findViewById(R.id.titleTV);

        joinBtn = findViewById(R.id.joinBtn);
        joinBtn.setOnClickListener(v -> joinGameActivity());

        createBtn = findViewById(R.id.createBtn);
        createBtn.setOnClickListener(v -> createGameActivity());
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null)
            signInAnonymously();
    }

    private void signInAnonymously() {
        auth.signInAnonymously().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("AnonymousAuth", "signInAnonymously:success");
                FirebaseUser user = auth.getCurrentUser();
            } else {
                // If sign in fails, display a message to the user.
                Log.w("AnonymousAuth", "signInAnonymously:failure", task.getException());
                Toast.makeText(SetupActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createGameActivity() {
        Intent intent = new Intent(this, GameplayActivity.class);
        startActivity(intent);
    }

    private void joinGameActivity() {
//        Intent intent = new Intent(this, GameplayActivity.class);
//        startActivity(intent);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message2");

        myRef.setValue("Hello, World!");
    }

    private void accepted() {
        Intent intent = new Intent(this, GameplayActivity.class);
        startActivity(intent);

        loadingPB.setVisibility(View.GONE);
        codeEdt.setVisibility(View.VISIBLE);
        titleTV.setVisibility(View.VISIBLE);
        joinBtn.setVisibility(View.VISIBLE);
        createBtn.setVisibility(View.VISIBLE);
    }

//    private void isValueAvailable(DataSnapshot dataSnapshot, String code){
//
//    }
}