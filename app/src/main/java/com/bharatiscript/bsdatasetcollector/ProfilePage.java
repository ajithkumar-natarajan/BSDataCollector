package com.bharatiscript.bsdatasetcollector;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class ProfilePage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        TextView textView = (TextView) findViewById(R.id.bharatiSite);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
