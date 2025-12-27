package programs;

import com.battle.heroes.army.programs.UnitTargetPathFinder;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.EdgeDistance;
import com.battle.heroes.army.Unit;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {

    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;
    private static final int[][] DIRECTIONS = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},
            {-1, -1}, {1, 1}, {-1, 1}, {1, -1}
    };
    private static final int INFINITY = Integer.MAX_VALUE;

    public UnitTargetPathFinderImpl() {
    }

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        // Массивы для алгоритма Дейкстры
        int[][] distances = new int[WIDTH][HEIGHT];
        boolean[][] visited = new boolean[WIDTH][HEIGHT];
        Edge[][] parents = new Edge[WIDTH][HEIGHT];

        // Расстояния как бесконечность
        for (int i = 0; i < WIDTH; i++) {
            Arrays.fill(distances[i], INFINITY);
        }

        // Приоритетная очередь для алгоритма Дейкстры
        PriorityQueue<EdgeDistance> queue = new PriorityQueue<>(
                Comparator.comparingInt(EdgeDistance::getDistance));

        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        int targetX = targetUnit.getxCoordinate();
        int targetY = targetUnit.getyCoordinate();

        // Устанавливка расстояния до стартовой точки как 0
        distances[startX][startY] = 0;
        queue.add(new EdgeDistance(startX, startY, 0));

        // Создание множества занятых клеток
        Set<String> occupiedCells = new HashSet<>();
        for (Unit unit : existingUnitList) {
            if (unit != attackUnit && unit != targetUnit && unit.isAlive()) {
                occupiedCells.add(unit.getxCoordinate() + "," + unit.getyCoordinate());
            }
        }

        // Алгоритм Дейкстры
        while (!queue.isEmpty()) {
            EdgeDistance current = queue.poll();
            int x = current.getX();
            int y = current.getY();

            if (visited[x][y]) {
                continue;
            }

            visited[x][y] = true;

            // Если достиг цели
            if (x == targetX && y == targetY) {
                break;
            }

            // Проверка всех соседей
            for (int[] dir : DIRECTIONS) {
                int newX = x + dir[0];
                int newY = y + dir[1];

                // Проверка валидности клетки и доступности
                if (!isValid(newX, newY, occupiedCells, targetUnit)) {
                    continue;
                }

                if (!visited[newX][newY]) {
                    int newDistance = distances[x][y] + 1;

                    if (newDistance < distances[newX][newY]) {
                        distances[newX][newY] = newDistance;
                        parents[newX][newY] = new Edge(x, y);
                        queue.add(new EdgeDistance(newX, newY, newDistance));
                    }
                }
            }
        }

        // Если путь не найден
        if (parents[targetX][targetY] == null) {
            System.out.println("Unit " + attackUnit.getName() +
                    " cannot find path to attack unit " + targetUnit.getName());
            return new ArrayList<>();
        }

        // Восстанавление пути от цели к старту
        List<Edge> path = new ArrayList<>();
        int x = targetX;
        int y = targetY;

        while (x != startX || y != startY) {
            path.add(new Edge(x, y));
            Edge parent = parents[x][y];
            x = parent.getX();
            y = parent.getY();
        }

        // Добавление стартовой точки и переворачивание пути
        path.add(new Edge(startX, startY));
        Collections.reverse(path);

        return path;
    }

    // Проверка, является ли клетка доступной для перемещения
    private boolean isValid(int x, int y, Set<String> occupiedCells, Unit targetUnit) {
        // Проверка границы поля
        if (x < 0 || x >= WIDTH || y < 0 || y >= HEIGHT) {
            return false;
        }

        // Проверка, не занята ли клетка (кроме клетки цели)
        String cellKey = x + "," + y;
        if (occupiedCells.contains(cellKey)) {
            // Если это клетка цели - true
            return x == targetUnit.getxCoordinate() && y == targetUnit.getyCoordinate();
        }

        return true;
    }
}