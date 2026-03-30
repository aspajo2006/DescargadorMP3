package descargadormp3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class MP3Downloader extends JFrame {

    private JTextField urlField;
    private JProgressBar progressBar;
    private JLabel statusLabel;

    public MP3Downloader() {
        // Configuración de la ventana
        setTitle("Descargador MP3 Profesional");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Diseño del Panel
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("YouTube a MP3", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(title);

        panel.add(new JLabel("Pega el enlace de YouTube aquí:"));

        urlField = new JTextField();
        panel.add(urlField);

        JButton downloadBtn = new JButton("DESCARGAR AHORA");
        downloadBtn.setBackground(new Color(200, 0, 0));
        downloadBtn.setForeground(Color.WHITE);
        downloadBtn.setFont(new Font("Arial", Font.BOLD, 14));
        downloadBtn.addActionListener(this::startDownload);
        panel.add(downloadBtn);

        progressBar = new JProgressBar();
        panel.add(progressBar);

        statusLabel = new JLabel("Listo para descargar", SwingConstants.CENTER);
        panel.add(statusLabel);

        add(panel);
    }

    private void startDownload(ActionEvent e) {
        String url = urlField.getText().trim();

        if (url.isEmpty()) {
            statusLabel.setText("⚠️ Por favor, pon un enlace.");
            return;
        }

        // Ejecutar en un hilo separado para que la ventana no se congele
        new Thread(() -> downloadProcess(url)).start();
    }

    private void downloadProcess(String url) {
        try {
            // 1. Crear carpeta de salida
            File musicFolder = new File("Musicas_Descargadas");
            if (!musicFolder.exists()) musicFolder.mkdirs();

            // 2. Detectar la ruta actual (donde está tu .exe)
            String currentDir = System.getProperty("user.dir");
            String ytDlpPath = currentDir + File.separator + "yt-dlp.exe";
            
            // Verificar si el motor existe
            File checker = new File(ytDlpPath);
            if(!checker.exists()){
                SwingUtilities.invokeLater(() -> statusLabel.setText("❌ Error: No se encuentra yt-dlp.exe"));
                return;
            }

            SwingUtilities.invokeLater(() -> {
                progressBar.setIndeterminate(true);
                statusLabel.setText("Descargando y convirtiendo...");
            });

            // 3. El Comando Maestro
            ProcessBuilder builder = new ProcessBuilder(
                ytDlpPath,
                "--ffmpeg-location", currentDir, // FFmpeg debe estar en la misma carpeta
                "-x",                          // Extraer audio
                "--audio-format", "mp3",       // Formato final
                "--audio-quality", "0",        // Máxima calidad
                "-o", musicFolder.getAbsolutePath() + "/%(title)s.%(ext)s",
                url
            );

            builder.redirectErrorStream(true);
            Process process = builder.start();

            // Leer salida para evitar que el proceso se bloquee
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (reader.readLine() != null) { 
                // Aquí podrías procesar el porcentaje si quisieras
            }

            int exitCode = process.waitFor();

            SwingUtilities.invokeLater(() -> {
                progressBar.setIndeterminate(false);
                if (exitCode == 0) {
                    progressBar.setValue(100);
                    statusLabel.setText("✅ ¡Descarga terminada! Revisa la carpeta Musica_Descargada");
                    urlField.setText("");
                } else {
                    statusLabel.setText("❌ Error en el proceso (Código: " + exitCode + ")");
                }
            });

        } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> {
                statusLabel.setText("Error fatal: " + ex.getMessage());
                progressBar.setIndeterminate(false);
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MP3Downloader().setVisible(true);
        });
    }
}