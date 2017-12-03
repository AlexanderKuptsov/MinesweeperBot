package AI;

import Main.Cell;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static Graphics.GUI.CELL_SIZE;
import static org.junit.Assert.*;

public class BotUselessTest {

    @Test
    public void ezOpenTest() {
        Cell[][] cells = new Cell[2][2];
        cells[0][0] = new Cell(0, 0, 1);
        cells[0][1] = new Cell(0, CELL_SIZE, 1);
        cells[1][0] = new Cell(CELL_SIZE, 0, 1);
        cells[1][1] = new Cell(CELL_SIZE, CELL_SIZE, -1);
        cells[0][1].setHidden(false);
        cells[1][0].setHidden(false);
        cells[0][0].setHidden(false);

        BotUseless botUseless = new BotUseless(cells);
        botUseless.ezOpen(cells[0][1]);
        assertEquals(true, botUseless.getCellsToMark().contains(cells[1][1]));

        botUseless.clearCellLists();
        botUseless.setFirstStep(false);
        botUseless.update();
        assertEquals(true, botUseless.getCellsToMark().contains(cells[1][1]));
    }

    @Test
    public void ezOpenTest2() {
        Cell[][] cells = new Cell[2][2];
        cells[0][0] = new Cell(0, 0, 1);
        cells[0][1] = new Cell(0, CELL_SIZE, 1);
        cells[1][0] = new Cell(CELL_SIZE, 0, -1);
        cells[1][1] = new Cell(CELL_SIZE, CELL_SIZE, 1);
        cells[0][0].setHidden(false);
        cells[0][1].setHidden(false);
        cells[1][0].setMarked(true);

        BotUseless botUseless = new BotUseless(cells);
        botUseless.ezOpen(cells[0][1]);
        assertEquals(true, botUseless.getCellsToOpen().contains(cells[1][1]));

        botUseless.clearCellLists();
        botUseless.setFirstStep(false);
        botUseless.update();
        assertEquals(true, botUseless.getCellsToOpen().contains(cells[1][1]));
    }

    @Test
    public void findSafeCellsTest() {
        Cell[][] cells = new Cell[3][2];
        cells[0][0] = new Cell(0, 0, 1);
        cells[1][0] = new Cell(CELL_SIZE, 0, 1);
        cells[2][0] = new Cell(CELL_SIZE * 2, 0, 1);
        cells[0][1] = new Cell(0, CELL_SIZE, 1);
        cells[1][1] = new Cell(CELL_SIZE, CELL_SIZE, -1);
        cells[2][1] = new Cell(CELL_SIZE * 2, CELL_SIZE, 1);
        for (Cell[] cell : cells) cell[0].setHidden(false);

        BotUseless botUseless = new BotUseless(cells);
        botUseless.findSafeCells(cells[0][0]);
        Set result = new HashSet<Cell>(Collections.singletonList(cells[2][1]));
        assertEquals(result, botUseless.getCellsToOpen());

        botUseless.clearCellLists();
        botUseless.setFirstStep(false);
        botUseless.update();
        assertEquals(result, botUseless.getCellsToOpen());
    }

    @Test
    public void findSafeCellsTest2() {
        Cell[][] cells = new Cell[3][2];
        cells[0][0] = new Cell(0, 0, 1);
        cells[1][0] = new Cell(CELL_SIZE, 0, 2);
        cells[2][0] = new Cell(CELL_SIZE * 2, 0, 1);
        cells[0][1] = new Cell(0, CELL_SIZE, 1);
        cells[1][1] = new Cell(CELL_SIZE, CELL_SIZE, -1);
        cells[2][1] = new Cell(CELL_SIZE * 2, CELL_SIZE, 1);
        for (Cell[] cell : cells) cell[0].setHidden(false);

        BotUseless botUseless = new BotUseless(cells);
        botUseless.findSafeCells(cells[0][0]);
        Set result = new HashSet<Cell>(Collections.singletonList(cells[2][1]));
        assertEquals(result, botUseless.getCellsToMark());

        botUseless.clearCellLists();
        botUseless.setFirstStep(false);
        botUseless.update();
        assertEquals(result, botUseless.getCellsToMark());
    }

    @Test
    public void firstLuckShotTest() {
        Cell[][] cells = new Cell[3][2];
        cells[0][0] = new Cell(0, 0, 1);
        cells[1][0] = new Cell(CELL_SIZE, 0, -1);
        cells[2][0] = new Cell(CELL_SIZE * 2, 0, 2);
        cells[0][1] = new Cell(0, CELL_SIZE, 1);
        cells[1][1] = new Cell(CELL_SIZE, CELL_SIZE, 2);
        cells[2][1] = new Cell(CELL_SIZE * 2, CELL_SIZE, -1);
        cells[0][0].setHidden(false);
        cells[0][1].setHidden(false);
        cells[2][1].setMarked(true);

        BotUseless botUseless = new BotUseless(cells);
        botUseless.firstLuckShot();
        Set result = new HashSet<Cell>(Collections.singletonList(cells[2][0]));
        assertEquals(result, botUseless.getCellsToOpen());

        botUseless.clearCellLists();
        botUseless.update();
        assertEquals(result, botUseless.getCellsToOpen());
    }

    @Test
    public void bestEverProbabilityTest() {
        Cell[][] cells = new Cell[4][3];
        cells[0][0] = new Cell(0, 0, -1);
        cells[1][0] = new Cell(CELL_SIZE, 0, 1);
        cells[2][0] = new Cell(CELL_SIZE * 2, 0, 0);
        cells[3][0] = new Cell(CELL_SIZE * 3, 0, 0);

        cells[0][1] = new Cell(0, CELL_SIZE, 2);
        cells[1][1] = new Cell(CELL_SIZE, CELL_SIZE, 2);
        cells[2][1] = new Cell(CELL_SIZE * 2, CELL_SIZE, 1);
        cells[3][1] = new Cell(CELL_SIZE * 3, CELL_SIZE, 1);

        cells[0][2] = new Cell(0, CELL_SIZE * 2, -1);
        cells[1][2] = new Cell(CELL_SIZE, CELL_SIZE * 2, 1);
        cells[2][2] = new Cell(CELL_SIZE * 2, CELL_SIZE * 2, 1);
        cells[3][2] = new Cell(CELL_SIZE * 3, CELL_SIZE * 2, -1);
        for (Cell[] cell : cells) cell[1].setHidden(false);
        cells[2][1].setHidden(true);
        cells[2][2].setHidden(false);

        BotUseless botUseless = new BotUseless(cells);
        botUseless.bestEverProbability();
        Set result = new HashSet<Cell>(Collections.singletonList(cells[3][0]));
        assertEquals(result, botUseless.getCellsToOpen());
    }

    @Test
    public void phantomMoveTest() {
        Cell[][] cells = new Cell[3][3];
        cells[0][0] = new Cell(0, 0, 1);
        cells[1][0] = new Cell(CELL_SIZE, 0, 1);
        cells[2][0] = new Cell(CELL_SIZE * 2, 0, 1);

        cells[0][1] = new Cell(0, CELL_SIZE, 2);
        cells[1][1] = new Cell(CELL_SIZE, CELL_SIZE, -1);
        cells[2][1] = new Cell(CELL_SIZE * 2, CELL_SIZE, 1);

        cells[0][2] = new Cell(0, CELL_SIZE * 2, -1);
        cells[1][2] = new Cell(CELL_SIZE, CELL_SIZE * 2, 2);
        cells[2][2] = new Cell(CELL_SIZE * 2, CELL_SIZE * 2, 1);
        cells[0][0].setHidden(false);
        cells[2][0].setHidden(false);
        cells[0][1].setHidden(false);
        cells[1][2].setHidden(false);
        cells[2][2].setHidden(false);
        cells[0][2].setMarked(true);

        BotUseless botUseless = new BotUseless(cells);
        Set result = new HashSet<Cell>(Collections.singletonList(cells[1][1]));
        botUseless.phantomMove(cells[1][1]);
        assertEquals(result, botUseless.getCellsToMark());

        botUseless.clearCellLists();
        result.clear();
        botUseless.phantomMove(cells[1][0]);
        botUseless.phantomMove(cells[2][1]);
        assertEquals(result, botUseless.getCellsToMark());
    }
}