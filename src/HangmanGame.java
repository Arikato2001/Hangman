import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class HangmanGame {
    // Liste der Wörter für das Spiel
    private List<String> wordList = new ArrayList<>();
    // Das aktuell ausgewählte Wort zum Raten
    private String selectedWord;
    // Menge der bereits geratenen Buchstaben
    private Set<Character> guessedLetters = new HashSet<>();
    // Anzahl der Fehler (Fehlversuche)
    private int mistakes = 1;
    // Maximale Fehleranzahl
    private int maxMistakes = 9;
    // Flag, ob die Historie der geratenen Buchstaben angezeigt wird
    private boolean showHistory = true;

    // GUI-Komponenten
    private JFrame frame;
    private JLabel wordLabel, picLabel, guessedLettersLabel;
    private JTextField inputField;
    private JButton guessButton;

    // Konstruktor, der das Spiel initialisiert
    public HangmanGame() {
        loadWordsFromFile("words.txt"); // Wörter aus einer Datei laden
        selectedWord = wordList.get(new Random().nextInt(wordList.size())); // Zufälliges Wort auswählen
        initUI(); // Benutzeroberfläche initialisieren
    }

    // GUI initialisieren
    private void initUI() {
        frame = new JFrame("Hangman");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Menüleiste erstellen
        JMenuBar menuBar = new JMenuBar();
        JMenu gameMenu = new JMenu("Einstellungen");
        JMenuItem setTries = new JMenuItem("Versuche setzen");
        JCheckBoxMenuItem historyToggle = new JCheckBoxMenuItem("Buchstaben-History anzeigen", showHistory);
        JMenuItem loadWords = new JMenuItem("Wörter neu laden");

        // Aktionen für Menüeinträge definieren
        setTries.addActionListener(e -> setMaxMistakes());
        historyToggle.addActionListener(e -> toggleHistory());
        loadWords.addActionListener(e -> reloadWords());

        // Menüeinträge hinzufügen
        gameMenu.add(setTries);
        gameMenu.add(historyToggle);
        gameMenu.add(loadWords);
        menuBar.add(gameMenu);
        frame.setJMenuBar(menuBar);

        // Label für das Wort
        wordLabel = new JLabel(getMaskedWord(), SwingConstants.CENTER);
        wordLabel.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(wordLabel, BorderLayout.NORTH);

        // Bild für den Hangman
        picLabel = new JLabel();
        updateImage();
        frame.add(picLabel, BorderLayout.CENTER);

        // Panel für die Eingabe und die geratenen Buchstaben
        JPanel bottomPanel = new JPanel(new BorderLayout());
        guessedLettersLabel = new JLabel("Geratene Buchstaben: ", SwingConstants.CENTER);
        guessedLettersLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        bottomPanel.add(guessedLettersLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel();
        inputField = new JTextField(5); // Textfeld für die Buchstabeneingabe
        guessButton = new JButton("Raten");
        guessButton.addActionListener(new GuessHandler()); // Button zum Raten
        inputField.addKeyListener(new KeyAdapter() { // Eingabefeld für Enter-Taste
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    guessButton.doClick(); // Button klicken bei Enter
                }
            }
        });
        inputPanel.add(inputField);
        inputPanel.add(guessButton);
        bottomPanel.add(inputPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        // Fenstergröße und Sichtbarkeit
        frame.setSize(400, 500);
        frame.setVisible(true);
    }

    // Maximale Fehleranzahl setzen
    private void setMaxMistakes() {
        String input = JOptionPane.showInputDialog(frame, "Maximale Versuche setzen:", maxMistakes);
        if (input != null && input.matches("\\d+")) {
            maxMistakes = Integer.parseInt(input);
        }
    }

    // Toggle für die Anzeige der geratenen Buchstaben-Historie
    private void toggleHistory() {
        showHistory = !showHistory;
        updateGuessedLetters();
    }

    // Wörter neu laden
    private void reloadWords() {
        loadWordsFromFile("words.txt");
        restartGame();
    }

    // Wörter aus einer Datei laden
    private void loadWordsFromFile(String filename) {
        wordList.clear();
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextLine()) {
                wordList.add(scanner.nextLine().toUpperCase()); // Wörter in Großbuchstaben speichern
            }
        } catch (FileNotFoundException e) {
            // Fallback, falls die Datei nicht gefunden wird
            wordList.addAll(Arrays.asList("JAVA", "SWING", "COMPUTER", "PROGRAMMING", "KEYBOARD"));
        }
    }

    // Maskiertes Wort für die Anzeige erstellen
    private String getMaskedWord() {
        StringBuilder masked = new StringBuilder();
        for (char c : selectedWord.toCharArray()) {
            if (guessedLetters.contains(c)) {
                masked.append(c).append(" ");
            } else {
                masked.append("_ ");
            }
        }
        return masked.toString();
    }

    // Bild für den Hangman basierend auf der Fehleranzahl aktualisieren
    private void updateImage() {
        String imagePath = "Hangmanbilder/hangman" + mistakes + ".png";
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
            picLabel.setIcon(new ImageIcon(img));
        }
    }

    // Die Anzeige der geratenen Buchstaben aktualisieren
    private void updateGuessedLetters() {
        if (showHistory) {
            StringBuilder guessedLettersText = new StringBuilder("Geratene Buchstaben: ");
            for (char c : guessedLetters) {
                guessedLettersText.append(c).append(" ");
            }
            guessedLettersLabel.setText(guessedLettersText.toString());
        } else {
            guessedLettersLabel.setText("");
        }
    }

    // Handler für das Raten eines Buchstabens
    private class GuessHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String input = inputField.getText().toUpperCase();
            if (input.length() == 1) {
                char guess = input.charAt(0);
                if (!guessedLetters.contains(guess)) {
                    guessedLetters.add(guess);
                    if (!selectedWord.contains(String.valueOf(guess))) {
                        mistakes++; // Fehler nur erhöhen, wenn der Buchstabe nicht im Wort ist
                    }
                    wordLabel.setText(getMaskedWord());
                    updateImage();
                    updateGuessedLetters();
                    checkGameStatus(); // Status nach der Fehlererhöhung prüfen
                }
            }
            inputField.setText(""); // Eingabefeld nach dem Raten leeren
        }
    }


    // Spielstatus prüfen (ob das Spiel zu Ende ist)
    private void checkGameStatus() {
        if (mistakes > maxMistakes) { // Fehleranzahl darf maxMistakes überschreiten, nicht gleich sein
            if (JOptionPane.showConfirmDialog(frame, "Game Over! Das Wort war: " + selectedWord + "\nNeues Spiel starten?", "Spiel beendet", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                frame.dispose();
            }
        } else if (!getMaskedWord().contains("_")) { // Wenn alle Buchstaben erraten wurden
            if (JOptionPane.showConfirmDialog(frame, "Glückwunsch! Du hast gewonnen!\nNeues Spiel starten?", "Spiel gewonnen", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                frame.dispose();
            }
        }
    }


    // Spiel neu starten
    private void restartGame() {
        selectedWord = wordList.get(new Random().nextInt(wordList.size())); // Neues Wort wählen
        guessedLetters.clear(); // Alle geratenen Buchstaben zurücksetzen
        mistakes = 1; // Fehler zurücksetzen auf 1 (nicht 0, damit die Fehlerzählung korrekt funktioniert)
        wordLabel.setText(getMaskedWord()); // Maskiertes Wort anzeigen
        updateImage(); // Hangman-Bild zurücksetzen
        updateGuessedLetters(); // Die Liste der geratenen Buchstaben zurücksetzen
    }


    // Hauptmethode zum Starten des Spiels
    public static void main(String[] args) {
        SwingUtilities.invokeLater(HangmanGame::new); // Das Spiel im Event-Dispatch-Thread starten
    }
}
