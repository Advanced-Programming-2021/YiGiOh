package edu.sharif.ce.apyugioh.controller;

import com.badlogic.gdx.files.FileHandle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.ShopCards;
import edu.sharif.ce.apyugioh.view.CardFactoryView;
import lombok.Getter;

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
            FileHandle exportPath = DatabaseManager.exportShopCards(exportCards);
            logger.info("exported {}", exportPath.toString());
            view.showSuccess(CardFactoryView.SUCCESS_CARDS_EXPORTED_SUCCESSFULLY);
        } catch (Exception e) {
            logger.error("Exception caused by: {}\nDetails: {}", e.getCause(), e.getMessage());
            Utils.printError("couldn't export cards");
        }
    }

    public void importCards(FileHandle backupPath) {
        try {
            if (DatabaseManager.importShopCards(backupPath)) {
                logger.info("imported {}", backupPath.toString());
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
