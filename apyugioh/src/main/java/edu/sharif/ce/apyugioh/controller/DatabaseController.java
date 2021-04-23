package edu.sharif.ce.apyugioh.controller;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.sharif.ce.apyugioh.model.Inventory;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.model.card.*;
import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseController {

    private static Path db;
    private static HashMap<String, Path> dbs;
    private static Moshi moshi;
    @Getter
    private static List<User> userList;
    @Getter
    private static List<Inventory> inventoryList;
    private static ShopCards cards;

    public static void init() {
        moshi = new Moshi.Builder().build();
        dbs = new HashMap<>();
        ArrayList<String> models = new ArrayList<>();
        models.add("user");
        models.add("inventory");
        try {
            db = Path.of("db");
            for (String model : models) {
                if (model.equals("inventory"))
                    dbs.put(model, Path.of("db", model.substring(0, model.length() - 1) + "ies.json"));
                else dbs.put(model, Path.of("db", model + "s.json"));
            }
            updateUsersFromDB();
            updateInventoriesFromDB();
            cards = CSVToShopCards();
        } catch (Exception e) {
            Utils.printError("couldn't initialize database");
            System.exit(1);
        }
    }

    private static String readFromFile(Path jsonDB) {
        try {
            initDB(jsonDB);
            return Files.readString(jsonDB);
        } catch (Exception e) {
            Utils.printError("failed to read database");
            System.exit(1);
        }
        return null;
    }

    private static void writeToFile(Path jsonDB, String jsonText) {
        try {
            initDB(jsonDB);
            Files.writeString(jsonDB, jsonText);
        } catch (Exception e) {
            Utils.printError("failed to write database");
            System.exit(1);
        }
    }

    private static void initDB(Path jsonDB) throws IOException {
        if (!Files.isDirectory(db)) {
            Files.createDirectory(db);
        }
        if (!Files.exists(jsonDB)) {
            Files.createFile(jsonDB);
            Files.writeString(jsonDB, "[]");
        }
    }

    private static void updateUsersFromDB() {
        String users = "";
        try {
            users = readFromFile(dbs.get("user"));
            Type type = Types.newParameterizedType(List.class, User.class);
            JsonAdapter<List<User>> usersAdapter = moshi.adapter(type);
            userList = usersAdapter.fromJson(users);
        } catch (Exception e) {
            Utils.printError("corrupted database");
            System.exit(1);
        }
    }

    private static void updateInventoriesFromDB() {
        String inventories = "";
        try {
            inventories = readFromFile(dbs.get("inventory"));
            Type type = Types.newParameterizedType(List.class, Inventory.class);
            JsonAdapter<List<Inventory>> inventoryAdapter = moshi.adapter(type);
            inventoryList = inventoryAdapter.fromJson(inventories);
        } catch (Exception e) {
            Utils.printError("corrupted database");
            System.exit(1);
        }
    }

    public static void updateUsersToDB() {
        try {
            Type type = Types.newParameterizedType(List.class, User.class);
            JsonAdapter<List<User>> usersAdapter = moshi.adapter(type);
            writeToFile(dbs.get("user"), usersAdapter.toJson(userList));
        } catch (Exception e) {
            Utils.printError("corrupted database");
            System.exit(1);
        }
    }

    public static void updateInventoriesToDB() {
        try {
            Type type = Types.newParameterizedType(List.class, Inventory.class);
            JsonAdapter<List<Inventory>> inventoryAdapter = moshi.adapter(type);
            writeToFile(dbs.get("inventory"), inventoryAdapter.toJson(inventoryList));
        } catch (Exception e) {
            Utils.printError("corrupted database");
            System.exit(1);
        }
    }

    public static void addUser(User user) {
        userList.add(user);
        updateUsersToDB();
    }

    private static ShopCards CSVToShopCards() {
        ShopCards cards = new ShopCards();
        addMonstersToCards(cards);
        addSpellsToCards(cards);
        return cards;
    }

    private static void addSpellsToCards(ShopCards cards) {
        for (HashMap<String, String> spellMap : new CSVParser("shop/SpellTrap.csv").getContentsAsMap()) {
            if (spellMap.get("type").equalsIgnoreCase("spell")) {
                Spell spell = new Spell(spellMap.get("name"), spellMap.get("description"), SpellProperty.
                        valueOf(spellMap.get("icon (property)").toUpperCase().replaceAll("-", "_")),
                        SpellLimit.valueOf(spellMap.get("status").toUpperCase()));
                cards.addSpell(spell, Integer.parseInt(spellMap.get("price")));
            } else {
                Trap trap = new Trap(spellMap.get("name"), spellMap.get("description"), SpellProperty.
                        valueOf(spellMap.get("icon (property)").toUpperCase().replaceAll("-", "_")),
                        SpellLimit.valueOf(spellMap.get("status").toUpperCase()));
                cards.addTrap(trap, Integer.parseInt(spellMap.get("price")));
            }
        }
    }

    private static void addMonstersToCards(ShopCards cards) {
        for (HashMap<String, String> monsterMap : new CSVParser("shop/Monsters.csv").getContentsAsMap()) {
            Monster monster = new Monster(monsterMap.get("name"), monsterMap.get("description"),
                    Integer.parseInt(monsterMap.get("level")), Integer.parseInt(monsterMap.get("atk")),
                    Integer.parseInt(monsterMap.get("def")), MonsterAttribute.valueOf(monsterMap.get("attribute")),
                    MonsterType.valueOf(monsterMap.get("monster type").replaceAll("[- ]", "_")
                            .toUpperCase()), monsterMap.get("card type").equalsIgnoreCase("NORMAL") ?
                    MonsterEffect.NORMAL : MonsterEffect.CONTINUOUS);
            cards.addMonster(monster, Integer.parseInt(monsterMap.get("price")));
        }
    }
}
