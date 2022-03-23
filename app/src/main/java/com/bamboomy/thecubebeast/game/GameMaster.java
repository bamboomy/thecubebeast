package com.bamboomy.thecubebeast.game;

import static com.bamboomy.thecubebeast.MainActivity.TAG;
import static java.lang.Thread.sleep;

import android.util.Log;

public class GameMaster {

    private static GameMaster instance = null;

    private int numberShown = 0;

    private Side[] sides = new Side[Tupple.MAX];

    private static final int NUMBER_OF_CUBES = 8;
    private static final int NUMBER_OF_SIDES = 6;

    private Tupple[] tupples;

    private int taps;
    private long rawTime, begin = System.currentTimeMillis();

    private boolean locked = false;

    private boolean isOneTupleSolved = false;

    private boolean hideable = false;

    private BeastRenderer beastRenderer;

    private Hider hider = null;

    private GameMaster() {
        // Exists only to defeat instantiation.

        Tupple.reset();

        tupples = new Tupple[(NUMBER_OF_CUBES * NUMBER_OF_SIDES) / Tupple.MAX];

        for (int i = 0; i < tupples.length; i++) {

            tupples[i] = new Tupple();
        }

        taps = 0;

        isOneTupleSolved = false;
    }

    public synchronized static GameMaster getInstance() {
        if (instance == null) {
            instance = new GameMaster();
        }
        return instance;
    }

    Tupple getTupple(Side side, boolean chooseUnchosen) {

        Tupple current = tupples[(int) (Math.random() * tupples.length)];

        while (current.isFull() || chooseUnchosen != current.isUnchosen()) {

            current = tupples[(int) (Math.random() * tupples.length)];
        }

        current.add(side);

        return current;
    }

    synchronized boolean canShow(Side side) {

        if (numberShown >= Tupple.MAX) {

            Log.d(TAG, "going to return false: " + numberShown);

            hide();
        }

        if (locked) {
            return false;
        }

        sides[numberShown++] = side;

        updateMetrics();

        if (numberShown == Tupple.MAX) {

            if (checkWin()) {

                Side solved = null;

                for (Side hide : sides) {

                    hide.hide();

                    solved = hide;
                }

                numberShown = 0;

                Tupple possiblyLast = solved.getTupple();

                possiblyLast.setSolved();

                possiblyLast.updatePicture(Side.GOOD);

                if (Tupple.isAllSolved()) {

                    //$$$ victoriouzzz

                }

            } else {

                hideable = true;

                hider = new Hider();

                new Thread(hider).start();
            }
        }

        Log.d(TAG, "going to return true: " + numberShown);

        return true;
    }

    private synchronized void hide() {

        if (hideable) {

            for (Side revert : sides) {

                if (revert != null) {
                    revert.reset();
                }
            }

            numberShown = 0;

            locked = hideable = false;

            beastRenderer.addMiss();

            if(hider != null){

                hider.cancelled = true;
                hider = null;
            }
        }
    }

    private void updateMetrics() {

        long now = System.currentTimeMillis();

        rawTime = now - begin;

        taps++;
    }

    private boolean checkWin() {

        Tupple tupple = sides[0].getTupple();

        int index = 1;

        boolean win = true;

        while (win && index < Tupple.MAX) {

            win = sides[index].getTupple().equals(tupple);
            index++;
        }

        return win;
    }

    synchronized boolean hide(Side side) {

        if (locked) {
            return false;
        }

        updateMetrics();

        //TODO: refactor if 3...

        if (sides[0].equals(side)) {

            sides[0] = sides[1];

        } else if (!sides[1].equals(side)) {

            throw new RuntimeException("this isn't right...");
        }

        sides[1] = null;

        //ugly fix for what is probably a very hard race condition that happens to often
        if (numberShown > 0) {
            numberShown--;
        }

        return true;
    }

    static void reset() {

        instance = new GameMaster();
    }

    static void dirtyReset() {

        BeastRenderer temp = instance.beastRenderer;

        instance = new GameMaster();

        instance.beastRenderer = temp;
    }

    void oneSolved() {

        isOneTupleSolved = true;
    }

    void setRenderer(BeastRenderer beastRenderer) {

        this.beastRenderer = beastRenderer;
    }

    private class Hider implements Runnable {

        boolean cancelled = false;

        @Override
        public void run() {

            locked = true;

            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.d(TAG, "running...");

            if (cancelled) {
                return;
            }

            hide();
        }
    }
}
