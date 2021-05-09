package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.model.card.CardLocation;
import edu.sharif.ce.apyugioh.view.ErrorView;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

@Command(name = "select", description = "card select commands")
public class SelectCommand implements Callable<Integer> {

    @Option(names = {"-d", "--deselect"}, description = "deselect the selected card", paramLabel = "deselect")
    boolean isDeselect;

    @Option(names = {"-o", "--opponent"}, description = "select from rival zone", paramLabel = "rival")
    boolean isRival;

    @Option(names = {"-m", "--monster"}, description = "select from monster zone", paramLabel = "monster zone")
    boolean isMonster;

    @Option(names = {"-s", "--spell"}, description = "select from spell zone", paramLabel = "spell zone")
    boolean isSpellZone;

    @Option(names = {"-f", "--field"}, description = "select from field zone", paramLabel = "field zone")
    boolean isFieldZone;

    @Option(names = {"-h", "--hand"}, description = "select from hand", paramLabel = "hand")
    boolean isHand;

    @Parameters(arity = "0..1", defaultValue = "-1", paramLabel = "position", description = "card position")
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
