package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.card.ShopCards;
import edu.sharif.ce.apyugioh.view.CardFactoryView;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class CardFactoryController {

    @Getter
    private static CardFactoryController instance;
    private static CardFactoryView view;
    private static Logger logger;

    static {
        instance = new CardFactoryController();
        view = new CardFactoryView();
        logger = LogManager.getLogger(CardFactoryController.class);
    }

    private CardFactoryController() {
    }

    public void export(String[] names) {
        ShopCards currentCards = DatabaseManager.getCards();
        ShopCards exportCards = new ShopCards();
        for (String name : names) {
            name = name.replaceAll("_", " ");
            exportCards.addCard(currentCards.getCardByName(name), currentCards.getCardPrice(name));
        }
        try {
            Path exportPath = DatabaseManager.exportShopCards(exportCards);
            logger.info("exported {}", Path.of("assets", "backup").relativize(exportPath).toString());
            ProgramController.updateCompleter();
            view.showSuccess(CardFactoryView.SUCCESS_CARDS_EXPORTED_SUCCESSFULLY);
        } catch (Exception e) {
            logger.error("Exception caused by: {}\nDetails: {}", e.getCause(), e.getMessage());
            Utils.printError("couldn't export cards");
        }
    }

    public void importCards(Path backupPath) {
        try {
            if (DatabaseManager.importShopCards(backupPath)) {
                ProgramController.updateCompleter();
                logger.info("imported {}", Path.of("assets", "backup").relativize(backupPath).toString());
                view.showSuccess(CardFactoryView.SUCCESS_CARDS_IMPORTED_SUCCESSFULLY);
            } else {
                view.showError(CardFactoryView.ERROR_BACKUP_PATH_INVALID);
            }
        } catch (Exception e) {
            logger.error("Exception caused by: {}\nDetails: {}", e.getCause(), e.getMessage());
            Utils.printError("corrupted database");
        }
    }
}
