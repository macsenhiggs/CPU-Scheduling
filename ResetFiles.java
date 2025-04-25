public class ResetFiles {
    public static void main(String[] args) {
        CSVWriter writer = new CSVWriter();
        writer.createFile("smallTest.csv", true);
        writer.createFile("largeTest.csv", true);
        writer.createFile("sameBTAndAT.csv", true);
        writer.createFile("longAndShort.csv", true);
        writer.createFile("shrinking.csv", true);
        System.out.println("Reset all Files.");
    }
}
