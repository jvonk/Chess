import java.awt.*;
public class Tile {
    public Piece p;
    private int x, y, width, height;
    private Color color;
    public Tile(int x, int y, int width, int height, boolean side) {
        this.p = null;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = side ? new Color(230, 230, 230) : new Color(100, 100, 100);
    }
    public void move(Tile t) {
        p.moved = true;
        t.p = p;
        p = null;
    }
    public void drawMe(Graphics2D g, int x, int y, int highlight) {
        float[] c = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        g.setColor(Color.getHSBColor(c[0], c[1], (1f - (float)Math.pow(1f - c[2], highlight + 1))));
        g.fillRect(x, y, this.width, this.height);
        if(p != null) {
            p.drawMe(g, x, y);
        }
    }
}
