package edu.sharif.ce.apyugioh.view.command;

import edu.sharif.ce.apyugioh.controller.CardFactoryController;
import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.ShopController;
import edu.sharif.ce.apyugioh.model.MenuState;
import edu.sharif.ce.apyugioh.view.ErrorView;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Command;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Command(name = "card", mixinStandardHelpOptions = true, description = "card commands")
public class CardCommand {

    @Command(name = "show", description = "show all cards")
    public void show(@Option(names = {"-s", "--selected"}, description = "show selected card") boolean isSelected,
                     @Parameters(index = "0", description = "card name") String name) {
        if (!isAvailable()) return;
        ShopController.getInstance().showCard(name.replaceAll("_", " "));
    }

    @Command(name = "export", description = "export cards")
    public void exportCards(@Parameters(arity = "1..*") String[] names) {
        if (!isImportExportAvailable()) return;
        if (!isParamsValid(names)) return;
        CardFactoryController.getInstance().export(names);
    }

    @Command(name = "import", description = "import cards backup")
    public void importCards(@Parameters(index = "0") Path backupPath) {
        if (!isImportExportAvailable()) return;
        backupPath = Path.of("assets", "backup").resolve(backupPath);
        if (!Files.exists(backupPath)) {
            System.out.println(backupPath);
            ErrorView.showError(ErrorView.COMMAND_INVALID);
            return;
        }
        CardFactoryController.getInstance().importCards(backupPath);
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
