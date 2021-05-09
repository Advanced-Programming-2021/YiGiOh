package edu.sharif.ce.apyugioh.view;

import de.codeshelf.consoleui.prompt.*;
import de.codeshelf.consoleui.prompt.builder.ExpandableChoicePromptBuilder;
import de.codeshelf.consoleui.prompt.builder.ListPromptBuilder;
import de.codeshelf.consoleui.prompt.builder.PromptBuilder;
import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciithemes.u8.U8_Grids;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.model.Phase;
import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.model.card.GameCard;

import java.io.IOException;
import java.util.HashMap;

public class GameView extends View {

    public static final int ERROR_SELECTION_CARD_POSITION_INVALID = -1;
    public static final int ERROR_SELECTION_CARD_NOT_FOUND = -2;
    public static final int ERROR_CARD_NOT_SELECTED = -3;
    public static final int ERROR_SELECTION_NOT_IN_HAND = -4;
    public static final int ERROR_ACTION_NOT_POSSIBLE_IN_THIS_PHASE = -5;
    public static final int ERROR_CANT_CHANGE_CARD_POSITION = -6;
    public static final int ERROR_CANT_ATTACK_WITH_CARD = -7;
    public static final int ERROR_CARD_ALREADY_ATTACKED = -8;
    public static final int ERROR_MONSTER_ZONE_FULL = -9;
    public static final int ERROR_ALREADY_SET_OR_SUMMONED_CARD = -10;
    public static final int ERROR_ALREADY_IN_WANTED_POSITION = -11;
    public static final int ERROR_ALREADY_CHANGED_POSITION_IN_TURN = -12;
    public static final int ERROR_CANT_DIRECTLY_ATTACK = -13;
    public static final int ERROR_NOT_ENOUGH_CARD_TO_TRIBUTE = -14;
    public static final int ERROR_NO_CARD_TO_ATTACK_TO = -15;
    public static final int ERROR_CANT_BE_SUMMONED = -16;
    public static final int ERROR_NOT_FROM_PLACE = -17;
    public static final int ERROR_CANT_ATTACK_IN_FIRST_TURN = -18;
    public static final int ERROR_WRONG_CARD_TYPE = -19;


    public static final int SUCCESS_SELECTION_SUCCESSFUL = 1;
    public static final int SUCCESS_DESELECTION_SUCCESSFUL = 2;
    public static final int SUCCESS_SUMMON_SUCCESSFUL = 3;
    public static final int SUCCESS_SET_SUCCESSFUL = 4;
    public static final int SUCCESS_CHANGE_POSITION_SUCCESSFUL = 5;
    public static final int SUCCESS_FLIP_SUMMON_SUCCESSFUL = 6;
    public static final int SUCCESS_DIRECT_ATTACK_SUCCESSFUL = 7;
    public static final int SUCCESS_EFFECT = 8;

    {
        errorMessages.put(ERROR_SELECTION_CARD_POSITION_INVALID, "card position invalid");
        errorMessages.put(ERROR_SELECTION_CARD_NOT_FOUND, "no card found in the given position");
        errorMessages.put(ERROR_CARD_NOT_SELECTED, "no card was selected");
        errorMessages.put(ERROR_SELECTION_NOT_IN_HAND, "you can't %s this card");
        errorMessages.put(ERROR_ACTION_NOT_POSSIBLE_IN_THIS_PHASE, "action not allowed in this phase");
        errorMessages.put(ERROR_CANT_CHANGE_CARD_POSITION, "you can’t change this card position");
        errorMessages.put(ERROR_CANT_ATTACK_WITH_CARD, "you can't attack with this card");
        errorMessages.put(ERROR_CARD_ALREADY_ATTACKED, "this card already attacked");
        errorMessages.put(ERROR_MONSTER_ZONE_FULL, "monster card zone is full");
        errorMessages.put(ERROR_ALREADY_SET_OR_SUMMONED_CARD, "you already summoned/set on this turn");
        errorMessages.put(ERROR_ALREADY_IN_WANTED_POSITION, "this card is already in the wanted position");
        errorMessages.put(ERROR_ALREADY_CHANGED_POSITION_IN_TURN, "you already changed this card position in this turn");
        errorMessages.put(ERROR_CANT_DIRECTLY_ATTACK, "you can't attack the opponent card directly");
        errorMessages.put(ERROR_NOT_ENOUGH_CARD_TO_TRIBUTE, "there are not enough cards for tribute");
        errorMessages.put(ERROR_NO_CARD_TO_ATTACK_TO, "there is no card to attack here");
        errorMessages.put(ERROR_CANT_BE_SUMMONED, "you can't summon any monster");
        errorMessages.put(ERROR_NOT_FROM_PLACE, "this card is not from %s");
        errorMessages.put(ERROR_CANT_ATTACK_IN_FIRST_TURN, "you can't have a battle in the first turn of game");
        errorMessages.put(ERROR_WRONG_CARD_TYPE, "this card in not a %s");


        successMessages.put(SUCCESS_SELECTION_SUCCESSFUL, "%s selected successfully");
        successMessages.put(SUCCESS_DESELECTION_SUCCESSFUL, "%s deselected successfully");
        successMessages.put(SUCCESS_SUMMON_SUCCESSFUL, "summoned successfully");
        successMessages.put(SUCCESS_SET_SUCCESSFUL, "set successfully");
        successMessages.put(SUCCESS_CHANGE_POSITION_SUCCESSFUL, "monster card position changed successfully");
        successMessages.put(SUCCESS_FLIP_SUMMON_SUCCESSFUL, "flip summoned successfully");
        successMessages.put(SUCCESS_DIRECT_ATTACK_SUCCESSFUL, "you opponent receives %s battle damage");
        successMessages.put(SUCCESS_EFFECT, "%s effect successfully done");
    }

    public void showGraveyard(Player player) {
        AsciiTable graveyardTable = new AsciiTable();
        graveyardTable.addRule();
        graveyardTable.addRow("", "name");
        graveyardTable.addStrongRule();
        int counter = 0;
        for (GameCard card : player.getField().getGraveyard()) {
            graveyardTable.addRow(++counter, card.getCard().getName());
            graveyardTable.addRule();
        }
        graveyardTable.setTextAlignment(TextAlignment.CENTER);
        graveyardTable.getContext().setGrid(U8_Grids.borderStrongDoubleLight());
        System.out.println(graveyardTable.render(Math.max(80, ProgramController.getReader().getTerminal().getWidth())));
    }

    public boolean confirm(String message) {
        String[] options = {"Yes", "No"};
        return promptChoice(options, "").equalsIgnoreCase("yes");
    }

    public String promptChoice(String[] options) {
        return promptChoice(options, "Choose the card you want");
    }

    public String promptChoice(String[] options, String message) {
        ConsolePrompt prompt = new ConsolePrompt();
        PromptBuilder builder = prompt.getPromptBuilder();
        ListPromptBuilder choicePrompt = builder.createListPrompt().name("tribute").message(message);
        for (String option : options) {
            choicePrompt = choicePrompt.newItem(option).add();
        }
        choicePrompt = choicePrompt.newItem("cancel").add();
        builder = choicePrompt.addPrompt();
        try {
            HashMap<String, ? extends PromtResultItemIF> results = prompt.prompt(builder.build());
            String result = ((ListResult) results.get("tribute")).getSelectedId();
            return result.equals("cancel") ? null : result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void showPhase(Phase phase) {
        Utils.printInfo("Phase: " + Utils.firstUpperOnly(phase.name().replaceAll("(\\d)", " $1")));
    }

    public void showBoard(Player firstPlayer, Player secondPlayer) {
        AsciiTable board = new AsciiTable();
        addRivalPlayerToBoard(secondPlayer, board);
        board.addStrongRule();
        board.addStrongRule();
        board.addStrongRule();
        addCurrentPlayerToBoard(firstPlayer, board);
        board.setTextAlignment(TextAlignment.CENTER);
        board.getContext().setGrid(U8_Grids.borderStrongDoubleLight());
        System.out.println(board.render(Math.max(80, ProgramController.getReader().getTerminal().getWidth())));
    }

    private void addRivalPlayerToBoard(Player secondPlayer, AsciiTable board) {
        board.addRule();
        board.addRow(null, null, null, secondPlayer.getUser().getNickname(), null, null, secondPlayer.getLifePoints());
        board.addRule();
        String[] secondHand = new String[7];
        for (int i = 0; i < 7; i++) {
            if (secondPlayer.getField().getHand().size() > i) {
                secondHand[i] = "C";
            } else {
                secondHand[i] = "";
            }
        }
        board.addRow((Object[]) secondHand);
        board.addRule();
        board.addRow(secondPlayer.getField().getDeck().size(), null, null, null, null, null, "");
        board.addRule();
        board.addRow("", getRivalSpell(secondPlayer, 3), getRivalSpell(secondPlayer, 1),
                getRivalSpell(secondPlayer, 0), getRivalSpell(secondPlayer, 2), getRivalSpell(secondPlayer, 4), "");
        board.addRule();
        board.addRow("", getRivalMonster(secondPlayer, 3), getRivalMonster(secondPlayer, 1),
                getRivalMonster(secondPlayer, 0), getRivalMonster(secondPlayer, 2), getRivalMonster(secondPlayer, 4), "");
        board.addRule();
        board.addRow(secondPlayer.getField().getGraveyard().size(), null, null, null, null, "", getFieldZone(secondPlayer));
    }

    private void addCurrentPlayerToBoard(Player firstPlayer, AsciiTable board) {
        board.addRow(getFieldZone(firstPlayer), null, null, null, null, "", firstPlayer.getField().getGraveyard().size());
        board.addRule();
        board.addRow("", getFriendlyMonster(firstPlayer, 4), getFriendlyMonster(firstPlayer, 2),
                getFriendlyMonster(firstPlayer, 0), getFriendlyMonster(firstPlayer, 1), getFriendlyMonster(firstPlayer, 3), "");
        board.addRule();
        board.addRow("", getFriendlySpell(firstPlayer, 4), getFriendlySpell(firstPlayer, 2),
                getFriendlySpell(firstPlayer, 0), getFriendlySpell(firstPlayer, 1), getFriendlySpell(firstPlayer, 3), "");
        board.addRule();
        board.addRow(null, null, null, null, null, "", firstPlayer.getField().getDeck().size());
        board.addRule();
        String[] firstHand = new String[7];
        for (int i = 0; i < 7; i++) {
            if (firstPlayer.getField().getHand().size() > i) {
                firstHand[i] = firstPlayer.getField().getHand().get(i).getCard().getName();
            } else {
                firstHand[i] = "";
            }
        }
        board.addRow((Object[]) firstHand);
        board.addRule();
        board.addRow(null, null, null, firstPlayer.getUser().getNickname(), null, null, firstPlayer.getLifePoints());
        board.addRule();
    }

    private String getFieldZone(Player player) {
        if (player.getField().getFieldZone() == null) {
            return "";
        } else {
            return player.getField().getFieldZone().getCard().getName();
        }
    }

    private String getRivalMonster(Player player, int position) {
        if (player.getField().getMonsterZone()[position] == null) {
            return "";
        } else {
            if (player.getField().getMonsterZone()[position].isRevealed()) {
                return (player.getField().getMonsterZone()[position].isFaceDown() ? "Defense" : "Offense")
                        + "<br>" + player.getField().getMonsterZone()[position].getCard().getName();
            } else {
                return (player.getField().getMonsterZone()[position].isFaceDown() ? "Defense" : "Offense")
                        + "<br>Hidden Monster";
            }
        }
    }

    private String getRivalSpell(Player player, int position) {
        if (player.getField().getSpellZone()[position] == null) {
            return "";
        } else {
            if (player.getField().getSpellZone()[position].isRevealed()) {
                return player.getField().getSpellZone()[position].getCard().getName();
            } else {
                return "Hidden Spell/Trap";
            }
        }
    }

    private String getFriendlyMonster(Player player, int position) {
        if (player.getField().getMonsterZone()[position] == null) {
            return "";
        } else {
            return (player.getField().getMonsterZone()[position].isFaceDown() ? "Defense" : "Offense")
                    + "<br>" + player.getField().getMonsterZone()[position].getCard().getName();
        }
    }

    private String getFriendlySpell(Player player, int position) {
        if (player.getField().getSpellZone()[position] == null) {
            return "";
        } else {
            return player.getField().getSpellZone()[position].getCard().getName();
        }
    }
}
