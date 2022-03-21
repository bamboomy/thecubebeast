package com.bamboomy.thecubebeast.game;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class HitInformation {

    private boolean found;
    private Cube cube;
    private Side side;

    HitInformation(){
        found = false;
    }
}
