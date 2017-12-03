package Main;

import AI.BotUseless;
import Graphics.GUI;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public enum Game {
    INSTANCE;

    private BotUseless botUseless;
    private boolean end_of_game = false;
    private float timeBetweenMoves;

    private static final float WAIT_TIME = 0.0175f;
    private static final int BUTTON_OPEN = 0;
    private static final int BUTTON_SET_FLAG = 1;

    Game() {
        this.botUseless = new BotUseless(GUI.getCells());
        this.timeBetweenMoves = WAIT_TIME;
    }

    //Если за последний такт произошли какие-то события с мышью, перебираем их по очереди
    private void input() {
        while (Mouse.next()) {
            //Если это было нажатие кнопки мыши, а не перемещение...
            if (Mouse.getEventButton() >= 0 && Mouse.getEventButtonState()) {
                //Отсылаем это на обработку в GUI
                GUI.receiveClick(Mouse.getEventX(), Mouse.getEventY(), Mouse.getEventButton());
            }
        }

        //То же самое с клавиатурой
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
                    end_of_game = true;
                    botUseless.setWorking(false);
                }
                if (Keyboard.getEventKey() == Keyboard.KEY_G) {
                    botUseless.setWorking(!botUseless.isWorking());
                }
            }
        }

        //Обрабатываем клик по кнопке "закрыть" окна
        end_of_game = end_of_game || Display.isCloseRequested();
    }


    public void update() {
        input();

        if (timeBetweenMoves >= 0) {
            timeBetweenMoves -= Clock.INSTANCE.getDelta();
        } else {
            botUseless.update();
            for (Cell cell : botUseless.getCellsToOpen()) GUI.receiveClick(cell.getX(), cell.getY(), BUTTON_OPEN);
            for (Cell cell : botUseless.getCellsToMark()) GUI.receiveClick(cell.getX(), cell.getY(), BUTTON_SET_FLAG);
            botUseless.clearCellLists();
            timeBetweenMoves = WAIT_TIME;
            if (botUseless.isNoWay() && botUseless.isSolved()) GUI.gameover();
        }
        GUI.draw();
    }

    public boolean isEnd_of_game() {
        return end_of_game;
    }

    public void setEnd_of_game(boolean end_of_game) {
        this.end_of_game = end_of_game;
    }

    public BotUseless getBotUseless() {
        return botUseless;
    }
}
