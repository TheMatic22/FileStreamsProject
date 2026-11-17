import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.RandomAccessFile;
public class RandProductMaker extends JFrame {

    private final JTextField idField    = new JTextField(10);
    private final JTextField nameField  = new JTextField(35);
    private final JTextField descField  = new JTextField(35);
    private final JTextField costField  = new JTextField(10);
    private final JTextField countField = new JTextField("0", 5);

    private final JButton addButton  = new JButton("Add");
    private final JButton quitButton = new JButton("Quit");

    private static final String FILE_NAME = "ProductTestData.bin";
    private int recordCount = 0;

    public RandProductMaker() {
        setTitle("Random Product Maker");
        setLayout(new BorderLayout());

        add(buildFormPanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        addButton.addActionListener(e -> onAdd());
        quitButton.addActionListener(e -> System.exit(0));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }
    private void clearFields() {
        idField.setText("");
        nameField.setText("");
        descField.setText("");
        costField.setText("");
        idField.requestFocus();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        countField.setEditable(false);

        panel.add(new JLabel("Product ID (6 chars):"));
        panel.add(idField);
        panel.add(new JLabel("Name (max 35):"));
        panel.add(nameField);
        panel.add(new JLabel("Description (max 75):"));
        panel.add(descField);
        panel.add(new JLabel("Cost:"));
        panel.add(costField);
        panel.add(new JLabel("Record count:"));
        panel.add(countField);

        return panel;
    }

    private JPanel buildButtonPanel() {
        JPanel panel = new JPanel();
        panel.add(addButton);
        panel.add(quitButton);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RandProductMaker().setVisible(true));
    }
    private void onAdd() {
        Product p = validateInput();
        if (p == null) {
            return;
        }

        try (RandomAccessFile file = new RandomAccessFile(FILE_NAME, "rw")) {
            file.seek(file.length());     // jump to the end so we append
            p.writeTo(file);              // Piece 4: writes exactly 240 bytes
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not write to file: " + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        recordCount++;
        countField.setText(String.valueOf(recordCount));
        clearFields();
    }

    private Product validateInput() {
        String id       = idField.getText().trim();
        String name     = nameField.getText().trim();
        String desc     = descField.getText().trim();
        String costText = costField.getText().trim();

        if (id.isEmpty() || name.isEmpty() || desc.isEmpty() || costText.isEmpty()) {
            showError("Please fill in all fields.", idField);
            return null;
        }
        if (id.length() > Product.ID_LEN) {
            showError("ID can be at most " + Product.ID_LEN + " characters.", idField);
            return null;
        }
        if (name.length() > Product.NAME_LEN) {
            showError("Name can be at most " + Product.NAME_LEN + " characters.", nameField);
            return null;
        }
        if (desc.length() > Product.DESC_LEN) {
            showError("Description can be at most " + Product.DESC_LEN + " characters.", descField);
            return null;
        }

        double cost;
        try {
            cost = Double.parseDouble(costText);
        } catch (NumberFormatException ex) {
            showError("Cost must be a number.", costField);
            return null;
        }
        if (!(cost >= 0 && cost <= 100000)) {
            showError("Cost must be between 0 and 100000.", costField);
            return null;
        }

        return new Product(name, desc, id, cost);
    }

    private void showError(String message, JTextField fieldToFocus) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.ERROR_MESSAGE);
        fieldToFocus.requestFocus();
    }
}