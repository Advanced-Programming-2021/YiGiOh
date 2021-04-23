package edu.sharif.ce.apyugioh.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UtilsTest {

    @Test
    void firstUpperOnly() {
        assertEquals("Card Factory", Utils.firstUpperOnly("CARD_FACTORY"));
    }

    @Test
    void hash() {
        assertEquals("OH5Xvire7ZGXQLs2mhANCxJkhcgCLz14u1vJ055UmbY=", Utils.hash("mioo"));
    }
}