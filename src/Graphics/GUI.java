package Graphics;

import Main.Cell;
import Main.Game;
import Main.Generator;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;

public class GUI {

    private static Cell[][] cells;
    public static final int CELLS_COUNT_X = 14;
    public static final int CELLS_COUNT_Y = 16;
    public static final int CELL_SIZE = 48;
    private static final int DIFFICULTY = 15;
    public static final int SCREEN_WIDTH = CELLS_COUNT_X * CELL_SIZE;
    public static final int SCREEN_HEIGHT = CELLS_COUNT_Y * CELL_SIZE;
    private static final String NAME = "Unpredictable Mines";

    public static void init() {
        initializeOpenGL();
        cells = Generator.INSTANCE.generate();
    }

    private static void initializeOpenGL() {
        try {
            //Задаём размер будущего окна
            Display.setDisplayMode(new DisplayMode(SCREEN_WIDTH, SCREEN_HEIGHT));

            //Задаём имя будущего окна
            Display.setTitle(NAME);

            //Создаём окно
            Display.create();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, SCREEN_WIDTH, 0, SCREEN_HEIGHT, 1, -1);
        glMatrixMode(GL_MODELVIEW);

		/*
         * Для поддержки текстур
		 */
        glEnable(GL_TEXTURE_2D);

		/*
         * Для поддержки прозрачности
		 */
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


		/*
         * Белый фоновый цвет
		 */
        glClearColor(1, 1, 1, 1);
    }

    public static void receiveClick(int x, int y, int button) {
        int cell_x = x / CELL_SIZE;
        int cell_y = y / CELL_SIZE;

        int result = cells[cell_x][cell_y].receiveClick(button);
        //System.out.println("Click (" + button + ") on [" + cell_x + "][" + cell_y + "]");
        if (result == 1) {
            ///Делаем вид, что тыкнули в клетки
            ///Сверху, снизу, справа и слева
            ///Игнорируем выхождение за границы поля
            try {
                receiveClick(x + CELL_SIZE, y, button);
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                //ignore
            }
            try {
                receiveClick(x - CELL_SIZE, y, button);
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                //ignore
            }
            try {
                receiveClick(x, y + CELL_SIZE, button);
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                //ignore
            }
            try {
                receiveClick(x, y - CELL_SIZE, button);
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                //ignore
            }

            try {
                receiveClick(x + CELL_SIZE, y + CELL_SIZE, button);
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                //ignore
            }
            try {
                receiveClick(x + CELL_SIZE, y - CELL_SIZE, button);
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                //ignore
            }
            try {
                receiveClick(x - CELL_SIZE, y - CELL_SIZE, button);
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                //ignore
            }
            try {
                receiveClick(x - CELL_SIZE, y + CELL_SIZE, button);
            } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                //ignore
            }
        }
        if (result == -1) gameover();
    }

    public static void gameover() {
        Game.INSTANCE.setEnd_of_game(true);
        Game.INSTANCE.getBotUseless().setWorking(false);
        Game.INSTANCE.getBotUseless().setNoWay(false);
        for (Cell[] line : cells) {
            for (Cell cell : line) {
                cell.show();
            }
        }
        System.out.println("GAMEOVER");
    }

    ///Рисует все клетки
    public static void draw() {
        ///Очищает экран от старого изображения
        glClear(GL_COLOR_BUFFER_BIT);

        for (GUIElement[] line : cells) {
            for (GUIElement cell : line) {
                drawElement(cell);
            }
        }
    }

    ///Рисует элемент, переданный в аргументе
    private static void drawElement(GUIElement elem) {
        elem.getSprite().getTexture().bind();

        glBegin(GL_QUADS);
        glTexCoord2f(0, 0);
        glVertex2f(elem.getX(), elem.getY() + elem.getHeight());
        glTexCoord2f(1, 0);
        glVertex2f(elem.getX() + elem.getWidth(), elem.getY() + elem.getHeight());
        glTexCoord2f(1, 1);
        glVertex2f(elem.getX() + elem.getWidth(), elem.getY());
        glTexCoord2f(0, 1);
        glVertex2f(elem.getX(), elem.getY());
        glEnd();
    }

    public static Cell[][] getCells() {
        return cells;
    }

    public static void setCells(Cell[][] cells) {
        GUI.cells = cells;
    }

    public static int getDIFFICULTY() {
        return DIFFICULTY;
    }
}
