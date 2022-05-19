package com.bamboomy.thecubebeast.game;

import android.graphics.Bitmap;
import android.util.Log;

public class Tupple {

    static final int MAX = 2;
    private Side[] sides = new Side[MAX];
    private int index = 0;
    private Bitmap bitmap;

    private static int numberOfUnsolvedTupples = 0;

    private static boolean isOneSolved = false;

    private boolean unchosen = true, solved = false;

    static void reset() {

        numberOfUnsolvedTupples = 0;
    }

    Tupple() {

        numberOfUnsolvedTupples++;
    }

    void add(Side other) {

        sides[index++] = other;

        unchosen = false;
    }

    boolean isFull() {

        return index == MAX;
    }

    boolean isBrandNew() {

        return index == 0;
    }

    boolean isNew() {

        return index == 1;
    }

    void setPicture(Bitmap picture) {

        bitmap = picture;
    }

    Bitmap getBitmap() {

        return bitmap;
    }

    void setSolved() {

        numberOfUnsolvedTupples--;

        isOneSolved = true;

        solved = true;

        for (Side side: sides){
            side.destroy();
        }

        GameMaster.getInstance().oneSolved();
    }

    static boolean isAllSolved() {

        Log.d("beast", "unsolved: " + numberOfUnsolvedTupples);

        return numberOfUnsolvedTupples == 0;
    }

    static boolean popIsOneSolved() {

        if (isOneSolved) {

            isOneSolved = false;

            return true;
        }

        return false;
    }

    void updatePicture(Bitmap good) {

        bitmap = good;

        Log.d("beast", "update");

        for (Side side : sides) {

            if (side != null) {

                side.setPicture(good);
            }
        }
    }

    boolean isUnchosen() {

        return unchosen;
    }

    boolean isSolved() {

        return solved;
    }
}
