package engine.widget;

public abstract class Container {
    private int x;
    private int y;
    private int width;
    private int height;
    
    public Container(int x, int y, int width, int height) {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void reposition(int newX, int newY) {
        x = newX;
        y = newY;
    }
}
