public class Microhabitat {

    //no. of bacteria in the microhabitat
    private int N;
    private double c;
    int S, S_max;

    //death and migration rates
    private double d = 0., b = 0.1;
    private double K_prime = 33.;

    public Microhabitat(int S, double c){
        this.S = S;
        this.S_max = S;
        this.c = c;
        this.N = 0;
    }

    public int getS(){return S;}
    public double getC(){return c;}
    public int getN(){return N;}
    public double getD(){return d;}
    public double getB(){return b;}


    public double beta(){

        double mu = S/(K_prime+S);
        double mu_max = S_max/(K_prime+S_max);
        return 1. + 9.*mu/mu_max;
    }

    public double phi_c(){
        double phi_c = 1. - (c/beta())*(c/beta());
        return (phi_c > 0.) ? phi_c : 0.;
    }

    public double replicationRate(){

        return phi_c()*(S/(K_prime + S));
    }


    public void fillWithWildType(int initalPop){
        N+=initalPop;
    }

    public void consumeANutrient(){
        if(S > 0) S--;
    }

    public void removeABacterium(){ if(N>0) N--;}
    public void addABacterium(){N++;}


}
