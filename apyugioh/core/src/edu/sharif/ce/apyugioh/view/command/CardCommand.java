package edu.sharif.ce.apyugioh.view.command;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.Arrays;

import edu.sharif.ce.apyugioh.controller.CardFactoryController;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.ShopController;
import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.controller.game.SelectionController;
import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ErrorView;

public class CardCommand {

    public void show(boolean isSelected, String name) {
        if (isSelected) {
            if (!isShowSelectedAvailable()) return;
            SelectionController selectionController = GameController.getGameControllerById(ProgramController.
                    getGameControllerID()).getSelectionController();
            if (selectionController != null) {
                if (selectionController.getCard().isRevealed() || ProgramController.getCurrentPlayerController().
                        getPlayer().getField().isInField(selectionController.getCard())) {
                    ShopController.getInstance().showCard(selectionController.getCard().getCard().getName());
                } else {
                    ErrorView.showError(ErrorView.CARD_NOT_SELECTED);
                }
            } else {
                ErrorView.showError(ErrorView.CARD_NOT_SELECTED);
            }
        } else {
            if (!isAvailable()) return;
            ShopController.getInstance().showCard(name.replaceAll("_", " "));
        }
    }

    public void exportCards(String[] names) {
        if (!isImportExportAvailable()) return;
        if (!isParamsValid(names)) return;
        CardFactoryController.getInstance().export(names);
    }

    public void importCards(String backupPath) {
        if (!isImportExportAvailable()) return;
        FileHandle backupFile = Gdx.files.local("assets/backup/" + backupPath);
        if (!backupFile.exists()) {
            System.out.println(backupPath);
            ErrorView.showError(ErrorView.COMMAND_INVALID);
            return;
        }
        CardFactoryController.getInstance().importCards(backupFile);
    }

    private boolean isAvailable() {
        if (ProgramController.getState().equals(MenuState.SHOP) || ProgramController.getState().equals(MenuState.DECK)
                || ProgramController.getState().equals(MenuState.DUEL)) {
            return true;
        } else {
            ErrorView.showError(ErrorView.COMMAND_INVALID);
            return false;
        }
    }

    private boolean isShowSelectedAvailable() {
        if (ProgramController.getState().equals(MenuState.DUEL)) {
            return ProgramController.getGameControllerID() != -1;
        } else {
            ErrorView.showError(ErrorView.COMMAND_INVALID);
            return false;
        }
    }

    private boolean isImportExportAvailable() {
        if (ProgramController.getState().equals(MenuState.CARD_FACTORY)) {
            return true;
        } else {
            ErrorView.showError(ErrorView.COMMAND_INVALID);
            return false;
        }
    }

    private boolean isParamsValid(String[] names) {
        String[] cardNames = DatabaseManager.getCards().getAllCompleterCardNames();
        for (String name : names) {
            if (Arrays.stream(cardNames).noneMatch(name::equalsIgnoreCase)) {
                ErrorView.showError(ErrorView.COMMAND_INVALID);
                return false;
            }
        }
        return true;
    }

}
