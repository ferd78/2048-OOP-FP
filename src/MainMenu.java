import java.io.File;
import java.awt.GridLayout;
import java.awt.CardLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.border.EmptyBorder;

public class MainMenu extends JFrame implements ActionListener {
    private JButton newButton; //creates a new grid
    private JButton loadButton; //load saved grid
    private JTextField rowInput;
    private JTextField colInput;
    private JButton playButton;
    private JButton slot1Button;
    private JButton slot2Button;
    private JButton slot3Button;
    private JPanel cards;

    public MainMenu()
    {
        //Sets up the frame
        super("2048");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setSize(250, 250);

        //Sets up the new button
        newButton = new JButton("New Game");
        newButton.setActionCommand("New");
        newButton.addActionListener(this);
        newButton.setFocusable(false);

        //Sets up the load button
        loadButton = new JButton("Load Game");
        loadButton.setActionCommand("Load");
        loadButton.addActionListener(this);
        loadButton.setFocusable(false);

        //Places new and load buttons within a single panel
        JPanel controlPanel = new JPanel();
        controlPanel.add(newButton);
        controlPanel.add(loadButton);

        //Sets up new game labels and places them within a single panel
        JLabel rowLabel = new JLabel("Rows:");
        JLabel colLabel = new JLabel("Columns:");
        rowLabel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
        colLabel.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        labelPanel.add(rowLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        labelPanel.add(colLabel);
        labelPanel.setAlignmentY(CENTER_ALIGNMENT);

        //Sets up new game inputs and places them within a single panel
        rowInput = new JTextField("4", 3);
        colInput = new JTextField("4", 3);
        rowInput.setAlignmentX(CENTER_ALIGNMENT);
        colInput.setAlignmentX(CENTER_ALIGNMENT);
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        inputPanel.add(rowInput);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        inputPanel.add(colInput);
        inputPanel.setAlignmentY(CENTER_ALIGNMENT);

        //Places label and input panels within a single panel
        JPanel panel = new JPanel();
        panel.add(labelPanel);
        panel.add(inputPanel);

        //Sets up the play button and places it within a panel
        playButton = new JButton("Play");
        playButton.setActionCommand("Play");
        playButton.addActionListener(this);
        playButton.setFocusable(false);
        playButton.setAlignmentY(CENTER_ALIGNMENT);
        JPanel playPanel = new JPanel();
        playPanel.add(playButton);

        //Sets up the new card and adds the label/input and play panels
        JPanel newCard = new JPanel();
        newCard.setLayout(new BoxLayout(newCard, BoxLayout.Y_AXIS));
        newCard.add(panel);
        newCard.add(playPanel);

        //Sets up the slot 1 button and places it within a panel
        slot1Button = new JButton("Slot 1");
        slot1Button.setActionCommand("Slot1");
        slot1Button.addActionListener(this);
        slot1Button.setFocusable(false);
        slot1Button.setAlignmentY(CENTER_ALIGNMENT);
        slot1Button.setAlignmentX(CENTER_ALIGNMENT);
        JPanel slot1Panel = new JPanel();
        slot1Panel.add(slot1Button);

        //Sets up the slot 2 button and places it within a panel
        slot2Button = new JButton("Slot 2");
        slot2Button.setActionCommand("Slot2");
        slot2Button.addActionListener(this);
        slot2Button.setFocusable(false);
        slot2Button.setAlignmentY(CENTER_ALIGNMENT);
        slot2Button.setAlignmentX(CENTER_ALIGNMENT);
        JPanel slot2Panel = new JPanel();
        slot2Panel.add(slot2Button);

        //Sets up the slot 3 button and places it within a panel
        slot3Button = new JButton("Slot 3");
        slot3Button.setActionCommand("Slot3");
        slot3Button.addActionListener(this);
        slot3Button.setFocusable(false);
        slot3Button.setAlignmentY(CENTER_ALIGNMENT);
        slot3Button.setAlignmentX(CENTER_ALIGNMENT);
        JPanel slot3Panel = new JPanel();
        slot3Panel.add(slot3Button);

        //Sets up the load card and adds the three slot panels
        JPanel loadCard = new JPanel();
        loadCard.setLayout(new GridLayout(3, 1));
        loadCard.add(slot1Panel);
        loadCard.add(slot2Panel);
        loadCard.add(slot3Panel);
        loadCard.setBorder(new EmptyBorder(20, 10, 10, 5));

        //Sets up the card panel and adds the new and load cards
        cards = new JPanel(new CardLayout());
        cards.add(newCard, "NEW GAME");
        cards.add(loadCard, "LOAD GAME");

        //Adds the control panel and card panel to the content pane
        getContentPane().add(controlPanel, BorderLayout.PAGE_START);
        getContentPane().add(cards, BorderLayout.CENTER);

        //Makes the frame visible
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("New"))
        {
            ((CardLayout)(cards.getLayout())).show(cards, "NEW GAME");
        }
        else if (command.equals("Load"))
        {
            ((CardLayout)(cards.getLayout())).show(cards, "LOAD GAME");
        }
        else if (command.equals("Play"))
        {
            int[] dim = getDimensions();

            if (dim != null)
            {
                new Game(dim[0], dim[1], this);
                setVisible(false);
            }
            else
            {
                new GameAlert("Board dimensions: 2 - 12.");
            }
        }
        else if (command.startsWith("Slot"))
        {
            char slot = command.charAt(4);
            File file = new File(".slot" + slot + ".sav");

            if (file.exists())
            {
                new Game(slot - '0', this);
                setVisible(false);
            }
            else
            {
                new GameAlert("Slot " + slot + " is empty.");
            }
        }
    }

    private int[] getDimensions()
    {
        String rowText = rowInput.getText();
        String colText = colInput.getText();

        int rows = 4;
        int cols = 4;

        try
        {
            rows = Integer.parseInt(rowText);
            cols = Integer.parseInt(colText);

            if (rows < 2 || rows > 12 || cols < 2 || cols > 12)
            {
                throw new NumberFormatException();
            }
        }
        catch (NumberFormatException e)
        {
            return null;
        }

        return new int[] {rows, cols};
    }
}