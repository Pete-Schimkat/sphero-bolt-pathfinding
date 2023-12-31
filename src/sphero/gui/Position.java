package sphero.gui;

public class Position {

    private int x;
    private int y;
    private char character;

    public Position(int x, int y, char character) {
        this.x = x;
        this.y = y;
        this.character = character;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public char getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    @Override
    public String toString() {
        return "Position of Robot: " + x + "X" + y;
    }
}