package com.example.user.mobilemicroscopy;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class UserSelectionActivity extends AppCompatActivity {
    /**
     * Login button
     */
    ImageView selectLoginButton;

    /**
     * Guest button
     */
    ImageView selectGuestButton;

    /**
     * About Us button
     */
    ImageView aboutUsButton;

    /**
     * onCreate method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_selection);

        getSupportActionBar().hide();

        // find all bottom menu buttons
        selectLoginButton = (ImageView) findViewById(R.id.select_login_button);
        selectGuestButton = (ImageView) findViewById(R.id.select_guest_button);
        aboutUsButton = (ImageView) findViewById(R.id.aboutUs_button);


        aboutUsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAboutUs();
            }
        });


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


    /**
     * About Us Message
     */
    public void onAboutUs() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("About Us");
        builder.setMessage("This app is brought to you by:\n\nTeam Kiot\n\nJon Callus\nLong Nguyen\nDaniel Mitchell\nGuy Sowden");
        // Add the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            }
        });
        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
