package AI;

import Main.Cell;

import java.util.*;

public class BotUseless {

    private boolean working, firstStep;
    private int cellsCountX, cellsCountY;
    private boolean noWay, solved;
    private Cell[][] cells;
    private Set<Cell> cellsToOpen;
    private Set<Cell> cellsToMark;

    public BotUseless(Cell[][] cells) {
        this.working = true;
        this.firstStep = true;
        this.cellsCountX = cells.length;
        this.cellsCountY = cells[0].length;
        this.noWay = true;
        this.solved = false;
        this.cells = cells;
        this.cellsToOpen = new HashSet<Cell>();
        this.cellsToMark = new HashSet<Cell>();
    }

    void ezOpen(Cell mainCell) {
        List<Cell> localNeighbours = getNotMarkedNeighbours(mainCell);
        if (localNeighbours.size() > 0) {
            int localState = getCurrentState(mainCell);

            // кол-во неоткрытых клеток равно кол-ву мин || кол-во неоткрытых клеток и кол-во флагов равно кол-ву мин
            if (localState == localNeighbours.size()) {
                System.out.println("Bot: ez flag");
                noWay = false;
                cellsToMark.addAll(localNeighbours);
            }

            // все мины отмечены флагами, значит нужно открыть оставшиеся клетки
            if (localState == 0) {
                System.out.println("Bot: ez open");
                noWay = false;
                cellsToOpen.addAll(localNeighbours);
            }
        }
    }

    void findSafeCells(Cell mainCell) {
        List<Cell> localOpenNeighbours = getOpenNeighbours(mainCell);
        List<Cell> localNeighbours = getNeighbours(mainCell);
        int mainCellState = getCurrentState(mainCell);

        for (Cell localCell : localOpenNeighbours) {
            List<Cell> neighbourList = getNeighbours(localCell);
            int deltaSize = Math.abs(neighbourList.size() - localNeighbours.size());
            int deltaState = Math.abs(getCurrentState(localCell) - mainCellState);

            if (deltaState == deltaSize || deltaState == 0) { // поиск клеток, где точно есть бомба/нет бомбы
                if (neighbourList.containsAll(localNeighbours)) {
                    for (Cell cell : neighbourList)
                        if (!cell.isMarked() && !localNeighbours.contains(cell)) {
                            System.out.println("Bot: make safe move ");
                            if (deltaState == 0) cellsToOpen.add(cell);
                            else if (!cell.isMarked()) cellsToMark.add(cell);
                            noWay = false;
                        }
                } else {
                    if (localNeighbours.containsAll(neighbourList)) {
                        for (Cell cell : localNeighbours)
                            if (!cell.isMarked() && !neighbourList.contains(cell)) {
                                System.out.println("Bot: make safe move ");
                                if (deltaState == 0) cellsToOpen.add(cell);
                                else cellsToMark.add(cell);
                                noWay = false;
                            }
                    }
                }
            }
        }
    }

    void phantomMove(Cell mainCell) {
        List<Cell> localOpenNeighbours = getOpenNeighbours(mainCell);
        if (localOpenNeighbours.size() > 1) {
            mainCell.setMarked(true);
            boolean check = true;
            for (Cell localCell : localOpenNeighbours) {
                if (getCurrentState(localCell) != 0) {
                    check = false;
                    break;
                } else {
                    List<Cell> localHiddenNeighbours = getNotMarkedNeighbours(mainCell);
                    for (Cell localHiddenCell : localHiddenNeighbours) {
                        List<Cell> newLocalOpenNeighbours = getOpenNeighbours(localHiddenCell);
                        for (Cell newLocalCell : newLocalOpenNeighbours) {
                            if (getCurrentState(newLocalCell) != 0 ||
                                    getNotMarkedNeighbours(newLocalCell) == localHiddenNeighbours) {
                                check = false;
                                break;
                            }
                        }
                    }
                }
            }
            mainCell.setMarked(false);
            if (check) {
                System.out.println("Bot: Best possible flag at [" +
                        mainCell.getXPosition() + "][" + mainCell.getYPosition() + "]");
                cellsToMark.add(mainCell);
                noWay = false;
            }
        }
    }

    void bestEverProbability() {
        float minAcceptableProbability = 0.275f;
        float minProbability = 1.0f;
        float maxProbability = 0f;
        Cell minProbabilityCell = null;
        Cell maxProbabilityCell = null;
        for (Cell[] line : cells) {
            for (Cell cell : line) {
                if (cell.isHidden() && !cell.isMarked()) {

                    float localProbability = 0;
                    List<Cell> neighbours = getOpenNeighbours(cell);
                    for (Cell localCell : neighbours) {
                        List<Cell> localList = getNotMarkedNeighbours(localCell);
                        float state = getCurrentState(localCell);
                        if (localList.size() > 1 && localList.size() > state && state != 0) {
                            localProbability += state / localList.size();
                        }
                    }
                    if (localProbability < minProbability && localProbability != 0) {
                        minProbability = localProbability;
                        minProbabilityCell = cell;
                    }
                    if (localProbability > maxProbability) {
                        maxProbability = localProbability;
                        maxProbabilityCell = cell;
                    }
                }
            }
        }
        if (maxProbabilityCell != null && maxProbability >= minAcceptableProbability) phantomMove(maxProbabilityCell);

        if (minProbabilityCell != null && minProbability <= minAcceptableProbability && noWay) {
            System.out.println("Bot: Best Probability (" + minProbability + ") Luck Shot [" +
                    minProbabilityCell.getXPosition() + "][" + minProbabilityCell.getYPosition() + "]");
            cellsToOpen.add(minProbabilityCell);
            noWay = false;
        }
    }

    void firstLuckShot() {
        if (checkCorner()) {
            Random rnd = new Random();
            int x = rnd.nextInt(2);
            int y = rnd.nextInt(2);
            if (x == 1) x = cellsCountX - 1;
            if (y == 1) y = cellsCountY - 1;
            Cell newCell = cells[x][y];
            if (newCell.isHidden() & !newCell.isMarked()) {
                System.out.println("Bot: Corner Luck Shot");
                cellsToOpen.add(newCell);
                if (newCell.getState() == 1 || newCell.getState() == 2) firstStep = true;
            } else firstLuckShot();
        }
    }

    private boolean checkCorner() {
        return cells[0][0].isHidden() ||
                cells[0][cellsCountY - 1].isHidden() ||
                cells[cellsCountX - 1][0].isHidden() ||
                cells[cellsCountX - 1][cellsCountY - 1].isHidden();
    }

    private void luckShot() {
        Random rnd = new Random();
        int x = rnd.nextInt(cellsCountX);
        int y = rnd.nextInt(cellsCountY);
        Cell localCell = cells[x][y];
        if (!localCell.isMarked() && localCell.isHidden()) {
            System.out.println("Bot: Luck Shot");
            cellsToOpen.add(localCell);
        }
    }

    public void update() {
        if (working) {
            if (firstStep) {
                firstLuckShot();
                firstStep = false;
            } else {
                noWay = true;
                solved = true;
                for (Cell[] line : cells) {
                    for (Cell cell : line) {
                        if (cell.getState() == 0) cell.setUseless(true);

                        if (!cell.isUseless() && !cell.isHidden() && !cell.isMarked()) {
                            ezOpen(cell);
                            if (noWay) findSafeCells(cell);
                        }
                        if (cell.isHidden() && !cell.isMarked()) {
                            solved = false;
                        }
                        if (!noWay) {
                            break;
                        }
                    }
                    if (!noWay) break;
                }
                if (noWay) if (checkCorner()) firstLuckShot();
                else bestEverProbability();
                if (noWay && !solved) luckShot();
            }
        }
    }

    public void clearCellLists() {
        cellsToOpen.clear();
        cellsToMark.clear();
    }

    private List<Cell> getNeighbours(Cell cell) {
        int cell_x = cell.getXPosition();
        int cell_y = cell.getYPosition();
        List<Cell> localNeighbours = new ArrayList<Cell>();

        for (int i = cell_x - 1; i <= cell_x + 1; i++)
            for (int k = cell_y - 1; k <= cell_y + 1; k++) {
                try {
                    Cell localCell = cells[i][k];
                    if (localCell.isHidden() && !(i == cell_x && k == cell_y))
                        localNeighbours.add(localCell);
                } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                    //ignore
                }
            }
        return localNeighbours;
    }

    private List<Cell> getNotMarkedNeighbours(Cell cell) {
        int cell_x = cell.getXPosition();
        int cell_y = cell.getYPosition();
        List<Cell> localNeighbours = new ArrayList<Cell>();

        for (int i = cell_x - 1; i <= cell_x + 1; i++)
            for (int k = cell_y - 1; k <= cell_y + 1; k++) {
                try {
                    Cell localCell = cells[i][k];
                    if (localCell.isHidden() && !localCell.isMarked() && !(i == cell_x && k == cell_y))
                        localNeighbours.add(localCell);
                } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                    //ignore
                }
            }
        return localNeighbours;
    }

    private List<Cell> getOpenNeighbours(Cell cell) {
        int cell_x = cell.getXPosition();
        int cell_y = cell.getYPosition();
        List<Cell> localNeighbours = new ArrayList<Cell>();

        for (int i = cell_x - 1; i <= cell_x + 1; i++)
            for (int k = cell_y - 1; k <= cell_y + 1; k++) {
                try {
                    Cell localCell = cells[i][k];
                    if (!localCell.isHidden() && !localCell.isMarked() && !(i == cell_x && k == cell_y))
                        localNeighbours.add(localCell);
                } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                    //ignore
                }
            }
        return localNeighbours;
    }

    private int getCurrentState(Cell cell) {
        int currentState = cell.getState();
        List<Cell> neighbours = getNeighbours(cell);
        for (Cell localCell : neighbours) if (localCell.isMarked()) currentState--;
        return currentState;
    }

    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        this.working = working;
    }

    public void setFirstStep(boolean firstStep) {
        this.firstStep = firstStep;
    }

    public boolean isNoWay() {
        return noWay;
    }

    public void setNoWay(boolean noWay) {
        this.noWay = noWay;
    }

    public boolean isSolved() {
        return solved;
    }

    public Set<Cell> getCellsToOpen() {
        return cellsToOpen;
    }

    public Set<Cell> getCellsToMark() {
        return cellsToMark;
    }
}
