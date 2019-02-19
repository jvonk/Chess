import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class Piece {
    public int type, width, height;
    public boolean side, moved;
    private BufferedImage image;
    public Piece(int type, int width, int height, boolean side) {
        this.type = type;
        this.side = side;
        this.width = width;
        this.height = height;
        this.moved = false;
        try {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D scaled = image.createGraphics();
            scaled.setComposite(AlphaComposite.Src);
            String[] names = new String[] {"null", "Pawn", "Bishop", "Knight", "Rook", "Queen", "King"};
            scaled.drawImage(ImageIO.read(new File("image/" + (side ? "White" : "Black") + names[type] + ".png")), 0, 0, width, height, null);
            scaled.dispose();
        } catch(IOException e) {
            System.out.println(e);
        }
    }
    public void drawMe(Graphics2D g, int x, int y) {
        g.drawImage(image, x, y, null);
    }
}
