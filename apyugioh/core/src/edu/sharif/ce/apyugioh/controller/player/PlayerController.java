package edu.sharif.ce.apyugioh.controller.player;

import edu.sharif.ce.apyugioh.controller.game.GameController;
import edu.sharif.ce.apyugioh.controller.game.SelectionController;
import edu.sharif.ce.apyugioh.model.DatabaseManager;
import edu.sharif.ce.apyugioh.model.Phase;
import edu.sharif.ce.apyugioh.model.Player;
import edu.sharif.ce.apyugioh.model.card.*;
import edu.sharif.ce.apyugioh.view.GameView;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public abstract class PlayerController {

    @Setter
    protected int gameControllerID;
    protected Player player;
    protected List<GameCard> availableCards;

    public PlayerController(Player player) {
        this.player = player;
    }

    public void select(CardLocation location) {
        if (isZoneSelected(location, location.isFromMonsterZone(), player.getField().getMonsterZone())) return;
        if (isZoneSelected(location, location.isFromSpellZone(), player.getField().getSpellZone())) return;
        if (isFieldZoneSelected(location)) return;
        if (isHandSelected(location)) return;
    }

    private boolean isHandSelected(CardLocation location) {
        if (location.isInHand()) {
            if (location.getPosition() >= player.getField().getHand().size() || location.getPosition() < 0) {
                GameController.getView().showError(GameView.ERROR_SELECTION_CARD_POSITION_INVALID);
            } else if (player.getField().getHand().get(location.getPosition()) == null) {
                GameController.getView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            } else {
                getGameController().select(location);
                if (!isAI()) {
                    GameController.getView().showSuccess(GameView.SUCCESS_SELECTION_SUCCESSFUL,
                            getSelectionController().getCard().getCard().getName());
                }
            }
            return true;
        }
        return false;
    }

    private boolean isFieldZoneSelected(CardLocation location) {
        if (location.isFromFieldZone()) {
            if (player.getField().getFieldZone() == null) {
                GameController.getView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            } else {
                getGameController().select(location);
                if (!isAI()) {
                    GameController.getView().showSuccess(GameView.SUCCESS_SELECTION_SUCCESSFUL,
                            getSelectionController().getCard().getCard().getName());
                }
            }
            return true;
        }
        return false;
    }

    private boolean isZoneSelected(CardLocation location, boolean isZoneSelected, GameCard[] zone) {
        if (isZoneSelected) {
            if (location.getPosition() > 4 || location.getPosition() < 0) {
                GameController.getView().showError(GameView.ERROR_SELECTION_CARD_POSITION_INVALID);
            } else if (zone[location.getPosition()] == null) {
                GameController.getView().showError(GameView.ERROR_SELECTION_CARD_NOT_FOUND);
            } else {
                getGameController().select(location);
                if (!isAI()) {
                    GameController.getView().showSuccess(GameView.SUCCESS_SELECTION_SUCCESSFUL,
                            getSelectionController().getCard().getCard().getName());
                }
            }
            return true;
        }
        return false;
    }

    public void deselect() {
        if (getSelectionController() == null) {
            GameController.getView().showError(GameView.ERROR_CARD_NOT_SELECTED);
        } else {
            if (!isAI()) {
                GameController.getView().showSuccess(GameView.SUCCESS_DESELECTION_SUCCESSFUL,
                        getSelectionController().getCard().getCard().getName());
            }
            getGameController().deselect();
        }
    }

    public void set() {
        getGameController().set();
        getGameController().deselect();
    }

    public void summon() {
        getGameController().summon();
        getGameController().deselect();
    }

    public void changePosition(boolean isChangeToAttack) {
        getGameController().changePosition(isChangeToAttack);
        getGameController().deselect();
    }

    public void flipSummon() {
        getGameController().flipSummon();
        getGameController().deselect();
    }

    public void attack(int position) {
        if (checkBeforeAttack()) return;
        getGameController().attack(position);
        getGameController().deselect();
    }

    public void directAttack() {
        if (checkBeforeAttack()) return;
        getGameController().directAttack();
        getGameController().deselect();
    }

    private boolean checkBeforeAttack() {
        if (getSelectionController() == null) {
            GameController.getView().showError(GameView.ERROR_CARD_NOT_SELECTED);
            return true;
        }
        if (!getPlayer().getField().isInMonsterZone(getSelectionController().getCard())) {
            GameController.getView().showError(GameView.ERROR_CANT_ATTACK_WITH_CARD);
            return true;
        }
        if (!getGameController().getGameTurnController().getPhase().equals(Phase.BATTLE)) {
            GameController.getView().showError(GameView.ERROR_ACTION_NOT_POSSIBLE_IN_THIS_PHASE);
            return true;
        }
        return false;
    }

    public void nextPhase() {
        getGameController().nextPhase();
    }

    public void startRound() {
        getGameController().startRound();
    }

    public void activeEffect() {
        getGameController().activeEffect();
    }

    public void exchange(String sideDeckCardName, String mainDeckCardName) {
        Card sideDeckCard = DatabaseManager.getCards().getCardByName(sideDeckCardName);
        Card mainDeckCard = DatabaseManager.getCards().getCardByName(mainDeckCardName);
        if (sideDeckCard == null || mainDeckCard == null) {
            GameController.getView().showError(GameView.ERROR_CARD_NAME_INVALID, sideDeckCard == null ?
                    sideDeckCardName : mainDeckCardName);
            return;
        }
        if (!player.getDeck().getSideDeck().contains(sideDeckCard)) {
            GameController.getView().showError(GameView.ERROR_CARD_NOT_IN_DECK, sideDeckCard.getName(), "side");
            return;
        }
        if (!player.getDeck().getMainDeck().contains(mainDeckCard)) {
            GameController.getView().showError(GameView.ERROR_CARD_NOT_IN_DECK, mainDeckCard.getName(), "main");
            return;
        }
        getGameController().exchange(sideDeckCard, mainDeckCard);
    }

    //SpecialCases
    //TributeMonsterForSummon
    public GameCard[] tributeMonster(int amount) {
        availableCards = Arrays.stream(player.getField().getMonsterZone())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return null;
    }

    //Scanner
    public GameCard scanMonsterForScanner() {
        availableCards = new ArrayList<>(getRivalPlayer().getField().getGraveyard());
        return null;
    }

    //Man-Eater Bug
    public GameCard directRemove() {
        availableCards = Arrays.stream(getRivalPlayer().getField().getMonsterZone())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return null;
    }

    //TexChanger
    public GameCard specialCyberseSummon() {
        availableCards = new ArrayList<>(player.getField().getGraveyard());
        availableCards.addAll(player.getField().getHand());
        availableCards.addAll(player.getField().getDeck());
        availableCards = availableCards.stream()
                .filter(e -> e.getCard().getCardType().equals(CardType.MONSTER))
                .filter(e -> ((Monster) e.getCard()).getType().equals(MonsterType.CYBERSE))
                .filter(e -> ((Monster) e.getCard()).getEffect().equals(MonsterEffect.NORMAL))
                .collect(Collectors.toList());
        return null;
    }

    //HeraldOfCreation
    public GameCard summonFromGraveyard() {
        availableCards = player.getField().getGraveyard().stream()
                .filter(e -> e.getCard().getCardType().equals(CardType.MONSTER))
                .filter(e -> ((Monster) e.getCard()).getLevel() >= 7)
                .collect(Collectors.toList());
        return null;
    }

    //Beast King Barbaros & Tricky
    public abstract int chooseHowToSummon(List<String> choices);

    //terratiger
    public GameCard selectMonsterToSummon() {
        availableCards = player.getField().getHand().stream()
                .filter(e -> e.getCard().getCardType().equals(CardType.MONSTER))
                .filter(e -> ((Monster) e.getCard()).getLevel() <= 4)
                .collect(Collectors.toList());
        return null;
    }

    //EquipMonster
    public GameCard equipMonster() {
        availableCards = Arrays.stream(player.getField().getMonsterZone())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return null;
    }

    //Select card from graveyard
    public GameCard selectCardFromGraveyard() {
        availableCards = new ArrayList<>(player.getField().getGraveyard());
        return null;
    }

    //Select card from monster zone
    public GameCard selectCardFromMonsterZone() {
        availableCards = Arrays.stream(player.getField().getMonsterZone())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return null;
    }

    //Select card from both graveyards
    public GameCard selectMonsterFromAllGraveyards() {
        availableCards = new ArrayList<>(player.getField().getGraveyard().stream()
                .filter(e -> e.getCard().getCardType().equals(CardType.MONSTER))
                .collect(Collectors.toList()));
        availableCards.addAll(getRivalPlayer().getField().getGraveyard().stream()
                .filter(e -> e.getCard().getCardType().equals(CardType.MONSTER))
                .collect(Collectors.toList()));
        return null;
    }

    //Select card from hand
    public GameCard selectCardFromHand(GameCard exceptCard) {
        availableCards = new ArrayList<>(player.getField().getHand());
        if (exceptCard != null) availableCards.remove(exceptCard);
        return null;
    }

    //Select card from deck
    public GameCard selectCardFromDeck() {
        availableCards = new ArrayList<>(player.getField().getDeck());
        return null;
    }

    //Select field spell from deck
    public GameCard selectFieldSpellFromDeck() {
        availableCards = player.getField().getDeck().stream()
                .filter(e -> e.getCard().getCardType().equals(CardType.SPELL))
                .filter(e -> ((Spell) e.getCard()).getProperty().equals(SpellProperty.FIELD))
                .collect(Collectors.toList());
        return null;
    }

    //Select one of rival monsters
    public GameCard selectRivalMonster() {
        availableCards = Arrays.stream(getRivalPlayer().getField().getMonsterZone())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return null;
    }

    //Select at most two spell or trap form field
    public GameCard[] selectSpellTrapsFromField(int amount) {
        availableCards = Arrays.stream(player.getField().getSpellZone())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        availableCards.add(player.getField().getFieldZone());
        availableCards = Arrays.stream(getRivalPlayer().getField().getSpellZone())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        availableCards.add(getRivalPlayer().getField().getFieldZone());
        return null;
    }

    //Select card from graveyard with level less than mostLevel
    public GameCard selectCardFromGraveyard(int mostLevel) {
        availableCards = player.getField().getGraveyard().stream()
                .filter(e -> e.getCard().getCardType().equals(CardType.MONSTER))
                .filter(e -> ((Monster) e.getCard()).getLevel() >= mostLevel)
                .collect(Collectors.toList());
        return null;
    }

    //Select normal card(without effect) from hand with level less than mostLevel
    public GameCard selectNormalCardFromHand(int mostLevel) {
        availableCards = player.getField().getHand().stream()
                .filter(e -> e.getCard().getCardType().equals(CardType.MONSTER))
                .filter(e -> ((Monster) e.getCard()).getLevel() <= mostLevel)
                .filter(e -> ((Monster) e.getCard()).getEffect().equals(MonsterEffect.NORMAL))
                .collect(Collectors.toList());
        return null;
    }

    public GameCard selectRitualMonsterFromHand() {
        return null;
    }

    public List<GameCard> selectCardsForRitualTribute(int level) {
        return null;
    }

    public abstract Card getACard();

    public GameCard selectRandomCardFromHand() {
        availableCards = player.getField().getHand();
        return null;
    }

    public abstract boolean confirm(String message);

    protected SelectionController getSelectionController() {
        return getGameController().getSelectionController();
    }

    protected Phase getPhase() {
        return getGameController().getGameTurnController().getPhase();
    }

    public Player getRivalPlayer() {
        return player.getUser().getUsername().equals(getGameController().getCurrentPlayer().getUser().getUsername()) ?
                getGameController().getRivalPlayer() : getGameController().getCurrentPlayer();
    }

    protected GameController getGameController() {
        return GameController.getGameControllerById(gameControllerID);
    }

    public boolean isAI() {
        return this instanceof AIPlayerController;
    }
}
