package edu.sharif.ce.apyugioh.controller;

import jdk.jshell.execution.Util;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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