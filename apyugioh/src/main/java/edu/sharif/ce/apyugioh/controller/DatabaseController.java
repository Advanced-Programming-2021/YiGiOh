package edu.sharif.ce.apyugioh.controller;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.sharif.ce.apyugioh.model.User;
import edu.sharif.ce.apyugioh.model.card.*;

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
    private static List<User> userList;
    private static ShopCards cards;

    public static void init() {
        moshi = new Moshi.Builder().build();
        dbs = new HashMap<>();
        ArrayList<String> models = new ArrayList<>();
        models.add("user");
        try {
            db = Path.of("db");
            for (String model : models) {
                dbs.put(model, Path.of("db\\" + model + "s.json"));
            }
            updateUsersFromDB();
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
            System.out.println(users);
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

    public static List<User> getUserList() {
        return userList;
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
            Card spell;
            if (spellMap.get("type").equalsIgnoreCase("spell")) {
                spell = new Spell();
                ((Spell) spell).setProperty(SpellProperty.valueOf(spellMap.get("icon (property)").toUpperCase()
                        .replaceAll("-", "_")));
                ((Spell) spell).setLimit(SpellLimit.valueOf(spellMap.get("status").toUpperCase()));
                spell.setName(spellMap.get("name"));
                spell.setDescription(spellMap.get("description"));
                cards.addSpell((Spell) spell, Integer.parseInt(spellMap.get("price")));
            } else {
                spell = new Trap();
                ((Trap) spell).setProperty(SpellProperty.valueOf(spellMap.get("icon (property)").toUpperCase()
                        .replaceAll("-", "_")));
                ((Trap) spell).setLimit(SpellLimit.valueOf(spellMap.get("status").toUpperCase()));
                spell.setName(spellMap.get("name"));
                spell.setDescription(spellMap.get("description"));
                cards.addTrap((Trap) spell, Integer.parseInt(spellMap.get("price")));
            }
        }
    }

    private static void addMonstersToCards(ShopCards cards) {
        for (HashMap<String, String> monsterMap : new CSVParser("shop/Monsters.csv").getContentsAsMap()) {
            Monster monster = new Monster();
            monster.setName(monsterMap.get("name"));
            monster.setDescription(monsterMap.get("description"));
            monster.setLevel(Integer.parseInt(monsterMap.get("level")));
            monster.setAttackPoints(Integer.parseInt(monsterMap.get("atk")));
            monster.setDefensePoints(Integer.parseInt(monsterMap.get("def")));
            monster.setAttribute(MonsterAttribute.valueOf(monsterMap.get("attribute")));
            monster.setType(MonsterType.valueOf(monsterMap.get("monster type")
                    .replaceAll("[- ]", "_").toUpperCase()));
            monster.setEffect(monsterMap.get("card type").equalsIgnoreCase("NORMAL") ? MonsterEffect.NORMAL :
                    MonsterEffect.CONTINUOUS);
            cards.addMonster(monster, Integer.parseInt(monsterMap.get("price")));
        }
    }
}
