package com.example.user.mobilemicroscopy.help;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;
import com.example.user.mobilemicroscopy.R;


public class HelpDetailsActivity extends AppCompatActivity {


        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView (R.layout.help_layout);

            TextView textView = (TextView) findViewById (R.id.help_text);
            textView.setText (Html.fromHtml (getString (R.string.topic_details)));
        }




    }
