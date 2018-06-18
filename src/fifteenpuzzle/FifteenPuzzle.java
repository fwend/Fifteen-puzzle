package fifteenpuzzle;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class FifteenPuzzle extends JPanel {
    final static int numTiles = 15;
    final static int side = 4;

    Random rand = new Random();
    int[] tiles = new int[numTiles + 1];
    int tileSize, blankPos, margin, gridSize;

    public FifteenPuzzle() {
        final int dim = 640;

        margin = 80;
        tileSize = (dim - 2 * margin) / side;
        gridSize = tileSize * side;

        setPreferredSize(new Dimension(dim, dim));
        setBackground(Color.white);
        setForeground(new Color(0x6495ED));
        setFont(new Font("SansSerif", Font.BOLD, 60));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int ex = e.getX() - margin;
                int ey = e.getY() - margin;

                if (ex < 0 || ex > gridSize || ey < 0 || ey > gridSize)
                    return;

                int c1 = ex / tileSize;
                int r1 = ey / tileSize;
                int c2 = blankPos % side;
                int r2 = blankPos / side;

                if ((c1 == c2 && Math.abs(r1 - r2) == 1)
                        || (r1 == r2 && Math.abs(c1 - c2) == 1)) {

                    int clickPos = r1 * side + c1;
                    tiles[blankPos] = tiles[clickPos];
                    tiles[clickPos] = 0;
                    blankPos = clickPos;
                }
                repaint();
            }
        });

        shuffle();
    }

    final void shuffle() {
        do {
            reset();
            // don't include the blank space in the shuffle, leave it
            // in the home position
            int n = numTiles;
            while (n > 1) {
                int r = rand.nextInt(n--);
                int tmp = tiles[r];
                tiles[r] = tiles[n];
                tiles[n] = tmp;
            }
        } while (!isSolvable());
    }

    void reset() {
        for (int i = 0; i < tiles.length; i++)
            tiles[i] = (i + 1) % tiles.length;
        blankPos = numTiles;
    }

    /*  Only half the permutations of the puzzle are solvable.

        Whenever a tile is preceded by a tile with higher value it counts
        as an inversion. In our case, with the blank space in the home
        position, the number of inversions must be even for the puzzle
        to be solvable.

        See also:
        cs.bham.ac.uk/~mdr/teaching/modules04/java2/TilesSolvability.html
    */
    boolean isSolvable() {
        int countInversions = 0;
        for (int i = 0; i < numTiles; i++) {
            for (int j = 0; j < i; j++) {
                if (tiles[j] > tiles[i])
                    countInversions++;
            }
        }
        return countInversions % 2 == 0;
    }

    void drawGrid(Graphics2D g) {
        for (int i = 0; i < tiles.length; i++) {
            if (tiles[i] == 0)
                continue;

            int r = i / side;
            int c = i % side;
            int x = margin + c * tileSize;
            int y = margin + r * tileSize;

            g.setColor(getForeground());
            g.fillRoundRect(x, y, tileSize, tileSize, 25, 25);
            g.setColor(Color.black);
            g.drawRoundRect(x, y, tileSize, tileSize, 25, 25);
            g.setColor(Color.white);

            drawCenteredString(g, String.valueOf(tiles[i]), x, y);
        }
    }

    void drawCenteredString(Graphics2D g, String s, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        int asc = fm.getAscent();
        int dec = fm.getDescent();

        x = x + (tileSize - fm.stringWidth(s)) / 2;
        y = y + (asc + (tileSize - (asc + dec)) / 2);

        g.drawString(s, x, y);
    }

    @Override
    public void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawGrid(g);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setTitle("Fifteen Puzzle");
            f.setResizable(false);
            f.add(new FifteenPuzzle(), BorderLayout.CENTER);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}