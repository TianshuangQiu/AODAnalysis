/**
 * This class feeds data to main
 */
package satelliteaod;

import java.io.IOException;
import java.util.ArrayList;
import ucar.ma2.Array;
import ucar.nc2.*;

/**
 * @author eqiu
 * @deprecated: the logic of the code is now changed. I'm just too scared to
 * actually delete it
 */
@Deprecated
public class Feeder {

    static NetcdfFile[] myFile;
    static String[] myFileLocation;
    static ArrayList<Double> latDump = new ArrayList<>();
    static ArrayList<Double> lonDump = new ArrayList<>();
    static ArrayList<Double> aodDump = new ArrayList<>();

    public static ArrayList<Double> convertArray(Array arr) {
        ArrayList<Double> buffer = new ArrayList<>();
        String toBeProcessed = arr.toString();

        while (toBeProcessed.contains(" ")) {
//          System.out.println(toBeProcessed + toBeProcessed.indexOf(" "));
            buffer.add(Double.parseDouble(toBeProcessed.substring(0, toBeProcessed.indexOf(" "))));
            toBeProcessed = toBeProcessed.substring(toBeProcessed.indexOf(" ") + 1);
        }
        return buffer;
    }

    public static void init() {
        myFile = Reader.readMyFile();
        String varName[] = {"lat", "lon", "AODANA"};
        for (NetcdfFile myFile : myFile) {
            for (int j = 0; j < 3; j++) {
                if (myFile == null) {
                    continue;
                }
                Variable v = myFile.findVariable(varName[j]);
                Array data = null;
                try {
                    data = v.read();
                } catch (IOException ioe) {
                    System.out.println("trying to read " + varName + ioe);
                }
                if (data != null) {
                    switch (j) {
                        case 0:
                            latDump.addAll(convertArray(data));
                            break;
                        case 1:
                            lonDump.addAll(convertArray(data));
                            break;
                        case 2:
                            ArrayList<Double> holder = convertArray(data);
                            double sum = 0;
                            for (double d : holder) {
                                sum += d;
                            }
                            aodDump.add(sum / holder.size());
                            break;
                    } //                    System.out.println(data);
                }
            }
        }
        double result = 0;
        double x = Integer.MAX_VALUE;
        for (double d : lonDump) {

            System.out.println(d);
            if (x > d) {
                x = d;
            }
            result = x;
        }
        System.out.println("RESULT " + result);
    }

    public static ArrayList<Double> getAOD() {
        return aodDump;
    }
}
