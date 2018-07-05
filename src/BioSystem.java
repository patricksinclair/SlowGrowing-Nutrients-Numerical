import java.util.Random;

public class BioSystem {

    private int L, K, s, s_max;
    private double c, alpha, timeElapsed;

    private Microhabitat[] microhabitats;
    Random rand = new Random();
    private int initialPop = 100;

    public BioSystem(int L, int S, double alpha){

        this.L = L;
        this.s = S;
        this.s_max = S;
        this.alpha = alpha;
        this.microhabitats = new Microhabitat[L];
        this.timeElapsed = 0.;

        for(int i = 0; i < L; i++){
            double c_i = Math.exp(alpha*(double)i) - 1.;
            microhabitats[i] = new Microhabitat(S, c_i);
        }
        microhabitats[0].fillWithWildType(initialPop);
    }

    public BioSystem(int L, int S, double c, String token){

        this.L = L;
        this.s = S;
        this.s_max = S;
        this.alpha = alpha;
        this.microhabitats = new Microhabitat[L];
        this.timeElapsed = 0.;

        for(int i = 0; i < L; i++){
            //double c_i = Math.exp(alpha*(double)i) - 1.;
            microhabitats[i] = new Microhabitat(S, c);
        }
        microhabitats[0].fillWithWildType(initialPop);
    }

    public int getL(){
        return L;
    }
    public double getTimeElapsed(){
        return timeElapsed;
    }

    public int getCurrentPopulation(){
        int runningTotal = 0;
        for(Microhabitat m : microhabitats) {
            runningTotal += m.getN();
        }
        return runningTotal;
    }

    public int[] getSpatialDistributionArray(){
        int[] mh_pops = new int[L];
        for(int i = 0; i < L; i++){
            mh_pops[i] = microhabitats[i].getN();
        }
        return mh_pops;
    }

    public double[] getGrowthRatesArray(){
        double[] mh_gRates = new double[L];
        for(int i = 0; i < L; i++){
            mh_gRates[i] = microhabitats[i].replicationRate();
        }
        return mh_gRates;
    }


    public int getRandMicrohabIndex(int randBacIndex){

        int bacCounter = 0;
        int mh_index = 0;

        forloop:
        for(int i = 0; i < L; i++){
            if(bacCounter+microhabitats[i].getN() <= randBacIndex){
                bacCounter+=microhabitats[i].getN();
                continue forloop;

            }else{
                mh_index = i;
                break forloop;
            }
        }
        return mh_index;
    }


    public void migrate(int microhab_index){
        //handles boundary conditions
        if(microhab_index == 0){
            microhabitats[0].removeABacterium();
            microhabitats[1].addABacterium();

        }else if(microhab_index == L-1){
            microhabitats[L-1].removeABacterium();
            microhabitats[L-2].addABacterium();
            //randomly moves the bacteria forward or backward
        }else{
            if(rand.nextBoolean()){
                microhabitats[microhab_index].removeABacterium();
                microhabitats[microhab_index+1].addABacterium();

            }else{
                microhabitats[microhab_index].removeABacterium();
                microhabitats[microhab_index-1].addABacterium();
            }
        }
    }


    public void death(int mh_index){
        microhabitats[mh_index].removeABacterium();
    }

    public void replicate(int mh_index){

        microhabitats[mh_index].consumeANutrient();
        microhabitats[mh_index].addABacterium();
    }


    public void performAction(){

        int N = getCurrentPopulation();
        int randBacIndex = rand.nextInt(N);
        int mh_index = getRandMicrohabIndex(randBacIndex);
        Microhabitat rand_mh = microhabitats[mh_index];

        double migrate_rate = rand_mh.getB();
        double death_rate = rand_mh.getD();
        double replication_rate = rand_mh.replicationRate();

        double R_max = 1.2;
        double rand_chance = rand.nextDouble()*R_max;

        if(rand_chance <= migrate_rate) migrate(mh_index);
        else if(rand_chance > migrate_rate && rand_chance <= (migrate_rate + death_rate)) death(mh_index);
        else if(rand_chance > (migrate_rate + death_rate) && rand_chance <= (migrate_rate + death_rate + replication_rate)) {
            replicate(mh_index);
        }

        timeElapsed += 1./((double)N*R_max);
    }



    public static void exponentialGradient_spatialAndGRateDistributions(double input_alpha){

        int L = 500, nReps = 20;
        int nTimeMeasurements = 20;

        double duration = 2000., interval = duration/(double)nTimeMeasurements;
        double preciseDuration = duration/5., preciseInterval = preciseDuration/(double)nTimeMeasurements;

        double alpha = input_alpha;
        int S = 500;

        String filename = "simple-slowGrowers-alpha="+String.valueOf(alpha)+"-spatialDistribution-FINAL";
        String filename_gRate = "simple-slowGrowers-alpha="+String.valueOf(alpha)+"-gRateDistribution-FINAL";
        String filename_precise = "simple-slowGrowers-alpha="+String.valueOf(alpha)+"-spatialDistribution_precise-FINAL";
        String filename_gRate_precise = "simple-slowGrowers-alpha="+String.valueOf(alpha)+"-gRateDistribution_precise-FINAL";

        int[][][] allMeasurements = new int[nReps][][];
        double[][][] allGRateMeasurements = new double[nReps][][];

        int[][][] allPreciseMeasurements = new int[nReps][][];
        double[][][] allPreciseGRateMeasurements = new double[nReps][][];

        for(int r = 0; r < nReps; r++){

            boolean alreadyRecorded = false, alreadyPreciselyRecorded = false;

            int[][] popsOverTime = new int[nTimeMeasurements+1][];
            double[][] gRatesOverTime = new double[nTimeMeasurements+1][];
            int timerCounter = 0;

            int[][] precisePopsOverTime = new int[nTimeMeasurements+1][];
            double[][] preciseGRatesOverTime = new double[nTimeMeasurements+1][];
            int preciseTimerCounter = 0;


            BioSystem bs = new BioSystem(L, S, alpha);

            while(bs.timeElapsed <= duration){

                bs.performAction();

                if((bs.getTimeElapsed()%preciseInterval >= 0. && bs.getTimeElapsed()%preciseInterval <= 0.01) && !alreadyPreciselyRecorded &&
                        preciseTimerCounter <= nTimeMeasurements){

                    System.out.println("rep: "+r+"\ttime elapsed: "+String.valueOf(bs.getTimeElapsed())+"\tPRECISE");
                    precisePopsOverTime[preciseTimerCounter] = bs.getSpatialDistributionArray();
                    preciseGRatesOverTime[preciseTimerCounter] = bs.getGrowthRatesArray();

                    alreadyPreciselyRecorded = true;
                    preciseTimerCounter++;
                }
                if(bs.getTimeElapsed()%preciseInterval >= 0.1) alreadyPreciselyRecorded = false;


                if((bs.getTimeElapsed()%interval >= 0. && bs.getTimeElapsed()%interval <= 0.01) && !alreadyRecorded){

                    System.out.println("rep: "+r+"\ttime elapsed: "+String.valueOf(bs.getTimeElapsed()));
                    popsOverTime[timerCounter] = bs.getSpatialDistributionArray();
                    gRatesOverTime[timerCounter] = bs.getGrowthRatesArray();

                    alreadyRecorded = true;
                    timerCounter++;
                }
                if(bs.getTimeElapsed()%interval >= 0.1) alreadyRecorded = false;
            }

            allMeasurements[r] = popsOverTime;
            allGRateMeasurements[r] = gRatesOverTime;
            allPreciseMeasurements[r] = precisePopsOverTime;
            allPreciseGRateMeasurements[r] = preciseGRatesOverTime;
        }

        double[][] averagedPopDistributions = Toolbox.averagedResults(allMeasurements);
        double[][] averagedGRateDistributions = Toolbox.averagedResults(allGRateMeasurements);
        double[][] averagedPrecisePopDistributions = Toolbox.averagedResults(allPreciseMeasurements);
        double[][] averagedPreciseGRateDistributions = Toolbox.averagedResults(allPreciseGRateMeasurements);

        Toolbox.printAveragedResultsToFile(filename, averagedPopDistributions);
        Toolbox.printAveragedResultsToFile(filename_gRate, averagedGRateDistributions);
        Toolbox.printAveragedResultsToFile(filename_precise, averagedPrecisePopDistributions);
        Toolbox.printAveragedResultsToFile(filename_gRate_precise, averagedPreciseGRateDistributions);
    }


    public static void uniformGradient_spatialAndGRateDistributions(double input_c){

        int L = 500, nReps = 20;
        int nTimeMeasurements = 20;

        double duration = 2000., interval = duration/(double)nTimeMeasurements;
        double preciseDuration = duration/5., preciseInterval = preciseDuration/(double)nTimeMeasurements;

        double c = input_c;
        int S = 500;

        String filename = "simple-fastGrowers-c="+String.valueOf(c)+"-spatialDistribution-FINAL";
        String filename_gRate = "simple-fastGrowers-c="+String.valueOf(c)+"-gRateDistribution-FINAL";
        String filename_precise = "simple-fastGrowers-c="+String.valueOf(c)+"-spatialDistribution_precise-FINAL";
        String filename_gRate_precise = "simple-fastGrowers-c="+String.valueOf(c)+"-gRateDistribution_precise-FINAL";

        int[][][] allMeasurements = new int[nReps][][];
        double[][][] allGRateMeasurements = new double[nReps][][];

        int[][][] allPreciseMeasurements = new int[nReps][][];
        double[][][] allPreciseGRateMeasurements = new double[nReps][][];

        for(int r = 0; r < nReps; r++){

            boolean alreadyRecorded = false, alreadyPreciselyRecorded = false;

            int[][] popsOverTime = new int[nTimeMeasurements+1][];
            double[][] gRatesOverTime = new double[nTimeMeasurements+1][];
            int timerCounter = 0;

            int[][] precisePopsOverTime = new int[nTimeMeasurements+1][];
            double[][] preciseGRatesOverTime = new double[nTimeMeasurements+1][];
            int preciseTimerCounter = 0;


            BioSystem bs = new BioSystem(L, S, c, "bla");

            while(bs.timeElapsed <= duration){

                bs.performAction();

                if((bs.getTimeElapsed()%preciseInterval >= 0. && bs.getTimeElapsed()%preciseInterval <= 0.01) && !alreadyPreciselyRecorded &&
                        preciseTimerCounter <= nTimeMeasurements){

                    System.out.println("rep: "+r+"\ttime elapsed: "+String.valueOf(bs.getTimeElapsed())+"\tPRECISE");
                    precisePopsOverTime[preciseTimerCounter] = bs.getSpatialDistributionArray();
                    preciseGRatesOverTime[preciseTimerCounter] = bs.getGrowthRatesArray();

                    alreadyPreciselyRecorded = true;
                    preciseTimerCounter++;
                }
                if(bs.getTimeElapsed()%preciseInterval >= 0.1) alreadyPreciselyRecorded = false;


                if((bs.getTimeElapsed()%interval >= 0. && bs.getTimeElapsed()%interval <= 0.01) && !alreadyRecorded){

                    System.out.println("rep: "+r+"\ttime elapsed: "+String.valueOf(bs.getTimeElapsed()));
                    popsOverTime[timerCounter] = bs.getSpatialDistributionArray();
                    gRatesOverTime[timerCounter] = bs.getGrowthRatesArray();

                    alreadyRecorded = true;
                    timerCounter++;
                }
                if(bs.getTimeElapsed()%interval >= 0.1) alreadyRecorded = false;
            }

            allMeasurements[r] = popsOverTime;
            allGRateMeasurements[r] = gRatesOverTime;
            allPreciseMeasurements[r] = precisePopsOverTime;
            allPreciseGRateMeasurements[r] = preciseGRatesOverTime;
        }

        double[][] averagedPopDistributions = Toolbox.averagedResults(allMeasurements);
        double[][] averagedGRateDistributions = Toolbox.averagedResults(allGRateMeasurements);
        double[][] averagedPrecisePopDistributions = Toolbox.averagedResults(allPreciseMeasurements);
        double[][] averagedPreciseGRateDistributions = Toolbox.averagedResults(allPreciseGRateMeasurements);

        Toolbox.printAveragedResultsToFile(filename, averagedPopDistributions);
        Toolbox.printAveragedResultsToFile(filename_gRate, averagedGRateDistributions);
        Toolbox.printAveragedResultsToFile(filename_precise, averagedPrecisePopDistributions);
        Toolbox.printAveragedResultsToFile(filename_gRate_precise, averagedPreciseGRateDistributions);
    }




}
