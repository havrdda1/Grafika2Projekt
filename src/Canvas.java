import imagedata.*;
import imagedata.Image;
import org.jetbrains.annotations.NotNull;
import rasterops.LineRasterizer;
import rasterops.LineRasterizerDDA;
import rasterops.Turtle;
import util.LRules;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * @author Havrda Daniel
 * @version 2018
 * author of transforms library: Jan Vanek
 */

/* TODO
 * Predelat GUI, oddelit od canvasu
 * Pridat pomale vykreslovani step by step
 *                 * Interface ObjectData, trida LineData, Thread
 * Dopsat dokumentaci ke tride LRules a Turtle
 */



public class Canvas implements ActionListener {

    private final JPanel panel;
    private final @NotNull Presenter<Color, Graphics> presenter;
    private final @NotNull LineRasterizer<Color> lineRasterizer;
    private Turtle<Color> turtle;
    private LRules lindenmayer;
    private Image image;
    private JComboBox<String> examplesBox;
    private JTextField startTextField;
    private JTextArea rulesTextArea;
    private JTextField iterationsTextField;
    private JTextField turnAngleTextField;
    private JTextField lineTextField;
    private ButtonGroup startingPointButtonGroup;
    private JRadioButton cornerButton;
    private JRadioButton centerButton;
    private JRadioButton bottomButton;
    private JCheckBox verticalCheckBox;
    private JSlider sliderColorR, sliderColorG, sliderColorB;
    private String[] examples = {"Fractal plant", "Fractal bush", "Fractal bush 2", "Tree", "Sierpinski", "Koch curve", "Dragon curve"};
    private Color value;

    private Canvas(final int width, final int height) {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        JPanel menuPanel = new JPanel(new GridBagLayout());
        menuPanel.setBorder(new EmptyBorder(new Insets(0, 0, 0, 5)));
        GridBagConstraints constraints = new GridBagConstraints();
        JLabel titleLabel = new JLabel("Settings");
        JLabel examplesLabel = new JLabel("Examples");
        JLabel startLabel = new JLabel("Start");
        JLabel rulesLabel = new JLabel("Rules");
        JLabel iterationsLabel = new JLabel("Number of iterations");
        JLabel turnAngleLabel = new JLabel("Turn angle");
        JLabel lineLabel = new JLabel("Line length");
        JLabel startingPointLabel = new JLabel("Starting point");
        JLabel colSliderLabel = new JLabel("Color settings");
        examplesBox = new JComboBox<>(examples);
        startTextField = new JTextField();
        rulesTextArea = new JTextArea(4, 10);
        iterationsTextField = new JTextField();
        turnAngleTextField = new JTextField();
        lineTextField = new JTextField();
        cornerButton = new JRadioButton("corner");
        centerButton = new JRadioButton("center");
        bottomButton = new JRadioButton("bottom");
        startingPointButtonGroup = new ButtonGroup();
        verticalCheckBox = new JCheckBox("Vertical");
        JButton drawButton = new JButton("Draw");
        sliderColorR = new JSlider(JSlider.HORIZONTAL, 0, 255, 150);
        sliderColorG = new JSlider(JSlider.HORIZONTAL, 0, 255, 150);
        sliderColorB = new JSlider(JSlider.HORIZONTAL, 0, 255, 150);
        sliderColorR.addChangeListener(e -> value = (new Color(sliderColorR.getValue(), sliderColorG.getValue(), sliderColorB.getValue())));
        sliderColorG.addChangeListener(e -> value = (new Color(sliderColorR.getValue(), sliderColorG.getValue(), sliderColorB.getValue())));
        sliderColorB.addChangeListener(e -> value = (new Color(sliderColorR.getValue(), sliderColorG.getValue(), sliderColorB.getValue())));
        value = new Color(sliderColorR.getValue(), sliderColorG.getValue(), sliderColorB.getValue());
        examplesBox.addActionListener(this);
        examplesBox.setSelectedIndex(0);

        startingPointButtonGroup.add(cornerButton);
        startingPointButtonGroup.add(centerButton);
        startingPointButtonGroup.add(bottomButton);


        constraints.gridx = 0;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.anchor = GridBagConstraints.NORTHWEST;

        constraints.gridy = 0;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        menuPanel.add(titleLabel, constraints);

        constraints.gridy = 0;
        menuPanel.add(new JSeparator(), constraints);

        constraints.gridy = 2;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        menuPanel.add(examplesLabel, constraints);

        constraints.gridy = 3;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        menuPanel.add(new JScrollPane(examplesBox), constraints);

        constraints.gridy = 4;
        menuPanel.add(new JSeparator(), constraints);

        constraints.gridy = 5;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        menuPanel.add(startLabel, constraints);

        constraints.gridy = 6;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        menuPanel.add(startTextField, constraints);

        constraints.gridy = 7;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        menuPanel.add(rulesLabel, constraints);

        constraints.gridy = 8;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        rulesTextArea.setLineWrap(true);
        menuPanel.add(new JScrollPane(rulesTextArea), constraints);

        constraints.gridy = 9;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        menuPanel.add(iterationsLabel, constraints);

        constraints.gridy = 10;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        menuPanel.add(iterationsTextField, constraints);

        constraints.gridy = 11;
        menuPanel.add(new JSeparator(), constraints);

        constraints.gridy = 12;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        menuPanel.add(turnAngleLabel, constraints);

        constraints.gridy = 13;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        menuPanel.add(turnAngleTextField, constraints);

        constraints.gridy = 14;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        menuPanel.add(lineLabel, constraints);

        constraints.gridy = 15;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        menuPanel.add(lineTextField, constraints);

        constraints.gridy = 16;
        menuPanel.add(new JSeparator(), constraints);

        constraints.gridy = 17;
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.NONE;
        menuPanel.add(startingPointLabel, constraints);

        constraints.gridy = 18;
        menuPanel.add(cornerButton, constraints);

        constraints.gridx = 1;
        menuPanel.add(centerButton, constraints);

        constraints.gridy = 19;
        constraints.gridx = 0;
        menuPanel.add(bottomButton, constraints);

        constraints.gridy = 20;
        constraints.gridwidth = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        menuPanel.add(new JSeparator(), constraints);

        constraints.gridy = 21;
        menuPanel.add(verticalCheckBox, constraints);

        constraints.gridy = 22;
        menuPanel.add(new JSeparator(), constraints);

        constraints.gridy = 23;
        drawButton.addActionListener(this);
        menuPanel.add(drawButton, constraints);

        constraints.gridy = 24;
        menuPanel.add(colSliderLabel, constraints);
        constraints.gridy = 25;
        menuPanel.add(sliderColorR, constraints);
        constraints.gridy = 26;
        menuPanel.add(sliderColorG, constraints);
        constraints.gridy = 27;
        menuPanel.add(sliderColorB, constraints);

        mainPanel.add(menuPanel);

        JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle("UHK FIM PGRF 1 " + this.getClass()
                                               .getName());
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        image = new ImageAWT<>(
                img,

                Color::getRGB,

                Color::new
        );
        presenter = new PresenterAWT<>();

        lineRasterizer = new LineRasterizerDDA<>();
        turtle = new Turtle<>(lineRasterizer, image);
        lindenmayer = new LRules();

        panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };
        clear();
        panel.repaint();
        panel.setPreferredSize(new Dimension(width, height));

        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        frame.add(menuPanel, BorderLayout.EAST);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Canvas(1024, 768)::start);
    }

    private void calculateLSystem(final String start, final String[] rules, final int iterations, final int angle, final int lineLength, final int startPosition, final boolean vertical, Color value) {
        String state = start;
        Map<Character, char[]> preparedRules = lindenmayer.prepareRules(rules);
        for (int i = 0; i < iterations; i++) {
            state = lindenmayer.applyRules(preparedRules, state);
        }
        turtle.prepare(startPosition, state, lineLength, angle, vertical, value);
        draw();
        panel.repaint();

    }

    private void clear() {
        image = image.cleared(new Color(0x2f, 0x2f, 0x2f));
    }

    private void present(final Graphics graphics) {
        presenter.present(image, graphics);

    }

    private void draw() {
        clear();
        image = turtle.startTurtle();

    }

    private void start() {
        clear();
        panel.repaint();
    }

    @Override
    public final void actionPerformed(final ActionEvent event) {
        if (event.getActionCommand()
                 .equals("comboBoxChanged")) {
            @SuppressWarnings("unchecked")
            JComboBox<String> examplesBox = (JComboBox<String>) event
                    .getSource();
            @NotNull String example = (String) examplesBox.getSelectedItem();

            switch (example) {
                case "Tree":
                    sliderColorR.setValue(150);
                    sliderColorG.setValue(75);
                    sliderColorB.setValue(0);
                    startTextField.setText("G");
                    rulesTextArea.setText("F:FF\nG:F[+G]-G");
                    iterationsTextField.setText("7");
                    turnAngleTextField.setText("45");
                    lineTextField.setText("1");
                    bottomButton.setSelected(true);
                    verticalCheckBox.setSelected(false);
                    break;
                case "Sierpinski":
                    sliderColorR.setValue(255);
                    sliderColorG.setValue(215);
                    sliderColorB.setValue(0);
                    startTextField.setText("F-G-G");
                    rulesTextArea.setText("F:F-G+F+G-F\nG:GG");
                    iterationsTextField.setText("6");
                    turnAngleTextField.setText("120");
                    lineTextField.setText("1");
                    centerButton.setSelected(true);
                    verticalCheckBox.setSelected(true);
                    break;
                case "Fractal plant":
                    sliderColorR.setValue(0);
                    sliderColorG.setValue(255);
                    sliderColorB.setValue(0);
                    startTextField.setText("X");
                    rulesTextArea.setText("X:F+[[X]-X]-F[-FX]+X\nF:FF");
                    iterationsTextField.setText("6");
                    turnAngleTextField.setText("25");
                    lineTextField.setText("1");
                    bottomButton.setSelected(true);
                    verticalCheckBox.setSelected(false);
                    break;

                case "Fractal bush":
                    sliderColorR.setValue(0);
                    sliderColorG.setValue(255);
                    sliderColorB.setValue(0);
                    startTextField.setText("Y");
                    rulesTextArea.setText("X:X[-FFF][+FFF]FX\nY:YFX[+Y][-Y]");
                    iterationsTextField.setText("6");
                    turnAngleTextField.setText("25");
                    lineTextField.setText("1");
                    bottomButton.setSelected(true);
                    verticalCheckBox.setSelected(false);
                    break;

                case "Fractal bush 2":
                    sliderColorR.setValue(0);
                    sliderColorG.setValue(255);
                    sliderColorB.setValue(0);
                    startTextField.setText("F");
                    rulesTextArea.setText("F:FF+[+F-F-F]-[-F+F+F]");
                    iterationsTextField.setText("4");
                    turnAngleTextField.setText("22");
                    lineTextField.setText("2");
                    bottomButton.setSelected(true);
                    verticalCheckBox.setSelected(false);
                    break;
                case "Koch curve":
                    sliderColorR.setValue(25);
                    sliderColorG.setValue(25);
                    sliderColorB.setValue(255);
                    startTextField.setText("F");
                    rulesTextArea.setText("F:F+F-F-F+F");
                    iterationsTextField.setText("4");
                    turnAngleTextField.setText("90");
                    lineTextField.setText("1");
                    centerButton.setSelected(true);
                    verticalCheckBox.setSelected(true);
                    break;
                case "Dragon curve":
                    sliderColorR.setValue(25);
                    sliderColorG.setValue(150);
                    sliderColorB.setValue(255);
                    startTextField.setText("FX");
                    rulesTextArea.setText("X:X+YF+\nY:-FX-Y");
                    iterationsTextField.setText("12");
                    turnAngleTextField.setText("90");
                    lineTextField.setText("1");
                    centerButton.setSelected(true);
                    verticalCheckBox.setSelected(false);
                    break;
                default:
                    break;
            }

        } else {
            String start = startTextField.getText()
                                         .replaceAll("\\s", "");
            String[] rules = rulesTextArea.getText()
                                          .split("\\n");
            int iterations = Integer
                    .parseInt(iterationsTextField.getText());
            int angle = Integer.parseInt(turnAngleTextField.getText());
            int lineLength = Integer.parseInt(lineTextField.getText());

            int startPosition = 1;
            if (!centerButton.isSelected())
                startPosition = cornerButton.isSelected() ? 2 : 3;

            calculateLSystem(start, rules, iterations, angle,
                    lineLength, startPosition,
                    verticalCheckBox.isSelected(), value);
        }
    }
}
