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
    public BattleEngine setRandomSeed(long seed) {
        this.random = new Random(seed);
        return this;
    }
    public void reset() {
        this.random = new Random(DEFAULT_SEED);
    }
    public EncounterResult runEncounter(List<Combatant> teamA, List<Combatant> teamB) {
        Objects.requireNonNull(teamA, "teamA must not be null");
        Objects.requireNonNull(teamB, "teamB must not be null");

        List<Combatant> a = copyAndValidateTeam(teamA, "teamA");
        List<Combatant> b = copyAndValidateTeam(teamB, "teamB");

        removeDeadInPlace(a);
        removeDeadInPlace(b);

        EncounterResult result = new EncounterResult();

        result.addLog("=== Encounter Start ===");
        result.addLog("Team A size: " + (teamA == null ? "null" : teamA.size()));
        result.addLog("Team B size: " + (teamB == null ? "null" : teamB.size()));
        result.setRounds(0);
        result.setWinner("TBD");
        result.addLog("=== Encounter End ===");
        return result;
    }
    private static List<Combatant> copyAndValidateTeam(List<Combatant> team, String paramName) {
        List<Combatant> copy = new ArrayList<>(team.size());
        for (int i = 0; i < team.size(); i++) {
            Combatant c = team.get(i);
            if (c == null) {
                throw new IllegalArgumentException(paramName + " contains null at index " + i);
            }
            copy.add(c);
        }
        return copy;
    }

    private static void removeDeadInPlace(List<Combatant> team) {
        team.removeIf(c -> c == null || !c.isAlive());
    }
}