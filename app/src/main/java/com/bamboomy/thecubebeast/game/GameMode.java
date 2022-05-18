package com.bamboomy.thecubebeast.game;

import lombok.Getter;

public enum GameMode {

    EASY(8), HARDER(4), DIFFICULT(2), REAL_DEAL(0);

    @Getter
    private int doubles;

    GameMode(int doubles){
        this.doubles = doubles;
    }
}
