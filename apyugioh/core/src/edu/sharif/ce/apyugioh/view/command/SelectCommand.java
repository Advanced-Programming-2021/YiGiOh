package edu.sharif.ce.apyugioh.view.command;

import java.util.concurrent.Callable;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.model.card.CardLocation;
import edu.sharif.ce.apyugioh.view.ErrorView;

public class SelectCommand implements Callable<Integer> {

    boolean isDeselect;
    boolean isRival;
    boolean isMonster;
    boolean isSpellZone;
    boolean isFieldZone;
    boolean isHand;
    int position;

    @Override
    public Integer call() {
        if (!isAvailable()) return -1;
        CardLocation location = new CardLocation();
        if (isDeselect) {
            if (!isMonster && !isSpellZone && !isFieldZone && !isHand && position == -1) {
                ProgramController.getCurrentPlayerController().deselect();
                return 0;
            } else {
                ErrorView.showError(ErrorView.COMMAND_INVALID);
                return -1;
            }
        } else {
            if (isMonster && !isSpellZone && !isFieldZone && !isHand && position != -1) {
                location.setFromMonsterZone(true);
                location.setFromEnemy(isRival);
                location.setPosition(position - 1);
            } else if (isSpellZone && !isMonster && !isFieldZone && !isHand && position != -1) {
                location.setFromSpellZone(true);
                location.setFromEnemy(isRival);
                location.setPosition(position - 1);
            } else if (isFieldZone && !isMonster && !isSpellZone && !isHand && position == -1) {
                location.setFromFieldZone(true);
                location.setFromEnemy(isRival);
            } else if (isHand && !isMonster && !isFieldZone && !isSpellZone && position != -1) {
                location.setInHand(true);
                location.setFromEnemy(isRival);
                location.setPosition(position - 1);
            } else {
                ErrorView.showError(ErrorView.COMMAND_INVALID);
                return -1;
            }
        }
        ProgramController.getCurrentPlayerController().select(location);
        return 0;
    }

    private boolean isAvailable() {
        return AttackCommand.isDuelCommandsAvailable();
    }

}
