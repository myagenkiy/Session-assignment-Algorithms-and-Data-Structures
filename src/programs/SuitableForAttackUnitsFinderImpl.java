package programs;

import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;
import com.battle.heroes.army.Unit;

import java.util.ArrayList;
import java.util.List;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    public SuitableForAttackUnitsFinderImpl() {
    }

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();

        if (unitsByRow == null || unitsByRow.isEmpty()) {
            System.out.println("Unit can not find target for attack!");
            return suitableUnits;
        }

        // Направление проверки блокировки
        int checkRowOffset = isLeftArmyTarget ? 2 : 0; // Для левой армии проверяем правый ряд (индекс 2)
        int direction = isLeftArmyTarget ? -1 : 1; // Направление движения при проверке

        // Проход по всем рядам
        for (int currentRow = 0; currentRow < unitsByRow.size(); currentRow++) {
            List<Unit> row = unitsByRow.get(currentRow);
            if (row == null || row.isEmpty()) {
                continue;
            }

            // Проверка каждого юнита в ряду
            for (Unit unit : row) {
                if (!unit.isAlive()) {
                    continue;
                }

                boolean isSuitable = true;

                // Если текущий ряд не равен checkRowOffset, проверка блокировки
                if (currentRow != checkRowOffset) {
                    int adjacentRow = currentRow + direction;

                    // Проверка, существует ли соседний ряд
                    if (adjacentRow >= 0 && adjacentRow < unitsByRow.size()) {
                        List<Unit> adjacentRowUnits = unitsByRow.get(adjacentRow);

                        // Проверка, есть ли в соседнем ряду живой юнит на той же Y-координате
                        if (adjacentRowUnits != null) {
                            for (Unit adjacentUnit : adjacentRowUnits) {
                                if (adjacentUnit.isAlive() &&
                                        adjacentUnit.getyCoordinate() == unit.getyCoordinate()) {
                                    isSuitable = false;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (isSuitable) {
                    suitableUnits.add(unit);
                }
            }
        }

        if (suitableUnits.isEmpty()) {
            System.out.println("Unit can not find target for attack!");
        }

        return suitableUnits;
    }
}