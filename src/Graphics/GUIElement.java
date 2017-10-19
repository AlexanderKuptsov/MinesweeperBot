package Graphics;

public interface GUIElement {

    int getWidth();

    int getHeight();

    int getY();

    int getX();

    Sprite getSprite();

    int receiveClick( int button); /// Возвращаем результат клика
    ///Параметр button определяет кнопку мыши, которой был сделан щелчок.

    default boolean isHit(int xclick, int yclick) {
        return ((xclick > getX()) && (xclick < getX() + this.getWidth()))
                && ((yclick > getY()) && (yclick < getY() + this.getHeight()));
    }
}
