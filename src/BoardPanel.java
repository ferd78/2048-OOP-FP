import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

public class BoardPanel extends JPanel {
    private Board board;

    public BoardPanel(Board board)
    {
        super();
        this.board = board;
    }

    protected void paintComponent(Graphics g)
    {
        Tile tile = null;

        int winDim = (this.getWidth() < this.getHeight()) ? this.getWidth() : this.getHeight();
        int count = (this.board.getColCount() > this.board.getRowCount()) ? this.board.getColCount() : this.board.getRowCount();
        int dim = winDim / count;
        int rad = (int)(dim / 4.0);
        int i, j;

        for (i = 0; i < this.board.getColCount(); i++) {
            for (j = 0; j < this.board.getRowCount(); j++) {
                tile = this.board.tileAt(j, i);

                if (tile != null) {
                    String str = "" + tile.getVal();

                    int strWid;
                    int strHgt;

                    int fontSize = 0;

                    do {
                        g.setFont(new Font("menlo", Font.PLAIN, fontSize++));

                        strWid = g.getFontMetrics().stringWidth(str);
                        strHgt = (int)g.getFontMetrics().getStringBounds(str, g).getHeight();
                    } while (strWid < dim - 10 && strHgt < dim / 2);

                    g.setColor(tile.getColor());
                    g.fillRoundRect(dim * i, dim * j, dim, dim, rad, rad);

                    int exp = (int)(Math.log(tile.getVal()) / Math.log(2));

                    if (exp % 12 == 1 || exp % 12 == 2) {
                        g.setColor(Color.BLACK);
                    } else {
                        g.setColor(Color.WHITE);
                    }

                    g.setFont(new Font("menlo", Font.PLAIN, fontSize));
                    g.drawString(str, (dim * i) + (dim / 2) - (strWid / 2), (dim * j) + dim - (strHgt / 2));
                } else {
                    g.setColor(Color.WHITE);
                    g.fillRoundRect(dim * i, dim * j, dim, dim, rad, rad);
                }

                g.setColor(Color.BLACK);
                g.drawRoundRect(dim * i, dim * j, dim, dim, rad, rad);
            }
        }
    }
}
