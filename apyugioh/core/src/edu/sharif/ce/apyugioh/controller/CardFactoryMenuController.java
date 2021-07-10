package edu.sharif.ce.apyugioh.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import edu.sharif.ce.apyugioh.model.*;
import edu.sharif.ce.apyugioh.view.menu.CardFactoryMenuView;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.spookygames.gdx.nativefilechooser.NativeFileChooser;
import net.spookygames.gdx.nativefilechooser.NativeFileChooserCallback;
import net.spookygames.gdx.nativefilechooser.NativeFileChooserConfiguration;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;


public class CardFactoryMenuController {
    @Getter
    private static CardFactoryMenuController instance;
    private static Logger logger;

    static{
        instance = new CardFactoryMenuController();
        logger = LogManager.getLogger(CardFactoryMenuController.class);
    }

    @Getter
    @Setter
    private User user;
    private CardFactoryMenuView view;
    @Getter
    private ArrayList<String> monsterEffects;
    @Getter
    private ArrayList<String> spellEffects;
    @Getter
    @Setter
    private ArrayList<String> cardEffects;
    @Getter
    @Setter
    private Class cardKind;
    private FileHandle imageFileHandle;
    private Pixmap imagePixmap;

    private CardFactoryMenuController(){
    }

    public void showCardFactoryMenu(){
        if (view != null)
            view.dispose();
        loadEffects();
        view = new CardFactoryMenuView(ProgramController.getGame());
        cardEffects = new ArrayList<>();
        ProgramController.setState(MenuState.CARD_FACTORY);
        ProgramController.setCurrentMenu(view);
    }

    public void back(){
        view.dispose();
        view = null;
        MainMenuController.getInstance().showMainMenu();
    }

    public void importCard(){

    }

    public void exportCard(){

    }

    public void loadImage(FileHandle imageFile){
        Pixmap pixmap = new Pixmap(imageFile);
        Pixmap resizedPixmap = new Pixmap(350, 520, pixmap.getFormat());
        resizedPixmap.drawPixmap(pixmap,
                0, 0, pixmap.getWidth(), pixmap.getHeight(),
                0, 0, resizedPixmap.getWidth(), resizedPixmap.getHeight());
        pixmap = resizedPixmap;
        view.updateCardImageView(new Texture(pixmap));
    }

    public void createFileChooser(NativeFileChooser fileChooser) {
        NativeFileChooserConfiguration conf = new NativeFileChooserConfiguration();
        conf.directory = Gdx.files.absolute(System.getProperty("user.home"));
        conf.mimeFilter = "audio/*";
        conf.nameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("ogg");
            }
        };
        conf.title = "Choose card's front photo";
        fileChooser.chooseFile(conf, new NativeFileChooserCallback() {
            @Override
            public void onFileChosen(FileHandle file) {
                // Do stuff with file, yay!
                loadImage(file);
            }

            @Override
            public void onCancellation() {
                System.out.println("Cancelled!");
            }

            @Override
            public void onError(Exception exception) {
                System.out.println("Not an audio");
            }
        });
    }

    public void addCardToMainCards(String cardName){
        if (!checkForName(cardName))
            return;
    }

    private boolean checkForName(String name){
        String[] cardNames = DatabaseManager.getCards().getAllCardNames();
        for(int i = 0;i<cardNames.length;++i){
            if (name.equals(cardNames[i])){
                view.showErrorDialog("There is another card with this name!");
                return false;
            }
        }
        return true;
    }

    public void removeEffect(String effect){
        cardEffects.remove(effect);
        view.updateCardEffectsTable();
    }

    public void addEffect(String effect){
        cardEffects.add(effect);
        view.updateCardEffectsTable();
    }

    private void loadEffects(){
        loadMonsterEffects();
        loadSpellEffects();
    }

    private void loadMonsterEffects(){
        monsterEffects = new ArrayList<>();
        monsterEffects.add(Utils.firstUpperOnly(Effects.SET_ATTACK.toString()));
        monsterEffects.add(Utils.firstUpperOnly(Effects.COMBINE_LEVELS_OF.toString()));
        monsterEffects.add(Utils.firstUpperOnly(Effects.SELECT_FACE_UP_MONSTERS.toString()));
        monsterEffects.add(Utils.firstUpperOnly(Effects.SELECT_ALL_MONSTERS.toString()));
        monsterEffects.add(Utils.firstUpperOnly(Effects.ADD_ATTACK_TO_ALL_MONSTERS.toString()));
        monsterEffects.add(Utils.firstUpperOnly(Effects.CAN_NOT_BE_ATTACKED_WHEN_WE_HAVE_ANOTHER_MONSTER.toString()));
        monsterEffects.add(Utils.firstUpperOnly(Effects.DESTROY_ATTACKER_CARD_IF_DESTROYED.toString()));
        monsterEffects.add(Utils.firstUpperOnly(Effects.ZERO_ATTACK_POWER_FOR_ATTACKER_ON_THAT_TURN.toString()));
        monsterEffects.add(Utils.firstUpperOnly(Effects.CALLABLE_BY_RITUAL_SUMMON.toString()));
        monsterEffects.add(Utils.firstUpperOnly(Effects.CANT_BE_DESTROYED_IN_NORMAL_ATTACK.toString()));
        monsterEffects.add(Utils.firstUpperOnly(Effects.DESTROY_ONE_OF_RIVAL_MONSTERS_AFTER_FLIP.toString()));
        monsterEffects.add(Utils.firstUpperOnly(Effects.SCAN_A_DESTROYED_MONSTER.toString()));
        monsterEffects.add(Utils.firstUpperOnly(Effects.DECREASE_ATTACKER_LP_IF_FACE_DOWN.toString()));
        monsterEffects.add(Utils.firstUpperOnly(Effects.NEUTRAL_ONE_ATTACK_IN_EACH_TURN.toString()));
        monsterEffects.add(Utils.firstUpperOnly(Effects.SPECIAL_SUMMON_A_NORMAL_CYBERSE_MONSTER.toString()));
        monsterEffects.add(Utils.firstUpperOnly(Effects.RIVAL_CANT_ACTIVE_TRAP.toString()));
        monsterEffects.add(Utils.firstUpperOnly(Effects.LPS_DOESNT_CHANGE.toString()));
        monsterEffects.add(Utils.firstUpperOnly(Effects.DESTROY_ANOTHER_CARD_IN_BATTLE_IF_DESTROYED.toString()));
        monsterEffects.add(Utils.firstUpperOnly(Effects.SPECIAL_SUMMON_BY_REMOVE_CARD_FROM_HAND.toString()));

    }

    private void loadSpellEffects(){
        spellEffects = new ArrayList<>();
        spellEffects.add(Utils.firstUpperOnly(Effects.SPECIAL_SUMMON_FROM_GRAVEYARD.toString()));
        spellEffects.add(Utils.firstUpperOnly(Effects.ADD_FIELD_SPELL_TO_HAND.toString()));
        spellEffects.add(Utils.firstUpperOnly(Effects.DRAW_TWO_CARD.toString()));
        spellEffects.add(Utils.firstUpperOnly(Effects.DESTROY_ALL_RIVAL_MONSTERS.toString()));
        spellEffects.add(Utils.firstUpperOnly(Effects.CONTROL_ONE_RIVAL_MONSTER.toString()));
        spellEffects.add(Utils.firstUpperOnly(Effects.DESTROY_ALL_RIVAL_SPELL_TRAPS.toString()));
        spellEffects.add(Utils.firstUpperOnly(Effects.DESTROY_ALL_MONSTERS.toString()));
        spellEffects.add(Utils.firstUpperOnly(Effects.DRAW_CARD_IF_MONSTER_DESTROYED.toString()));
        spellEffects.add(Utils.firstUpperOnly(Effects.INCREASE_LP_IF_SPELL_ACTIVATED.toString()));
        spellEffects.add(Utils.firstUpperOnly(Effects.DESTROY_SPELL_OR_TRAP.toString()));
        spellEffects.add(Utils.firstUpperOnly(Effects.RITUAL_SUMMON.toString()));
    }

}
