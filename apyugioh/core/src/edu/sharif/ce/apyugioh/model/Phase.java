package edu.sharif.ce.apyugioh.model;

public enum Phase {
    DRAW(0),
    STANDBY(1),
    MAIN1(2),
    BATTLE(3),
    MAIN2(4),
    END(5);
    public final int phaseLevel;

    Phase(int phaseLevel) {
        this.phaseLevel = phaseLevel;
    }

    public int getPhaseLevel() {
        return phaseLevel;
    }

    private Phase valueOfLevel(int level) {
        for (Phase e : values()) {
            if (e.phaseLevel == level) {
                return e;
            }
        }
        return null;
    }

    public Phase nextPhase() {
        if (phaseLevel == 5) {
            return Phase.DRAW;
        }
        return valueOfLevel(phaseLevel + 1);
    }
}
