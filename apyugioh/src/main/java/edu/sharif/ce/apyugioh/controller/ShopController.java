package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.model.User;
import lombok.Getter;
import lombok.Setter;

public class ShopController {

    @Getter
    private static ShopController instance;

    static {
        instance = new ShopController();
    }

    @Getter @Setter
    private User user;

}
