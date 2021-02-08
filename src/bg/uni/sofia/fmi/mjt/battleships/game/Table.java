package bg.uni.sofia.fmi.mjt.battleships.game;

public class Table {
    private final Cells[][] cells;
    private int shipCellsCount;
    private Point lastAttack;

    public Table(Ship[] ships) {
        cells = new Cells[10][10];
        shipCellsCount = 0;
        lastAttack = null;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                cells[i][j] = Cells.EMPTY;
            }
        }
        for (Ship ship : ships) {
            Point p1 = ship.a();
            Point p2 = ship.b();
            if (p1.x() == p2.x()) {
                for (int j = Math.min(p1.y(), p2.y()) - 1; j < Math.max(p1.y(), p2.y()); j++) {
                    cells[j][p1.x() - 1] = Cells.SHIP;
                    shipCellsCount++;
                }
            }
            if (p1.y() == p2.y()) {
                for (int j = Math.min(p1.x(), p2.x()) - 1; j < Math.max(p1.x(), p2.x()); j++) {
                    cells[p1.y() - 1][j] = Cells.SHIP;
                    shipCellsCount++;
                }
            }
        }
    }

    private boolean isInTable(Point a) {
        return a.x() > 0 && a.x() <= 10 && a.y() > 0 && a.y() <= 10;
    }

    public void attack(Point a) {
        if (isInTable(a)) {
            Cells cell = cells[a.y() - 1][a.x() - 1];
            if (cell == Cells.EMPTY) {
                cell = Cells.HIT_EMPTY;
            }
            if (cell == Cells.SHIP) {
                cell = Cells.HIT_SHIP;
                shipCellsCount--;
            }
            cells[a.y() - 1][a.x() - 1] = cell;
        }
        lastAttack = a;
    }

    public String toString(boolean enemyBoard) {
        StringBuilder builder = new StringBuilder();
        if (enemyBoard) {
            builder.append("        ENEMY BOARD");
            builder.append(System.lineSeparator());
            builder.append("   ");
            for (int i = 0; i < 10; i++) {
                builder.append(i + 1).append(" ");
            }
            builder.append(System.lineSeparator());
            builder.append("   ");
            builder.append("_ ".repeat(10));
            builder.append(System.lineSeparator());
            char row = 'A';
            for (int i = 0; i < 10; i++) {
                builder.append(row).append(" |");
                row++;
                for (int j = 0; j < 10; j++) {
                    builder.append(cells[i][j].getEnemySymbol()).append("|");
                }
                builder.append(System.lineSeparator());
            }
        } else {
            builder.append("        YOUR BOARD");
            builder.append(System.lineSeparator());
            builder.append("   ");
            for (int i = 0; i < 10; i++) {
                builder.append(i + 1).append(" ");
            }
            builder.append(System.lineSeparator());
            builder.append("   ");
            builder.append("_ ".repeat(10));
            builder.append(System.lineSeparator());
            char row = 'A';
            for (int i = 0; i < 10; i++) {
                builder.append(row).append(" |");
                row++;
                for (int j = 0; j < 10; j++) {
                    builder.append(cells[i][j].getYourSymbol()).append("|");
                }
                builder.append(System.lineSeparator());
            }
        }
        return builder.toString();
    }

    public int getShipCellsRemaining() {
        return shipCellsCount;
    }
}
