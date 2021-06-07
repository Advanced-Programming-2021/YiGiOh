package edu.sharif.ce.apyugioh.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoundResult {
    private boolean isFirstPlayerWin;
    private int firstPlayerLifePoints;
    private int secondPlayerLifePoints;
}
