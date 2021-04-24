package edu.sharif.ce.apyugioh.view;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.model.card.*;

import java.nio.file.Files;
import java.nio.file.Path;

public class ShopView extends View {

    public static final int SUCCESS_BUY_CARD = 1;

    public static final int ERROR_CARD_NAME_INVALID = -1;
    public static final int ERROR_MONEY_NOT_ENOUGH = -2;

    {
        successMessages.put(SUCCESS_BUY_CARD, "bought %s successfully.\nbalance: %s");

        errorMessages.put(ERROR_CARD_NAME_INVALID, "there is no card with this name");
        errorMessages.put(ERROR_MONEY_NOT_ENOUGH, "not enough money to buy %s.\nyou currently have %s and you need %s more to buy this card");
    }

    public void showAllCards(String[] cardNames, int[] cardPrices) {
        AsciiTable cards = new AsciiTable();
        cards.addRule();
        cards.addRow("name", "price").setTextAlignment(TextAlignment.CENTER);
        cards.addRule();
        for (int i = 0; i < cardNames.length; i++) {
            cards.addRow(cardNames[i], cardPrices[i]).setTextAlignment(TextAlignment.CENTER);
            cards.addRule();
        }
        System.out.println(cards.render(Math.max(80, ProgramController.getReader().getTerminal().getWidth())));
    }

    public void showCard(Card card) {
        AsciiTable cardInfo = new AsciiTable();
        cardInfo.addRule();
        if (card.getCardType().equals(CardType.MONSTER)) {
            Monster monster = (Monster) card;
            cardInfo.addRow("name", "level", "type", "attack", "defense", "description")
                    .setTextAlignment(TextAlignment.CENTER);
            cardInfo.addRule();
            cardInfo.addRow(monster.getName(), monster.getLevel() + " ★".repeat(monster.getLevel()),
                    Utils.firstUpperOnly(monster.getType().toString()), monster.getAttackPoints(), monster.getDefensePoints(),
                    monster.getDescription()).setTextAlignment(TextAlignment.CENTER);
            cardInfo.addRule();
        } else if (card.getCardType().equals(CardType.SPELL)) {
            Spell spell = (Spell) card;
            cardInfo.addRow("name", "type", "description")
                    .setTextAlignment(TextAlignment.CENTER);
            cardInfo.addRule();
            cardInfo.addRow(spell.getName(), Utils.firstUpperOnly(spell.getProperty().toString()), spell.getDescription())
                    .setTextAlignment(TextAlignment.CENTER);
            cardInfo.addRule();
        } else if (card.getCardType().equals(CardType.TRAP)) {
            Trap trap = (Trap) card;
            cardInfo.addRow("name", "type", "description")
                    .setTextAlignment(TextAlignment.CENTER);
            cardInfo.addRule();
            cardInfo.addRow(trap.getName(), Utils.firstUpperOnly(trap.getProperty().toString()), trap.getDescription())
                    .setTextAlignment(TextAlignment.CENTER);
            cardInfo.addRule();
        }
        System.out.println(cardInfo.render(Math.max(80, ProgramController.getReader().getTerminal().getWidth())));
        String imageName = Utils.firstUpperOnly(card.getName()).replaceAll("\'", "")
                .replaceAll(" ", "");
        Path imagePath = Path.of("assets", "cards", imageName + ".png");
        if (Files.exists(imagePath)) {
            System.out.println(new ImageToASCII("cards/" + imageName, 3).getASCII());
        }
    }

}
