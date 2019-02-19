import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
public class Board extends JPanel implements MouseListener, MouseMotionListener {
    private int width, height;
    private Point dragStart, cursor, hover;
    private Tile[][] board;
    private boolean turn;
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        addMouseListener(this);
        addMouseMotionListener(this);
        setup();
    }
    public void setup() {
        dragStart = null;
        cursor = null;
        hover = null;
        turn = true;
        board = new Tile[8][8];
        int[] order = new int[] {4, 3, 2, 5, 6, 2, 3, 4};
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                int type = 0;
                if(i == 0 || i == board.length - 1) {
                    type = order[j];
                } else if(i == 1 || i == board.length - 2) {
                    type = 1;
                }
                board[i][j] = new Tile(j, i, (int)(width / board[i].length), (int)(height / board.length), (i + j) % 2 == 0);
                if(type != 0) {
                    board[i][j].p = new Piece(type, (int)(width / board[i].length), (int)(height / board.length), i >= board.length / 2);
                }
            }
        }
    }

    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics;
        g.translate(width / 2, height / 2);
        int[] start = new int[] {-1, -1};
        if(dragStart != null) {
            start = getPosition(dragStart);
        } else if(cursor != null) {
            start = getPosition(cursor);
        }
        int[] h = new int[] {-1, -1};
        if(hover != null) {
            h = getPosition(hover);
        }
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                board[i][j].drawMe(g, j * width / board[i].length - width / 2, i * height / board.length - height / 2, ((start[0] >= 0 && start[1] >= 0 && start[1] < board.length && start[0] < board[0].length && move(start, new int[] {j, i})) ? 1 : 0) + (i == h[1] && j == h[0] ? 1 : 0));
            }
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    public int[] getPosition(Point p) {
        return new int[] {(int)((p.getX() - 800)*board[0].length / width), (int)((p.getY() - 400)*board.length / height)};
    }

    public boolean move(int[] start, int[] end) {
        int sX = start[0];
        int sY = start[1];
        int eX = end[0];
        int eY = end[1];
        int dX = eX - sX;
        int dY = eY - sY;
        if(sX < 0 || sX >= board[0].length || sY < 0 || sY >= board.length) {
            return false;
        }
        Piece s = board[sY][sX].p;
        Piece e = board[eY][eX].p;
        if(s == null) {
            return false;
        }
        if(s.side != turn) {
            return false;
        }
        if(e != null && s.side == e.side) {
            return false;
        }
        if(dX == 0 && dY == 0) {
            return true;
        }
        if(s.type == 1) {
            if((dY == 1 && !s.side) || (dY == -1 && s.side)) {
                if(e == null) {
                    if(dX == 0) {
                        return true;
                    }
                } else if(dX == -1 || dX == 1) {
                    return true;
                }
            } else if((dY == 2 && !s.side) || (dY == -2 && s.side)) {
                if(e == null && dX == 0 && !s.moved && board[sY + (s.side ? -1 : 1)][sX].p == null) {
                    return true;
                }
            }
        } else if(s.type == 2) {
            if(dY == dX) {
                if(checkLine(2, sX, sY, Math.min(sX, eX) + 1, Math.max(sX, eX) - 1)) {
                    return false;
                }
            } else if(dY == -dX) {
                if(checkLine(3, sX, sY, Math.min(sX, eX) + 1, Math.max(sX, eX) - 1)) {
                    return false;
                }
            } else {
                return false;
            }
            return true;
        } else if(s.type == 3) {
            if(((dX == 2 || dX == -2) && (dY == 1 || dY == -1)) || (dX == 1 || dX == -1) && (dY == 2 || dY == -2)) {
                return true;
            }
        } else if(s.type == 4) {
            if(dX == 0) {
                if(checkLine(0, sX, -1, Math.min(sY, eY) + 1, Math.max(sY, eY) - 1)) {
                    return false;
                }
            } else if(dY == 0) {
                if(checkLine(1, sY, -1, Math.min(sX, eX) + 1, Math.max(sX, eX) - 1)) {
                    return false;
                }
            } else {
                return false;
            }
            return true;
        } else if(s.type == 5) {
            if(dY == dX) {
                if(checkLine(2, sX, sY, Math.min(sX, eX) + 1, Math.max(sX, eX) - 1)) {
                    return false;
                }
            } else if(dY == -dX) {
                if(checkLine(3, sX, sY, Math.min(sX, eX) + 1, Math.max(sX, eX) - 1)) {
                    return false;
                }
            } else if(dX == 0) {
                if(checkLine(0, sX, -1, Math.min(sY, eY) + 1, Math.max(sY, eY) - 1)) {
                    return false;
                }
            } else if(dY == 0) {
                if(checkLine(1, sY, -1, Math.min(sX, eX) + 1, Math.max(sX, eX) - 1)) {
                    return false;
                }
            } else {
                return false;
            }
            return true;
        } else if(s.type == 6) {
            if(dX <= 1 && dY <= 1 && dX >= -1 && dY >= -1) {
                return true;
            }
        }
        return false;
    }

    public boolean checkLine(int which, int a, int b, int s, int e) {
        for(int i = s; i <= e; i++) {
            if(board[which == 0 ? i : (which == 1 ? a : (which == 2 ? i - a + b : a - i + b))][which == 0 ? a : i].p != null) {
                return true;
            }
        }
        return false;
    }

    public void handleDrag(int[] start, int[] end) {
        if(move(start, end)) {
            turn = !turn;
            board[start[1]][start[0]].move(board[end[1]][end[0]]);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(dragStart == null) {
            dragStart = e.getLocationOnScreen();
            hover = dragStart;
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(dragStart != null) {
            handleDrag(getPosition(dragStart), getPosition(e.getLocationOnScreen()));
            repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        cursor = e.getLocationOnScreen();
        hover = cursor;
        repaint();
    }


    @Override
    public void mouseMoved(MouseEvent e) {
        dragStart = null;
        hover = e.getLocationOnScreen();
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        hover = e.getLocationOnScreen();
        repaint();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
