import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("CallToPrintStackTrace")
public class CSVWriter {

    private String fileName;


    public void createFile(String fileName, boolean replace) {
        this.fileName = fileName;
        File myObj = new File(fileName);
        boolean exists = myObj.exists();

        if (replace || !exists) {
            if (exists) {
                //System.out.println("File " + myObj.getName() + " already exists. Replacing.");
            } else {
                //System.out.println("File " + myObj.getName() + " created.");
            }
            write("Method, Avg WT, Avg TAT, Util %, Throughput ", false, true);
        } else {
            //System.out.println("File " + myObj.getName() + " already exists.");
        }
    }


    public void write(String input, boolean append, boolean inLine) {
        try {
            FileWriter fw = new FileWriter(fileName, append);
            if (inLine) fw.write(input);
            else fw.write(System.lineSeparator() + input);
            fw.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void writeNums(double[] input, boolean append, boolean inLine) {
        try {
            FileWriter fw = new FileWriter(fileName, append);
            if (!inLine) fw.write(System.lineSeparator());
            for (double x : input) {
                fw.write(x + ",");
            }
            fw.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public String getAbsolutePath(String fileName) {
        File myObj = new File(fileName);
        return "file://" + myObj.getAbsolutePath();
    }

}