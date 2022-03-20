package com.bamboomy.thecubebeast;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bamboomy.thecubebeast.game.GameActivity;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "beast";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent myIntent = new Intent(MainActivity.this, GameActivity.class);

        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        MainActivity.this.startActivity(myIntent);
    }
}