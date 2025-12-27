package programs;

import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;
import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;

import java.util.*;

public class SimulateBattleImpl implements SimulateBattle {

    private PrintBattleLog printBattleLog;

    public SimulateBattleImpl() {
    }

    public void setPrintBattleLog(PrintBattleLog printBattleLog) {
        this.printBattleLog = printBattleLog;
    }

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        int round = 0;

        // Получение живых юнитов
        List<Unit> playerUnits = playerArmy.getUnits().stream()
                .filter(Unit::isAlive).toList();
        List<Unit> computerUnits = computerArmy.getUnits().stream()
                .filter(Unit::isAlive).toList();

        // Проверка, что есть с кем сражаться
        if (playerUnits.isEmpty() || computerUnits.isEmpty()) {
            System.out.println("Battle cannot start: one of armies is empty!");
            return;
        }

        Set<Unit> movedUnits = new HashSet<>();

        while (true) {
            boolean roundCompleted = true;

            // Сортировка по убыванию атаки
            PriorityQueue<Unit> playerQueue = new PriorityQueue<>(
                    Comparator.comparingInt(Unit::getBaseAttack).reversed());
            PriorityQueue<Unit> computerQueue = new PriorityQueue<>(
                    Comparator.comparingInt(Unit::getBaseAttack).reversed());

            // Обновление списка живых юнитов
            playerUnits = playerArmy.getUnits().stream()
                    .filter(Unit::isAlive).toList();
            computerUnits = computerArmy.getUnits().stream()
                    .filter(Unit::isAlive).toList();

            // В очередь только тех, кто еще не ходил в раунде
            playerQueue.addAll(playerUnits.stream()
                    .filter(u -> !movedUnits.contains(u)).toList());
            computerQueue.addAll(computerUnits.stream()
                    .filter(u -> !movedUnits.contains(u)).toList());

            // Ходы
            while (!playerQueue.isEmpty() || !computerQueue.isEmpty()) {
                // Ход юнита игрока
                if (!playerQueue.isEmpty()) {
                    Unit playerUnit = playerQueue.poll();
                    Unit target = unitAttack(playerUnit);

                    // Если цель убита и еще не ходила в этом раунде
                    if (target != null && !target.isAlive() && !movedUnits.contains(target)) {
                        roundCompleted = false;
                        break;
                    }

                    movedUnits.add(playerUnit);
                }

                // Ход юнита компьютера
                if (!computerQueue.isEmpty()) {
                    Unit computerUnit = computerQueue.poll();
                    Unit target = unitAttack(computerUnit);

                    // Если цель убита и еще не ходила в этом раунде
                    if (target != null && !target.isAlive() && !movedUnits.contains(target)) {
                        roundCompleted = false;
                        break;
                    }

                    movedUnits.add(computerUnit);
                }
            }

            // Если раунд успешно завершен
            if (roundCompleted) {
                round++;
                movedUnits.clear();

                System.out.println();
                System.out.println("Round " + round + " is over!");
                System.out.println("Player army has " + playerUnits.size() + " units");
                System.out.println("Computer army has " + computerUnits.size() + " units");
                System.out.println();
            }

            // Проверка условия окончания боя
            playerUnits = playerArmy.getUnits().stream()
                    .filter(Unit::isAlive).toList();
            computerUnits = computerArmy.getUnits().stream()
                    .filter(Unit::isAlive).toList();

            if (playerUnits.isEmpty() || computerUnits.isEmpty()) {
                System.out.println("Battle is over!");
                return;
            }
        }
    }

    // Выполняет атаку юнитом и возвращает цель
    private Unit unitAttack(Unit attacker) throws InterruptedException {
        if (attacker == null || !attacker.isAlive()) {
            return null;
        }

        Unit target = attacker.getProgram().attack();
        if (target != null) {
            printBattleLog.printBattleLog(attacker, target);
        }

        return target;
    }
}