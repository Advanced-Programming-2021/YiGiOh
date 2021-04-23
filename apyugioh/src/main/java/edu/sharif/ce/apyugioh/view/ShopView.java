package edu.sharif.ce.apyugioh.view;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.skb.interfaces.transformers.textformat.TextAlignment;

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
        AsciiTable scoreboard = new AsciiTable();
        scoreboard.addRule();
        scoreboard.addRow("name", "price").setTextAlignment(TextAlignment.CENTER);
        scoreboard.addRule();
        for (int i = 0; i < cardNames.length; i++) {
            scoreboard.addRow(cardNames[i], cardPrices[i]).setTextAlignment(TextAlignment.CENTER);
            scoreboard.addRule();
        }
        System.out.println(scoreboard.render());
    }

}
