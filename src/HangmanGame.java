import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class HangmanGame {
    // Eine Liste mit möglichen Wörtern für das Spiel
    private static final String[] WORDS = {"JAVA", "SWING", "COMPUTER", "PROGRAMMING", "KEYBOARD"};
    private String selectedWord; // Das aktuell zu ratende Wort
    private Set<Character> guessedLetters = new HashSet<>(); // Set zur Speicherung bereits geratener Buchstaben
    private int mistakes = 1; // Anzahl der Fehler
    private final int maxMistakes = 9; // Maximale Anzahl an Fehlern

    private JFrame frame;
    private JLabel wordLabel;
    private JTextField inputField;
    private JButton guessButton;
    private JLabel picLabel;
    private JLabel guessedLettersLabel; // für die geratenen Buchstaben

    public HangmanGame() {
        // Zufälliges Wort aus der Liste auswählen
        selectedWord = WORDS[new Random().nextInt(WORDS.length)];
        initUI(); // Benutzeroberfläche initialisieren
    }

    private void initUI() {
        frame = new JFrame("Hangman");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Label für das Wort mit Platzhaltern (_) anzeigen
        wordLabel = new JLabel(getMaskedWord(), SwingConstants.CENTER);
        wordLabel.setFont(new Font("Arial", Font.BOLD, 24));
        frame.add(wordLabel, BorderLayout.NORTH);

        // Label für das Bild hinzufügen
        picLabel = new JLabel();
        updateImage();
        frame.add(picLabel, BorderLayout.CENTER);

        // Panel für die Eingabe und geratenen Buchstaben
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());

        // Neues Label für die geratenen Buchstaben
        guessedLettersLabel = new JLabel("Geratene Buchstaben: ", SwingConstants.CENTER);
        guessedLettersLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        bottomPanel.add(guessedLettersLabel, BorderLayout.NORTH);

        // Eingabebereich für Buchstabenraten erstellen
        JPanel inputPanel = new JPanel();
        inputField = new JTextField(5);
        guessButton = new JButton("Guess");
        guessButton.addActionListener(new GuessHandler()); // Event-Listener hinzufügen
        inputPanel.add(inputField);
        inputPanel.add(guessButton);
        bottomPanel.add(inputPanel, BorderLayout.CENTER);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // KeyListener hinzufügen, um die ENTER-Taste zu überwachen
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Wenn die ENTER-Taste gedrückt wird
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    guessButton.doClick(); // Simuliert einen Klick auf den Guess-Button
                }
            }
        });

        frame.setSize(400, 500);
        frame.setVisible(true);
    }

    private String getMaskedWord() {
        // Erstellt eine Darstellung des Wortes mit _ für unerratene Buchstaben
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

    private void updateImage() {
        // Das Bild basierend auf der Fehleranzahl aktualisieren
        String imagePath = "Hangmanbilder/hangman" + mistakes + ".png";
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            ImageIcon icon = new ImageIcon(imageFile.getAbsolutePath());
            Image img = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
            picLabel.setIcon(new ImageIcon(img));
        } else {
            System.out.println("Bild nicht gefunden: " + imageFile.getAbsolutePath());
        }
    }

    private void updateGuessedLetters() {
        // Aktualisiert das Label mit den geratenen Buchstaben
        StringBuilder guessedLettersText = new StringBuilder("Geratene Buchstaben: ");
        for (char c : guessedLetters) {
            guessedLettersText.append(c).append(" ");
        }
        guessedLettersLabel.setText(guessedLettersText.toString());
    }

    private class GuessHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Nimmt die Eingabe des Benutzers entgegen und verarbeitet sie
            String input = inputField.getText().toUpperCase();
            if (input.length() == 1) {
                char guess = input.charAt(0);
                if (!guessedLetters.contains(guess)) {
                    guessedLetters.add(guess);
                    if (!selectedWord.contains(String.valueOf(guess))) {
                        mistakes++; // Erhöht Fehleranzahl, wenn Buchstabe nicht im Wort ist
                    }
                }
                wordLabel.setText(getMaskedWord()); // Wortanzeige aktualisieren
                updateImage(); // Bild aktualisieren
                updateGuessedLetters(); // Die Liste der geratenen Buchstaben aktualisieren
                checkGameStatus(); // Prüfen, ob das Spiel vorbei ist
            }
            inputField.setText(""); // Textfeld nach der Eingabe zurücksetzen
        }
    }

    private void checkGameStatus() {
        // Überprüft, ob das Spiel gewonnen oder verloren wurde
        if (mistakes >= maxMistakes) {
            JOptionPane.showMessageDialog(frame, "Game Over! Das Wort war: " + selectedWord);
            restartGame();
        } else if (!getMaskedWord().contains("_")) {
            updateImage();
            JOptionPane.showMessageDialog(frame, "Glückwunsch! Du hast gewonnen!");
            restartGame();
        }
    }

    private void restartGame() {
        // Setzt das Spiel zurück und startet neu
        selectedWord = WORDS[new Random().nextInt(WORDS.length)];
        guessedLetters.clear();
        mistakes = 1;
        wordLabel.setText(getMaskedWord());
        updateImage(); // Das Bild wird ebenfalls zurückgesetzt
        updateGuessedLetters(); // Die Liste der geratenen Buchstaben wird zurückgesetzt
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HangmanGame::new); // Startet das Spiel in der Event-Dispatch-Thread
    }
}
