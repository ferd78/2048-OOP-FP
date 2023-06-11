import java.util.Scanner;
import java.util.NoSuchElementException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;

public class Game extends JFrame implements ActionListener
{
    private int slot;
    private int score;
    private int highScore;
    private Board board;
    private MainMenu mainMenu;
    private JButton saveButton;
    private JButton quitButton;
    private JLabel scoreLabel;
    private JLabel highScoreLabel;
    private GameDispatcher dispatcher;

    public Game(int rows, int cols, MainMenu menu) {
        super();
        slot = -1;
        score = 0;
        highScore = 0;
        board = new Board(rows, cols);
        initGUI();
        mainMenu = menu;
    }

    public Game(int slot, MainMenu menu) {
        super();
        if (load(slot))
        {
            initGUI();
            mainMenu = menu;
        }
    }

    private void initGUI()
    {
        //Sets up frame
        setTitle("2048");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(569, 569);
        setMinimumSize(new Dimension(569, 569));

        //Sets up save button
        saveButton = new JButton("Save");
        saveButton.setActionCommand("Save");
        saveButton.addActionListener(this);
        saveButton.setFocusable(false);

        //Sets up quit button
        quitButton = new JButton("Quit");
        quitButton.setActionCommand("Quit");
        quitButton.addActionListener(this);
        quitButton.setFocusable(false);

        //Sets up score label
        scoreLabel = new JLabel("Score: " + score, SwingConstants.CENTER);

        //Sets up high score label
        readHighScoreFromFile();
        highScoreLabel = new JLabel("High Score: " + highScore, SwingConstants.CENTER);

        //Sets up button panel for save, quit, and scores
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2));
        buttonPanel.add(saveButton);
        buttonPanel.add(quitButton);
        buttonPanel.add(scoreLabel);
        buttonPanel.add(highScoreLabel);

        //Sets up board panel for gameplay
        BoardPanel boardPanel = new BoardPanel(board);
        boardPanel.setPreferredSize(new Dimension(420 + 1, 420 + 1));

        //Places all components within single panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(buttonPanel);
        mainPanel.add(boardPanel);

        //Adds main panel to content pane
        Container pane = getContentPane();
        pane.setLayout(new GridBagLayout());
        pane.add(mainPanel);

        //Sets up keyboard for gameplay
        dispatcher = new GameDispatcher();
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher);
        setVisible(true);
    }

    private boolean load(int slot)
    {
        try
        {
            this.slot = slot;
            Scanner in = new Scanner(new File(".slot" + slot + ".sav"));
            score = in.nextInt();
            int rows = in.nextInt();
            int cols = in.nextInt();
            int[][] values = new int[rows][cols];

            for (int i = 0; i < rows; i++)
            {
                for (int j = 0; j < cols; j++)
                {
                    values[i][j] = in.nextInt();
                }
            }

            in.close();
            board = new Board(values);
            return true;
        }
        catch (FileNotFoundException e)
        {
            new GameAlert("Save file not found.");
            return false;
        }
        catch (NoSuchElementException|NumberFormatException|IndexOutOfBoundsException e)
        {
            new GameAlert("Corrupt save file.");
            return false;
        }
    }

    private void save()
    {
        //Save to correct slot
        PrintWriter pw = getPrintWriter(".slot" + slot + ".sav");
        pw.println(score);
        int[][] values = board.getTileValues();
        pw.println(values.length);
        pw.println(values[0].length);

        for (int i = 0; i < values.length; i++)
        {
            for (int j = 0; j < values[0].length; j++)
            {
                pw.println(values[i][j]);
            }
        }

        pw.close();
        writeHighScoreToFile();
    }

    private void writeHighScoreToFile()
    {
        if (score >= highScore)
        {
            PrintWriter pw = getPrintWriter(".2048-highscore.sav");
            pw.println(score);
            pw.close();
        }
    }

    private PrintWriter getPrintWriter(String filePath)
    {
        try
        {
            return new PrintWriter(filePath);
        }
        catch (FileNotFoundException e)
        {
            //Non-user error (not a GameException)
            System.out.println("Could not write to file");
            return null;
        }
    }

    //Reads high score from file and updates instance variable
    private void readHighScoreFromFile()
    {
        File file = new File(".2048-highscore.sav");

        try
        {
            Scanner in = new Scanner(file);
            highScore = in.nextInt();
            in.close();
        }
        catch (NoSuchElementException e)
        {
            highScore = 0;
            new GameAlert("Corrupt high score file.");
        }
        catch (FileNotFoundException e)
        {
            highScore = 0;
        }
    }

    private int chooseNewSlot()
    {
        String[] names = generateSlotNames();
        int val = JOptionPane.showOptionDialog(this,
                "Select a slot.",
                "Save",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                names,
                null);

        //reversed for JOptionPane functionality
        return val == -1 ? -1 : names.length - val;
    }

    private String[] generateSlotNames()
    {
        String[] names = new String[3];

        for (int i = 0; i < names.length; i++)
        {
            String str = ".slot" + (i + 1) + ".sav";
            String name = "Slot " + (i + 1);

            //reversed for JOptionPane functionality
            names[names.length - 1 - i] = new File(str).exists() ? name : name + " Empty";
        }

        return names;
    }

    private void updateScores(int delta)
    {
        score += delta;
        scoreLabel.setText("Score: " + score);

        if (score > highScore)
        {
            highScore = score;
            highScoreLabel.setText("High Score: " + highScore);
        }
    }

    private void gameOver()
    {
        writeHighScoreToFile();
        saveButton.setEnabled(false);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(dispatcher);

        if (slot != -1)
        {
            try
            {
                new File(".slot" + slot + ".sav").delete();
            }
            catch (SecurityException e)
            {
                new GameAlert("Could not delete save file.");
            }
        }

        new GameAlert("Game Over!");
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();

        if (command.equals("Save"))
        {
            slot = slot == -1 ? chooseNewSlot() : slot;

            if (slot > -1)
            {
                save();
            }
        }
        else if (command.equals("Quit"))
        {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(dispatcher);
            mainMenu.setVisible(true);
            dispose();
        }
    }

    private class GameDispatcher implements KeyEventDispatcher
    {
        public boolean dispatchKeyEvent(KeyEvent e)
        {
            if (e.getID() == KeyEvent.KEY_PRESSED)
            {
                if (e.getKeyCode() == KeyEvent.VK_UP)
                {
                    updateScores(board.moveUp());
                }
                else if (e.getKeyCode() == KeyEvent.VK_DOWN)
                {
                    updateScores(board.moveDown());
                }
                else if (e.getKeyCode() == KeyEvent.VK_LEFT)
                {
                    updateScores(board.moveLeft());
                }
                else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                {
                    updateScores(board.moveRight());
                }

                repaint();

                if (!Game.this.board.hasMove())
                {
                    Game.this.gameOver();
                }
            }

            return true;
        }
    }
}
