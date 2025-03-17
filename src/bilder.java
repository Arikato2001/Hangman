import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Image;

public class bilder {
    private JButton prevBtn;
    private JPanel mainPanel;
    private JButton nextBtn;
    private JLabel picLabel;

    private int picCounter = 1;

    public bilder() {
        prevBtn.addActionListener(new ImageChangeBtnClick(-1));
        nextBtn.addActionListener(new ImageChangeBtnClick(1));

        ImageIcon new_picture=new ImageIcon("hangman/hangman"+picCounter+".png");
        Image image = new_picture.getImage(); // transform it
        Image newimg = image.getScaledInstance(250, 250,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        new_picture = new ImageIcon(newimg);  // transform it back
        picLabel.setIcon(new_picture);
    }

    private class ImageChangeBtnClick implements ActionListener {
        private int direction;

        public ImageChangeBtnClick(int val) {
            this.direction = val;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            picCounter += direction;

            if (picCounter < 1) picCounter = 10;
            else if (picCounter > 10) picCounter = 1;

            ImageIcon new_picture=new ImageIcon("hangman/hangman"+picCounter+".png");
            Image image = new_picture.getImage(); // transform it
            Image newimg = image.getScaledInstance(250, 250,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
            new_picture = new ImageIcon(newimg);  // transform it back
            picLabel.setIcon(new_picture);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pictures");
        frame.setContentPane(new bilder().mainPanel);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
