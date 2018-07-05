import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Toolbox {

    public static void writeContoursToFile(ArrayList<Double> xData, ArrayList<Double> yData, ArrayList<Double> zData, String filename){

        try{
            File file = new File(filename+".txt");
            if(!file.exists()) file.createNewFile();

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            int n = xData.size();

            for(int i = 0; i < n; i++){
                for(int j = i*n; j < (i+1)*n; j++){
                    String output = String.valueOf(xData.get(i)) + " " + String.valueOf(yData.get(j)) +" " + String.valueOf(zData.get(j));
                    bw.write(output);
                    bw.newLine();
                }
                bw.newLine();
            }
            bw.close();
        }catch (IOException e){}
    }

    public static double[][] averagedResults(int[][][] inputData){

        int nReps = inputData.length;
        int nTimes = inputData[0].length;
        int L = inputData[0][0].length;

        double[][] averagedResults = new double[nTimes][L];

        for(int t = 0; t < nTimes; t++){

            for(int l = 0; l < L; l++){

                double runningTotal = 0.;

                for(int r = 0; r < nReps; r++){
                    runningTotal += (double)inputData[r][t][l];
                }
                averagedResults[t][l] = runningTotal/(double)nReps;
            }
        }
        return averagedResults;
    }

    public static double[][] averagedResults(double[][][] inputData){

        int nReps = inputData.length;
        int nTimes = inputData[0].length;
        int L = inputData[0][0].length;

        double[][] averagedResults = new double[nTimes][L];

        for(int t = 0; t < nTimes; t++){

            for(int l = 0; l < L; l++){

                double runningTotal = 0.;

                for(int r = 0; r < nReps; r++){
                    runningTotal += inputData[r][t][l];
                }
                averagedResults[t][l] = runningTotal/(double)nReps;
            }
        }
        return averagedResults;
    }

    public static void printAveragedResultsToFile(String filename, double[][] inputData){

        try {
            File file = new File(filename+".txt");
            if(!file.exists()) file.createNewFile();

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);

            int nTimes = inputData.length;
            int L = inputData[0].length;

            for(int l = 0; l < L; l++){
                String output = String.valueOf(l)+" ";
                for(int t = 0; t < nTimes; t++){
                    output += String.valueOf(inputData[t][l]+ " ");
                }
                bw.write(output);
                bw.newLine();
            }
            bw.close();
        }catch (IOException e){}
    }
}
