/*
 * Copyright (c) 2016 Sander Theetaert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.bamboomy.thecubebeast;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bamboomy.thecubebeast.R;
import com.bamboomy.thecubebeast.game.GameActivity;
import com.bamboomy.thecubebeast.game.GameMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by a162299 on 9-10-2015.
 */
public class MenuActivity extends FragmentActivity {

    private TextView easy, harder, difficult, realDeal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu);

        easy = findViewById(R.id.easy);

        easy.setOnClickListener(v -> {
            GameActivity.GAME_MODE = GameMode.EASY;
            Intent myIntent = new Intent(MenuActivity.this, GameActivity.class);
            MenuActivity.this.startActivity(myIntent);
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("test\ntest");

        builder.create().show();

        harder = findViewById(R.id.harder);

        harder.setOnClickListener(v -> {
            GameActivity.GAME_MODE = GameMode.HARDER;
            Intent myIntent = new Intent(MenuActivity.this, GameActivity.class);
            MenuActivity.this.startActivity(myIntent);
        });

        difficult = findViewById(R.id.difficult);

        difficult.setOnClickListener(v -> {
            GameActivity.GAME_MODE = GameMode.DIFFICULT;
            Intent myIntent = new Intent(MenuActivity.this, GameActivity.class);
            MenuActivity.this.startActivity(myIntent);
        });

        realDeal = findViewById(R.id.real_deal);

        realDeal.setOnClickListener(v -> {
            GameActivity.GAME_MODE = GameMode.REAL_DEAL;
            Intent myIntent = new Intent(MenuActivity.this, GameActivity.class);
            MenuActivity.this.startActivity(myIntent);
        });
    }
}