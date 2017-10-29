package AI;

import Graphics.GUI;
import Main.Cell;
import Main.Clock;

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
    private static final float WAIT_TIME = 0.075f;

    public BotUseless(Cell[][] cells) {
        this.working = true;
        this.firstStep = true;
        this.timeBetweenMoves = WAIT_TIME;
        this.stop = false;
        this.noWay = true;
        this.solved = false;
        this.cells = cells;
    }

    private void ezOpen(Cell mainCell) {
        List<Cell> localNeighbours = mainCell.getNotMarkedNeighbours();

        int localState = mainCell.getCurrentState();

        // кол-во неоткрытых клеток равно кол-ву мин || кол-во неоткрытых клеток и кол-во флагов равно кол-ву мин
        if (localState == localNeighbours.size()) {
            for (Cell cell : localNeighbours) {
                GUI.receiveClick(cell.getX(), cell.getY(), BUTTON_SET_FLAG);
                System.out.println("Bot: ez flag");
                timeBetweenMoves = WAIT_TIME;
                noWay = false;
                stop = true;
            }

        }

        // все мины отмечены флагами, значит нужно открыть оставшиеся клетки
        if (localState == 0) {
            for (Cell cell : localNeighbours) {
                GUI.receiveClick(cell.getX(), cell.getY(), BUTTON_OPEN);
                System.out.println("Bot: ez open");
                timeBetweenMoves = WAIT_TIME;
                noWay = false;
                stop = true;
            }
        }
    }

    private void openSafeCells(Cell mainCell) {
        List<Cell> localOpenNeighbours = mainCell.getOpenNeighbours();
        List<Cell> localNeighbours = mainCell.getNeighbours();
        int mainCellState = mainCell.getCurrentState();

        for (Cell localCell : localOpenNeighbours) {
            List<Cell> neighbourList = localCell.getNeighbours();
            int deltaSize = Math.abs(neighbourList.size() - localNeighbours.size());
            int deltaState = Math.abs(localCell.getCurrentState() - mainCellState);

            if (neighbourList.containsAll(localNeighbours)) {
                if (deltaState == deltaSize) {
                    for (Cell cell : neighbourList)
                        if (!cell.isMarked() && !localNeighbours.contains(cell)) {
                            cell.receiveClick(BUTTON_SET_FLAG);
                            System.out.println("Bot: set safe flag");
                            noWay = false;
                        }
                }
            } else {
                if (localNeighbours.containsAll(neighbourList)) {
                    if (deltaState == deltaSize) {
                        for (Cell cell : localNeighbours)
                            if (!cell.isMarked() && !neighbourList.contains(cell)) {
                                cell.receiveClick(BUTTON_SET_FLAG);
                                System.out.println("Bot: set safe flag");
                                noWay = false;
                            }
                    }
                }
            }
        }
    }

    private void firstLuckShot() {
        System.out.println("Bot: First Luck Shot");
        Random rnd = new Random();
        int x = rnd.nextInt(2);
        int y = rnd.nextInt(2);
        if (x == 1) x = CELLS_COUNT_X - 1;                                    ////////////
        if (y == 1) y = CELLS_COUNT_Y - 1;
        GUI.receiveClick(x * CELL_SIZE, y * CELL_SIZE, BUTTON_OPEN);
        timeBetweenMoves = WAIT_TIME;
    }

    private void luckShot() {
        Random rnd = new Random();
        int x = rnd.nextInt(SCREEN_WIDTH);
        int y = rnd.nextInt(SCREEN_HEIGHT);
        Cell localCell = GUI.getCells()[x / CELL_SIZE][y / CELL_SIZE];
        if (!localCell.isMarked() && localCell.isHidden()) {
            System.out.println("Bot: Luck Shot");
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

                        if (!cell.isUseless() && !cell.isHidden() && !cell.isMarked()) {
                            ezOpen(cell);
                            if (noWay) openSafeCells(cell);
                        }

                        if (stop) {
                            timeBetweenMoves = WAIT_TIME;
                            break;
                        }

                        if (cell.isHidden() && !cell.isMarked()) {
                            solved = false;
                        }
                    }
                    if (stop) break;
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
