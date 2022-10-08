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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;

import com.bamboomy.thecubebeast.game.GameActivity;
import com.bamboomy.thecubebeast.game.GameMode;

/**
 * Created by a162299 on 9-10-2015.
 */
public class LevelActivity extends FragmentActivity {

    private static final String TEXT_EASY = "\"Easy\":\n" +
            "In the easy level\n" +
            "each cube has a double\n" +
            "so you could,\n" +
            "- if you want -\n" +
            "start off with solving those doubles first\n" +
            "so you have, for each cube,\n" +
            "a reference to solve the other sides...";

    private static final String TEXT_HARDER = "Harder:\n" +
            "In the harder level\n" +
            "4 of the cubes have a double.\n" +
            "You could start searching those doubles first.\n" +
            "The rest of the cubes are guaranteed\n" +
            "to not have a double.";

    private static final String TEXT_DIFFICULT = "Difficult:\n" +
            "The difficult level is almost the real deal:\n" +
            "Only two cubes have doubles,\n" +
            "the rest of the cubes are guaranteed\n" +
            "to not have a double.";

    private static final String TEXT_THE_REAL_DEAL = "The Real Deal:\n" +
            "It doesn't get harder than this:\n" +
            "Each cube is guaranteed\n" +
            "to not have a double!!!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game);

        findViewById(R.id.easy).setOnClickListener(v -> {
            GameActivity.GAME_MODE = GameMode.EASY;
            Intent myIntent = new Intent(LevelActivity.this, GameActivity.class);
            LevelActivity.this.startActivity(myIntent);
        });

        findViewById(R.id.harder).setOnClickListener(v -> {
            GameActivity.GAME_MODE = GameMode.HARDER;
            Intent myIntent = new Intent(LevelActivity.this, GameActivity.class);
            LevelActivity.this.startActivity(myIntent);
        });

        findViewById(R.id.difficult).setOnClickListener(v -> {
            GameActivity.GAME_MODE = GameMode.DIFFICULT;
            Intent myIntent = new Intent(LevelActivity.this, GameActivity.class);
            LevelActivity.this.startActivity(myIntent);
        });

        findViewById(R.id.real_deal).setOnClickListener(v -> {
            GameActivity.GAME_MODE = GameMode.REAL_DEAL;
            Intent myIntent = new Intent(LevelActivity.this, GameActivity.class);
            LevelActivity.this.startActivity(myIntent);
        });

        findViewById(R.id.info_easy).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(TEXT_EASY).create().show();
        });

        findViewById(R.id.info_harder).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(TEXT_HARDER).create().show();
        });

        findViewById(R.id.info_difficult).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(TEXT_DIFFICULT).create().show();
        });

        findViewById(R.id.info_real_deal).setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(TEXT_THE_REAL_DEAL).create().show();
        });
    }
}