package edu.sharif.ce.apyugioh.view;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.asciithemes.u8.U8_Grids;
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
        cards.addRow("name", "price");
        cards.addRule();
        for (int i = 0; i < cardNames.length; i++) {
            cards.addRow(cardNames[i], cardPrices[i]);
            cards.addRule();
        }
        cards.setTextAlignment(TextAlignment.CENTER);
        cards.getContext().setGrid(U8_Grids.borderDouble());
        System.out.println(cards.render(Math.max(80, ProgramController.getReader().getTerminal().getWidth())));
    }

    public void showCard(Card card) {
        showCardImage(card);
        showCardStats(card);
    }

    private void showCardStats(Card card) {
        AsciiTable cardInfo = new AsciiTable();
        CWC_LongestLine cwc = new CWC_LongestLine();
        cardInfo.addRule();
        if (card.getCardType().equals(CardType.MONSTER)) {
            Monster monster = (Monster) card;
            addMonsterRow(cardInfo, cwc, monster);
        } else if (card.getCardType().equals(CardType.SPELL)) {
            Spell spell = (Spell) card;
            addSpellTrapRow(cardInfo, cwc, spell.getName(), spell.getProperty(), spell.getDescription());
        } else if (card.getCardType().equals(CardType.TRAP)) {
            Trap trap = (Trap) card;
            addSpellTrapRow(cardInfo, cwc, trap.getName(), trap.getProperty(), trap.getDescription());
        }
        cardInfo.addRule();
        cardInfo.setTextAlignment(TextAlignment.CENTER);
        cardInfo.getContext().setGrid(U8_Grids.borderDouble());
        cardInfo.getRenderer().setCWC(cwc);
        System.out.println(cardInfo.render(Math.max(80, ProgramController.getReader().getTerminal().getWidth())));
    }

    private void addMonsterRow(AsciiTable cardInfo, CWC_LongestLine cwc, Monster monster) {
        cardInfo.addRow("name", "level", "type", "attack", "defense", "description");
        cardInfo.addRule();
        cardInfo.addRow(monster.getName(), monster.getLevel() + "<br>" + "★".repeat(monster.getLevel()),
                Utils.firstUpperOnly(monster.getType().toString()), monster.getAttackPoints(),
                monster.getDefensePoints(), monster.getDescription());
        cwc.add(4, 40).add(5, 20).add(4, 20).add(6, 20).add(7, 20).add(11, ProgramController.getReader().getTerminal().getWidth() / 2);
    }

    private void addSpellTrapRow(AsciiTable cardInfo, CWC_LongestLine cwc, String name, SpellProperty property, String description) {
        cardInfo.addRow("name", "type", "description");
        cardInfo.addRule();
        cardInfo.addRow(name, Utils.firstUpperOnly(property.toString()), description);
        cwc.add(4, 20).add(4, 20).add(11, ProgramController.getReader().getTerminal().getWidth() / 2);
    }

    private void showCardImage(Card card) {
        String imageName = Utils.firstUpperOnly(card.getName()).replaceAll("[\',]", "")
                .replaceAll(" ", "");
        Path imagePath = Path.of("assets", "cards", imageName + ".png");
        if (Files.exists(imagePath)) {
            System.out.println(new ImageToASCII("cards/" + imageName, Utils.getTerminalScale(3), false).getASCII());
        }
    }

}
