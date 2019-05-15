import java.math.BigInteger;
import java.util.HashSet;
import java.util.Random;
import java.util.Arrays;

public class User {

    private BigInteger secret;
    private int totalNum;
    private int lnNum;
    private BigInteger G;
    private BigInteger P;
    private BigInteger max;
    private BigInteger randNum;

    private BigInteger g;
    private BigInteger N;
    private BigInteger N_square;



    private static final int PERSON_RAND = 2;
    private static final int bitLength = 2;

    User(BigInteger secret, int totalNum, int lnNum, BigInteger P, BigInteger max, BigInteger g, BigInteger N){

        this.secret = secret;
        this.totalNum = totalNum;
        this.lnNum = lnNum;
        this.G = BigInteger.ONE;
        this.P = P;
        this.max = max;
        this.g = g;
        this.N = N;
        this.N_square = N.multiply(N);

        Random rand = new Random();
       // System.out.println("P is :"+P.toString());

        String a = String.valueOf(rand.nextInt(P.intValue()));
        //System.out.println("a is :"+a);

        this.randNum = new BigInteger(a);

    }

    private int[] getSortVector(){
        int distance =
                (int) Math.floor((Math.log(max.doubleValue())/Math.log((double)lnNum))/totalNum);
        //System.out.println(distance);
        int[] sortVector = new int[totalNum];
        int bucketNum = (int) Math.floor((Math.log(secret.doubleValue())/Math.log((double)lnNum)-1)/distance);
        //System.out.println(bucketNum);
        for (int j = 0; j<totalNum; j++) {
            if (j == bucketNum) {
                sortVector[j] = 1;
            } else {
                sortVector[j] = 0;
            }
        }
//        for (int i =0;i<totalNum;i++)
//            System.out.println(sortVector[i]);
        //System.out.println(sortVector);
        return sortVector;
    }

    private int vectorToInt(){
        int cnt = 0;
        for(int i=0; i< totalNum;i++)
        {
            if(this.getSortVector()[i]==1) {
                cnt = (int) Math.pow(totalNum + 1,i);
            }
        }
        return cnt;
    }

    BigInteger getPosRandNum(){
//        System.out.println(P.toString());
//        System.out.println(randNum.toString());
//        System.out.println(N_square.toString());
       // System.out.println("POS:"+g.modPow(P.add(randNum),N_square).toString());
        return g.modPow(P.add(randNum),N_square);
    }


    BigInteger getNegRandNum(){
//        System.out.println(P.toString());
//        System.out.println(randNum.toString());
//        System.out.println(N_square.toString());
        //System.out.println("NEG:"+g.modPow(P.subtract(randNum),N_square).toString());
        return g.modPow(P.subtract(randNum),N_square);
    }

    int[] getPersonRand() {
        int[] randPerson = new int[PERSON_RAND];
        Random rand = new Random();
//        for (int i = 0; i < PERSON_RAND; i++) {
//            randPerson[i] = rand.nextInt(totalNum);
//        }
        HashSet<Integer> set = new HashSet<>();
        while(set.size()<PERSON_RAND) {
            set.add(rand.nextInt(totalNum));
        }
        Integer[] randPerson_ =  new Integer[PERSON_RAND];
        set.toArray(randPerson_);

        for (int i = 0; i < PERSON_RAND; i++) {
            randPerson[i] = randPerson_[i];
      //      System.out.println(randPerson[i]);
        }
        return randPerson;
    }

    BigInteger updateG(BigInteger update){
        this.G = G.multiply(update).mod(N_square);
        return G;
    }

    BigInteger En(){
        BigInteger r = new BigInteger(bitLength, new Random());
        BigInteger a = new BigInteger(String.valueOf(this.vectorToInt()));
        return g.modPow(a, N_square).multiply(r.modPow(N, N_square)).multiply(G).mod(N_square);
    }

    int showSort(int[] vector){
        int[] sortVector;
        sortVector = getSortVector();
        int i = 0;
        int cnt = 0;
        while (sortVector[i]!=1) {
            cnt += vector[i++];
        }
        return totalNum-cnt;
    }

    public static void main(String[] args){
        BigInteger m1 = new BigInteger("2216");
        BigInteger p = new BigInteger("10");
        BigInteger max = new BigInteger("4096");
        BigInteger N = new BigInteger("5");
        BigInteger g = new BigInteger("2");


        User user = new User(m1,4,2,p,max,g,N);
        System.out.println(Arrays.toString(user.getSortVector()));
        System.out.println(Arrays.toString(user.getPersonRand()));
        System.out.println(user.vectorToInt());
        System.out.println(user.getPosRandNum());
        System.out.println(user.getNegRandNum());
        System.out.println("origin:"+user.En().toString());
        user.updateG(user.getNegRandNum());
        System.out.println("after sub:"+user.En());
        user.updateG(user.getPosRandNum());
        System.out.println("after add back:"+user.En());

        int[] a ={1,1,1,1};
        System.out.println(user.showSort(a));





    }
}

