import java.io.IOException;
import java.io.RandomAccessFile;
public class Product {


    public static final int NAME_LEN = 35;
    public static final int DESC_LEN = 75;
    public static final int ID_LEN = 6;
    public static final int RECORD_SIZE = (NAME_LEN + DESC_LEN + ID_LEN) * 2 + 8;

    private String name;
    private String description;
    private String ID;      // should never change once assigned
    private double cost;

    public Product(String name, String description, String ID, double cost) {
        this.name = name;
        this.description = description;
        this.ID = ID;
        this.cost = cost;
    }

    private static String pad(String s, int length) {
        if (s == null) {
            s = "";
        }
        if (s.length() > length) {
            return s.substring(0, length);
        }
        return String.format("%-" + length + "s", s);
    }

    public Product(String name, String ID, double cost) {
        this(name, "", ID, cost);
    }

    public Product(Product other) {
        this(other.name, other.description, other.ID, other.cost);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPaddedName() {
        return pad(name, NAME_LEN);
    }

    public String getPaddedDescription() {
        return pad(description, DESC_LEN);
    }

    public String getPaddedID() {
        return pad(ID, ID_LEN);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getID() {
        return ID;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Cost cannot be negative.");
        }
        this.cost = cost;
    }

    public String toCSV() {
        return ID + "," + name + "," + description + "," + cost;
    }

    public String toJSON() {
        return "{"
                + "\"ID\":\"" + ID + "\","
                + "\"name\":\"" + name + "\","
                + "\"description\":\"" + description + "\","
                + "\"cost\":" + cost
                + "}";
    }

    public String toXML() {
        return "<product>"
                + "<ID>" + ID + "</ID>"
                + "<name>" + name + "</name>"
                + "<description>" + description + "</description>"
                + "<cost>" + cost + "</cost>"
                + "</product>";
    }
    public static Product readFrom(RandomAccessFile file) throws IOException {
        String id          = readFixedString(file, ID_LEN);
        String name        = readFixedString(file, NAME_LEN);
        String description = readFixedString(file, DESC_LEN);
        double cost        = file.readDouble();

        return new Product(name, description, id, cost);
    }

    private static String readFixedString(RandomAccessFile file, int length) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(file.readChar());
        }
        return sb.toString().trim();
    }

    public void writeTo(RandomAccessFile file) throws IOException {
        file.writeChars(getPaddedID());
        file.writeChars(getPaddedName());
        file.writeChars(getPaddedDescription());
        file.writeDouble(cost);
    }
    @Override
    public String toString() {
        return "Product{" +
                "ID='" + ID + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", cost=" + cost +
                '}';
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (Double.compare(product.cost, cost) != 0) return false;
        if (!name.equals(product.name)) return false;
        if (!description.equals(product.description)) return false;
        return ID.equals(product.ID);
    }
}