package Main;

import Graphics.GUI;
import Graphics.GUIElement;
import Graphics.Sprite;

import static Graphics.GUI.CELL_SIZE;
import static Graphics.GUI.getCells;
import static Graphics.Sprite.spriteByNumber;


public class Cell implements GUIElement {

    private int x;
    private int y;
    private int state;
    private boolean isMarked, isHidden, useless;

    public Cell(int x, int y, int state) {
        this.x = x;
        this.y = y;
        this.state = state;
        this.isMarked = false;
        this.isHidden = true;
        this.useless = false;
    }

    @Override
    public Sprite getSprite() {
        if (this.isMarked) {
            if (!this.isHidden && this.state != -1) {
                //Если эта клетка не скрыта, и на ней ошибочно стоит флажок
                return Sprite.BROKEN_FLAG;
            }
            //В другом случае --
            return Sprite.FLAG;
        } else if (this.isHidden) {
            //Если клетка не помечена, притом скрыта...
            return Sprite.HIDDEN;
        } else {
            //Если не помечена и не скрыта, выводим как есть:
            switch (state) {
                case -2:
                    return Sprite.EXPLOSION;
                case -1:
                    return Sprite.BOMB;
                default:
                    assert (state >= 0 && state <= 8) : "Some crap :c";
                    return spriteByNumber[state];
            }
        }
    }


    public int receiveClick(int button) {
        if (isHidden) {
            if (button == 0 && !this.isMarked) {

                this.isHidden = false;

                if (this.state == -1) {
                    //Если это была мина, меняем состояние
                    //на взорванную и передаём сигнал назад
                    this.state = -2;
                    return -1;
                }

                if (this.state == 0) {
                    return 1;
                }

            } else if (button == 1) {
                this.isMarked = !this.isMarked;
            }
        }
        return 0;
    }

    public void show() {
        this.isHidden = false;
    }

    public void incNearMines() {
        if (state >= 0) {
            state++;
        }
    }

    public void removeFromGlobalCells() {
        GUI.getCells()[getXPosition()][getYPosition()] = null;
    }

    @Override
    public int getWidth() {
        return CELL_SIZE;
    }

    @Override
    public int getHeight() {
        return CELL_SIZE;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    public int getXPosition() {
        return x / CELL_SIZE;
    }

    public int getYPosition() {
        return y / CELL_SIZE;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public void setHidden(boolean hidden) {
        isHidden = hidden;
    }

    public boolean isUseless() {
        return useless;
    }

    public void setUseless(boolean useless) {
        this.useless = useless;
    }
}
