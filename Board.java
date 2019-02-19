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
                board[i][j] = new Tile((int)(width / board[i].length), (int)(height / board.length), (i + j) % 2 == 0);
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
        Point start = new Point(-1, -1);
        if(dragStart != null) {
            start = getPosition(dragStart);
        } else if(cursor != null) {
            start = getPosition(cursor);
        }
        Point h = new Point(-1, -1);
        if(hover != null) {
            h = getPosition(hover);
        }
        for(int i = 0; i < board.length; i++) {
            for(int j = 0; j < board[i].length; j++) {
                board[i][j].drawMe(g, new Point(j * width / board[i].length - width / 2, i * height / board.length - height / 2), ((checkBounds(start) && move(start, new Point(j, i))) ? 1 : 0) + (i == h.y && j == h.x ? 1 : 0));
            }
        }
    }

    public boolean checkBounds(Point p) {
        return (p.x >= 0 && p.y >= 0 && p.y < board.length && p.x < board[0].length);
    }

    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    public Point getPosition(Point p) {
        return new Point((int)(p.getX()*board[0].length / width), (int)(p.getY()*board.length / height));
    }

    public boolean move(Point s, Point e) {
        Point d = new Point(e.x - s.x, e.y - s.y);
        if(!checkBounds(e)) {
            return false;
        }
        Piece start = board[s.y][s.x].p;
        Piece end = board[e.y][e.x].p;
        if(start == null || start.side != turn) {
            return false;
        }
        if(end != null && start.side == end.side) {
            return false;
        }
        if(d.x == 0 && d.y == 0) {
            return true;
        }
        if(start.type == 1) {
            if((d.y == 1 && !start.side) || (d.y == -1 && start.side)) {
                if(end == null&&d.x == 0) {
                    return true;
                } else if(end!=null&&end.type>0&&(d.x == -1 || d.x == 1)) {
                    return true;
                }
            } else if((d.y == 2 && !start.side) || (d.y == -2 && start.side)) {
                if(end == null && d.x == 0 && !start.moved && board[s.y + (start.side ? -1 : 1)][s.x].p == null) {
                    return true;
                }
            }
        } else if(start.type == 2) {
            if(d.y == d.x && checkLine(2, s.x, s.y, Math.min(s.x, e.x) + 1, Math.max(s.x, e.x) - 1)) {
                return true;
            } else if(d.y == -d.x && checkLine(3, s.x, s.y, Math.min(s.x, e.x) + 1, Math.max(s.x, e.x) - 1)) {
                return true;
            }
        } else if(start.type == 3) {
            if(((d.x == 2 || d.x == -2) && (d.y == 1 || d.y == -1)) || (d.x == 1 || d.x == -1) && (d.y == 2 || d.y == -2)) {
                return true;
            }
        } else if(start.type == 4) {
            if(d.x == 0 && checkLine(0, s.x, -1, Math.min(s.y, e.y) + 1, Math.max(s.y, e.y) - 1)) {
                return true;
            } else if(d.y == 0 && checkLine(1, s.y, -1, Math.min(s.x, e.x) + 1, Math.max(s.x, e.x) - 1)) {
                return true;
            }
        } else if(start.type == 5) {
            if(d.x == 0 && checkLine(0, s.x, -1, Math.min(s.y, e.y) + 1, Math.max(s.y, e.y) - 1)) {
                return true;
            } else if(d.y == 0 && checkLine(1, s.y, -1, Math.min(s.x, e.x) + 1, Math.max(s.x, e.x) - 1)) {
                return true;
            } else if(d.y == d.x && checkLine(2, s.x, s.y, Math.min(s.x, e.x) + 1, Math.max(s.x, e.x) - 1)) {
                return true;
            } else if(d.y == -d.x && checkLine(3, s.x, s.y, Math.min(s.x, e.x) + 1, Math.max(s.x, e.x) - 1)) {
                return true;
            }
        } else if(start.type == 6) {
            if(d.x <= 1 && d.y <= 1 && d.x >= -1 && d.y >= -1) {
                return true;
            }
        }
        return false;
    }

    public boolean checkLine(int which, int a, int b, int s, int e) {
        for(int i = s; i <= e; i++) {
            if(board[which == 0 ? i : (which == 1 ? a : (which == 2 ? i - a + b : a - i + b))][which == 0 ? a : i].p != null) {
                return false;
            }
        }
        return true;
    }

    public void handleDrag(Point start, Point end) {
        if(move(start, end)) {
            turn = !turn;
            if (board[start.y][start.x].p.type==1&&(board[start.y][start.x].p.side?end.y==0:end.y==board.length-1)) {
                board[start.y][start.x].p = new Piece(5, (int)(width / board[end.y].length), (int)(height / board.length), board[start.y][start.x].p.side);
                board[start.y][start.x].p.moved = true;
            } else if ((end.y==start.y)&&board[start.y][start.x].p.type==4&&!board[start.y][start.x].p.moved&&board[start.y][4].p.type==6&&!board[start.y][4].p.moved) {
                if (end.x==start.x+3&&checkLine(1, start.y, -1, start.x+1, end.x)) {
                    board[start.y][4].move(board[start.y][2]);
                } else if (end.x==start.x-2&&checkLine(1, start.y, -1, 5, start.x-1)) {
                    board[start.y][4].move(board[start.y][6]);
                }
            }
            board[start.y][start.x].move(board[end.y][end.x]);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(dragStart == null) {
            dragStart = e.getPoint();
            hover = dragStart;
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(dragStart != null) {
            handleDrag(getPosition(dragStart), getPosition(e.getPoint()));
            repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        cursor = e.getPoint();
        hover = cursor;
        repaint();
    }


    @Override
    public void mouseMoved(MouseEvent e) {
        dragStart = null;
        hover = e.getPoint();
        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        hover = e.getPoint();
        repaint();
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
