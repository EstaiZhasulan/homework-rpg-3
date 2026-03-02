package com.narxoz.rpg.battle;

public final class BattleEngine {

    private static BattleEngine instance;

    private BattleEngine() {
    }

    public static BattleEngine getInstance() {
        if (instance == null) {
            instance = new BattleEngine();
        }
        return instance;
    }
}
