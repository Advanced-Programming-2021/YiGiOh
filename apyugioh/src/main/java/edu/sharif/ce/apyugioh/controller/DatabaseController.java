package edu.sharif.ce.apyugioh.controller;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import edu.sharif.ce.apyugioh.model.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DatabaseController {

    private static Path db, usersDB;
    private static Moshi moshi;
    private static List<User> userList;

    public static void init() {
        moshi = new Moshi.Builder().build();
        try {
            db = Path.of("db");
            usersDB = Path.of("db\\users.json");
            updateUsersFromDB();
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
            users = readFromFile(usersDB);
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
            writeToFile(usersDB, usersAdapter.toJson(userList));
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

}
