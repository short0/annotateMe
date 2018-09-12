package com.example.user.mobilemicroscopy;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class UserSelectionActivity extends AppCompatActivity {

    ImageView selectLoginButton;

    ImageView selectGuestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);

        selectLoginButton = (ImageView) findViewById(R.id.select_login_button);
        selectGuestButton = (ImageView) findViewById(R.id.select_guest_button);

        selectLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserSelectionActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        selectGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserSelectionActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
