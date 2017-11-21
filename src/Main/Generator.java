package Main;

import java.util.Random;

import static Graphics.GUI.CELLS_COUNT_X;
import static Graphics.GUI.CELLS_COUNT_Y;
import static Graphics.GUI.CELL_SIZE;

public class Generator {
    private static final int DIFFICULTY = 15;

    public static Cell[][] generate() {
        {
            Random rnd = new Random();

            //Карта
            Cell[][] map = new Cell[CELLS_COUNT_X][CELLS_COUNT_Y];

            //Матрица с пометками, указывается кол-во мин рядом с каждой клеткой
            int[][] counts = new int[CELLS_COUNT_X][CELLS_COUNT_Y];

            for (int x = 0; x < CELLS_COUNT_X; x++) {
                for (int y = 0; y < CELLS_COUNT_Y; y++) {
                    boolean isMine = rnd.nextInt(100) < DIFFICULTY;
                    boolean isCorner = ((x == 0 || x == CELLS_COUNT_X - 1) && (y == 0 || y == CELLS_COUNT_Y - 1));

                    if (isMine && !isCorner) {
                        map[x][y] = new Cell(x * CELL_SIZE, y * CELL_SIZE, -1);

                        for (int i = -1; i < 2; i++) {
                            for (int j = -1; j < 2; j++) {
                                try {
                                    if (map[x + i][y + j] == null) {
                                        ///Если клетки там ещё нет, записываем сведение о мине в матрицу
                                        counts[x + i][y + j] += 1;
                                    } else {
                                        ///Если есть, говорим ей о появлении мины
                                        map[x + i][y + j].incNearMines();
                                    }
                                } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                                    //ignore
                                }
                            }
                        }
                    } else {
                        map[x][y] = new Cell(x * CELL_SIZE, y * CELL_SIZE, counts[x][y]);
                    }
                }
            }
            return map;
        }
    }
}
