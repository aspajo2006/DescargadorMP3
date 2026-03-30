package descargadormp3;

import javax.swing.SwingUtilities;

public class main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MP3Downloader().setVisible(true);
        });
    }
}