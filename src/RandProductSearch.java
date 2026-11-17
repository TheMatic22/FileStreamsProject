import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandProductSearch extends JFrame {

    private static final String FILE_NAME = "ProductTestData.bin";

    private final JTextField searchField = new JTextField(20);
    private final JButton searchButton   = new JButton("Search");
    private final JButton quitButton     = new JButton("Quit");
    private final JTextArea resultsArea  = new JTextArea(15, 80);

    public RandProductSearch() {
        setTitle("Random Product Search");
        setLayout(new BorderLayout());

        add(buildSearchPanel(), BorderLayout.NORTH);
        add(new JScrollPane(resultsArea), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        resultsArea.setEditable(false);
        resultsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        searchButton.addActionListener(e -> onSearch());
        searchField.addActionListener(e -> onSearch());   // Enter key also searches
        quitButton.addActionListener(e -> System.exit(0));

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Partial product name:"));
        panel.add(searchField);
        panel.add(searchButton);
        return panel;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel();
        panel.add(quitButton);
        return panel;
    }

    private void onSearch() {
        String target = searchField.getText().trim();
        if (target.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter part of a product name.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            searchField.requestFocus();
            return;
        }

        resultsArea.setText("");
        int matches = 0;

        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "r")) {
            resultsArea.append(String.format("%-8s%-37s%-77s%s%n",
                    "ID", "Name", "Description", "Cost"));
            resultsArea.append("=".repeat(130) + "\n");

            while (file.getFilePointer() < file.length()) {
                Product p = Product.readFrom(file);

                if (p.getName().toLowerCase().contains(target.toLowerCase())) {
                    resultsArea.append(String.format("%-8s%-37s%-77s%.2f%n",
                            p.getID(), p.getName(), p.getDescription(), p.getCost()));
                    matches++;
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not read " + FILE_NAME + ": " + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (matches == 0) {
            resultsArea.setText("No products found matching \"" + target + "\".");
        } else {
            resultsArea.append("\n" + matches + " product(s) found.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RandProductSearch().setVisible(true));
    }
}