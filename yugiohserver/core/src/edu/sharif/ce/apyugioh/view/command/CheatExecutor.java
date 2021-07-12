package edu.sharif.ce.apyugioh.view.command;

import com.strongjoshua.console.CommandExecutor;

import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.model.Cheats;

public class CheatExecutor extends CommandExecutor {

    public void money_amount(String option) {
        GameController.getGameControllerById(ProgramController.getGameControllerID())
                .getCheatController().cheat(Cheats.MONEY_AMOUNT, new String[]{option});
    }

    public void life_points_amount(String option) {
        GameController.getGameControllerById(ProgramController.getGameControllerID())
                .getCheatController().cheat(Cheats.LIFE_POINTS_AMOUNT, new String[]{option});
    }

    public void instant_win() {
        GameController.getGameControllerById(ProgramController.getGameControllerID())
                .getCheatController().cheat(Cheats.INSTANT_WIN, new String[0]);
    }

    public void draw_card(String option) {
        GameController.getGameControllerById(ProgramController.getGameControllerID())
                .getCheatController().cheat(Cheats.DRAW_CARD, new String[]{option});
    }

    public void summon_monster(String option) {
        GameController.getGameControllerById(ProgramController.getGameControllerID())
                .getCheatController().cheat(Cheats.SUMMON_MONSTER, new String[]{option});
    }

    public void set_monster(String option) {
        GameController.getGameControllerById(ProgramController.getGameControllerID())
                .getCheatController().cheat(Cheats.SET_MONSTER, new String[]{option});
    }

    public void set_spell(String option) {
        GameController.getGameControllerById(ProgramController.getGameControllerID())
                .getCheatController().cheat(Cheats.SET_SPELL, new String[]{option});
    }

}
