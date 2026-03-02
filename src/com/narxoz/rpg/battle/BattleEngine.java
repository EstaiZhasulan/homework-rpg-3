package com.narxoz.rpg.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class BattleEngine {
    private static BattleEngine instance;
    private BattleEngine() {}
    private static final long DEFAULT_SEED = 1L;
    private Random random = new Random(DEFAULT_SEED);
    public static BattleEngine getInstance() {
        if (instance == null) {
            instance = new BattleEngine();
        }
        return instance;
    }
}