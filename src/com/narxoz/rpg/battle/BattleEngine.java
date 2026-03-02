package com.narxoz.rpg.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public final class BattleEngine {
    private static final long DEFAULT_SEED = 1L;

    private static BattleEngine instance;

    private Random random = new Random(DEFAULT_SEED);

    private BattleEngine() {
    }


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
        result.addLog("Team A size: " + a.size());
        result.addLog("Team B size: " + b.size());

        if (a.isEmpty() && b.isEmpty()) {
            result.setWinner("Draw");
            result.setRounds(0);
            result.addLog("No combatants on either side.");
            result.addLog("=== Encounter End ===");
            return result;
        }
        if (a.isEmpty()) {
            result.setWinner("Team B");
            result.setRounds(0);
            result.addLog("Team A has no living combatants.");
            result.addLog("=== Encounter End ===");
            return result;
        }
        if (b.isEmpty()) {
            result.setWinner("Team A");
            result.setRounds(0);
            result.addLog("Team B has no living combatants.");
            result.addLog("=== Encounter End ===");
            return result;
        }

        int rounds = 0;
        final int maxRoundsSafety = 10_000;

        while (!a.isEmpty() && !b.isEmpty() && rounds < maxRoundsSafety) {
            rounds++;
            result.addLog("");
            result.addLog("--- Round " + rounds + " ---");

            result.addLog("Team A attacks:");
            performTurn(a, b, result);

            if (b.isEmpty()) {
                break;
            }

            result.addLog("Team B attacks:");
            performTurn(b, a, result);

            removeDeadInPlace(a);
            removeDeadInPlace(b);
        }

        result.setRounds(rounds);

        String winner;
        if (a.isEmpty() && b.isEmpty()) {
            winner = "Draw";
        } else if (b.isEmpty()) {
            winner = "Team A";
        } else if (a.isEmpty()) {
            winner = "Team B";
        } else {
            winner = "Draw (max rounds reached)";
            result.addLog("Safety stop: max rounds reached (" + maxRoundsSafety + ").");
        }
        result.setWinner(winner);

        result.addLog("");
        result.addLog("=== Encounter End ===");
        result.addLog("Winner: " + winner);
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


    private void performTurn(List<Combatant> attackers, List<Combatant> defenders, EncounterResult result) {
        // Snapshot to keep stable order even if attackers list is cleaned later.
        List<Combatant> attackerSnapshot = new ArrayList<>(attackers);

        for (Combatant attacker : attackerSnapshot) {
            if (defenders.isEmpty()) {
                return;
            }
            if (attacker == null || !attacker.isAlive()) {
                continue;
            }

            Combatant target = pickTarget(defenders);
            int rawDamage = attacker.getAttackPower();
            int damage = Math.max(0, rawDamage);

            result.addLog(attacker.getName() + " -> " + target.getName() + " (" + damage + " dmg)");
            target.takeDamage(damage);

            if (!target.isAlive()) {
                result.addLog("  " + target.getName() + " is defeated!");
                defenders.remove(target);
            }
        }

        removeDeadInPlace(defenders);
    }

    private Combatant pickTarget(List<Combatant> defenders) {
        if (defenders.size() == 1) {
            return defenders.get(0);
        }
        int idx = random.nextInt(defenders.size());
        return defenders.get(idx);
    }
}
