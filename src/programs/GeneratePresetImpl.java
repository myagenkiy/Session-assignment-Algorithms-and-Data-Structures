package programs;

import com.battle.heroes.army.programs.GeneratePreset;
import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;

import java.util.*;

public class GeneratePresetImpl implements GeneratePreset {

    private static final int MAX_UNITS_PER_TYPE = 11;

    public GeneratePresetImpl() {
    }

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        Army army = new Army();

        if (unitList == null || unitList.isEmpty()) {
            return army;
        }

        // Сортировка типов юнитов по эффективности
        unitList.sort((u1, u2) -> {
            double ratio1 = (double) u1.getBaseAttack() / u1.getCost();
            double ratio2 = (double) u2.getBaseAttack() / u2.getCost();

            if (Double.compare(ratio2, ratio1) != 0) {
                return Double.compare(ratio2, ratio1);
            }

            double healthRatio1 = (double) u1.getHealth() / u1.getCost();
            double healthRatio2 = (double) u2.getHealth() / u2.getCost();
            return Double.compare(healthRatio2, healthRatio1);
        });

        List<Unit> result = new ArrayList<>();
        Map<String, Integer> typeCounts = new HashMap<>();
        Random random = new Random();
        int totalPoints = 0;

        // Проход по отсортированным типам
        for (Unit template : unitList) {
            String type = template.getUnitType();
            int count = typeCounts.getOrDefault(type, 0);

            // Максимальное количество этого типа, для добавления
            int maxPossible = Math.min(
                    MAX_UNITS_PER_TYPE - count,
                    (maxPoints - totalPoints) / template.getCost()
            );

            for (int i = 0; i < maxPossible; i++) {
                // Свободные координаты
                int[] coordinates = findFreeCoordinates(result, random);
                if (coordinates == null) {
                    break;
                }

                int x = coordinates[0];
                int y = coordinates[1];

                // Создание нового юнита
                count++;
                Unit newUnit = new Unit(
                        type + " " + count,
                        type,
                        template.getHealth(),
                        template.getBaseAttack(),
                        template.getCost(),
                        template.getAttackType(),
                        template.getAttackBonuses() != null ?
                                new HashMap<>(template.getAttackBonuses()) : new HashMap<>(),
                        template.getDefenceBonuses() != null ?
                                new HashMap<>(template.getDefenceBonuses()) : new HashMap<>(),
                        x,
                        y
                );

                newUnit.setAlive(true);
                result.add(newUnit);
                totalPoints += template.getCost();
                typeCounts.put(type, count);

                if (totalPoints >= maxPoints) {
                    break;
                }
            }

            if (totalPoints >= maxPoints) {
                break;
            }
        }

        System.out.println("Used points: " + totalPoints);
        army.setUnits(result);
        army.setPoints(totalPoints);

        return army;
    }

    // Поиск свободных координат
    private int[] findFreeCoordinates(List<Unit> existingUnits, Random random) {
        for (int attempt = 0; attempt < 100; attempt++) {
            int x = random.nextInt(3);
            int y = random.nextInt(21);

            if (!isOccupied(existingUnits, x, y)) {
                return new int[]{x, y};
            }
        }

        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 21; y++) {
                if (!isOccupied(existingUnits, x, y)) {
                    return new int[]{x, y};
                }
            }
        }

        return null;
    }

    // Проверка, занята ли клетка
    private boolean isOccupied(List<Unit> units, int x, int y) {
        for (Unit unit : units) {
            if (unit.getxCoordinate() == x && unit.getyCoordinate() == y) {
                return true;
            }
        }
        return false;
    }
}