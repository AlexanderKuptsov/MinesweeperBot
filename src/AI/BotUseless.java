package AI;

import Graphics.GUI;
import Main.Cell;
import Main.Clock;
import Main.Game;

import java.util.List;
import java.util.Random;

import static Graphics.GUI.*;

public class BotUseless {

    private boolean working, firstStep;
    private float timeBetweenMoves;
    private boolean stop, noWay, solved;
    private Cell[][] cells;

    private static final int BUTTON_OPEN = 0;
    private static final int BUTTON_SET_FLAG = 1;
    private static final float WAIT_TIME = 0.4f;

    public BotUseless(Cell[][] cells) {
        this.working = true;
        this.firstStep = true;
        this.timeBetweenMoves = WAIT_TIME;
        this.stop = false;
        this.noWay = true;
        this.solved = false;
        this.cells = cells;
    }

    private void ezOpen(int x, int y) {
        List<Cell> localNeighbours = GUI.getNeighbours(x, y);
        int fantomMines = 0;
        int flags = 0;
        boolean check = true;
        for (Cell cell : localNeighbours)
            if (!cell.isMarked()) fantomMines++;
            else {
                check = false;
                flags++;
            }
        int localState = cells[x / CELL_SIZE][y / CELL_SIZE].getState();

        // кол-во неоткрытых клеток равно кол-ву мин || кол-во неоткрытых клеток и кол-во флагов равно кол-ву мин
        if (localState == fantomMines && check || localState == fantomMines + flags) {
            for (Cell cell : localNeighbours)
                if (!cell.isMarked()) {
                    GUI.receiveClick(cell.getX(), cell.getY(), BUTTON_SET_FLAG);
                    //cell.setUseless(true);
                    timeBetweenMoves = WAIT_TIME;
                    noWay = false;
                    // stop = true;
                }

        }

        // все мины отмечены флагами, значит нужно открыть оставшиеся клетки
        if (localState == flags) {
            for (Cell cell : localNeighbours)
                if (!cell.isMarked()) {
                    GUI.receiveClick(cell.getX(), cell.getY(), BUTTON_OPEN);
                    //cell.setUseless(true);
                    timeBetweenMoves = WAIT_TIME;
                    noWay = false;
                    //stop = true;
                }
        }
    }

    private void firstLuckShot() {
        System.out.println("Bot: First Luck Shot");
        Random rnd = new Random();
        int x = rnd.nextInt(2);
        int y = rnd.nextInt(2);
        if (x == 1) x = CELLS_COUNT_X - 1;          ////////////
        if (y == 1) y = CELLS_COUNT_Y - 1;
        System.out.println("x & y:  " + x + " & " + y);
        GUI.receiveClick(x * CELL_SIZE, y * CELL_SIZE, BUTTON_OPEN);
        timeBetweenMoves = WAIT_TIME;
    }

    private void luckShot() {
        System.out.println("Bot: Luck Shot");
        Random rnd = new Random();
        int x = rnd.nextInt(SCREEN_WIDTH);
        int y = rnd.nextInt(SCREEN_HEIGHT);
        Cell localCell = GUI.getCells()[x / CELL_SIZE][y / CELL_SIZE];
        if (!localCell.isMarked() && localCell.isHidden()) {
            GUI.receiveClick(x, y, BUTTON_OPEN);
            timeBetweenMoves = WAIT_TIME;
        }
    }

    public void update() {
        if (working) {
            if (firstStep) {
                firstLuckShot();
                firstStep = false;
            }

            if (timeBetweenMoves >= 0) {
                timeBetweenMoves -= Clock.INSTANCE.getDelta();
            } else {
                stop = false;
                noWay = true;
                solved = true;
                for (Cell[] line : cells) {
                    for (Cell cell : line) {
                        if (cell.getState() == 0) cell.setUseless(true);
                        if (!cell.isUseless() && !cell.isHidden() && !cell.isMarked())
                            ezOpen(cell.getX(), cell.getY());
                       /* if (stop) {
                            System.out.println("Break");
                            // timeBetweenMoves = WAIT_TIME;
                            break;
                        }*/
                        if (cell.isHidden() && !cell.isMarked()) {
                            solved = false;
                        }
                    }
                    //if (stop) break;
                }
                if (noWay) {
                    if (!solved) luckShot();
                    else GUI.gameover();
                }
            }
        }
    }

    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }
}
