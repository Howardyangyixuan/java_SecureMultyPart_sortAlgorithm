import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIGlobalBinding;
import org.omg.CORBA.UserException;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.math.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class ThirdPart{
    //选取两个较大的质数p与q，lambda是p-1与q-1的最小公倍数
    //private key
    private BigInteger p, q, lambda;

    private int totalNum;
    //public key
    //n_square = n*n
    //n是p与q的乘积
    public BigInteger n;
    public BigInteger n_square;
    BigInteger g;

    //public parameters
    public int bitLength;
    public BigInteger P;


    public ThirdPart(int bitLengthVal, int certainty) {
        Key(bitLengthVal, certainty);
    }
    private ThirdPart(int totalNum) {
        Key(30, 64);
        this.totalNum = totalNum;
    }
    private void Key(int bitLengthVal, int certainty) {
        bitLength = bitLengthVal;
        //随机构造两个大素数，详情参见API，BigInteger的构造方法
        p = new BigInteger(bitLength / 2, certainty, new Random());
        q = new BigInteger(bitLength / 2, certainty, new Random());
//        p = new BigInteger(bitLength, certainty, new Random());
//        q = new BigInteger(bitLength, certainty, new Random());

        //n=p*q;
        n = p.multiply(q);

        //nsquare=n*n;
        n_square = n.multiply(n);
        g=new BigInteger("2");

        //求p-1与q-1的乘积除于p-1于q-1的最大公约数
        lambda = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE))
                .divide(p.subtract(BigInteger.ONE).gcd(q.subtract(BigInteger.ONE)));

        //构造P
//        P = new BigInteger(bitLength,new Random());
//        System.out.println("P is :"+P.toString());
        P = new BigInteger("40732698");

        //检测g是某满足要求
        if (g.modPow(lambda, n_square).subtract(BigInteger.ONE).divide(n).gcd(n).intValue() != 1) {
            System.out.println("g的选取不合适!");
            System.exit(1);
        }
    }

    public BigInteger getN(){
        return n;
    }

    public BigInteger getG(){
        return g;
    }

    public BigInteger getP(){
        return P;
    }

    //解密
    private BigInteger De(BigInteger c){
        BigInteger u = g.modPow(lambda, n_square).subtract(BigInteger.ONE).divide(n).modInverse(n);
        return c.modPow(lambda, n_square).subtract(BigInteger.ONE).divide(n).multiply(u).mod(n).mod(P);
    }


    public int[] intToVector(int sum){
        int[] vector = new int[totalNum];
        for (int i = 0;i<totalNum;i++){
            vector[i]= sum % (totalNum+1);
            //System.out.println(sum);
            //System.out.println(vector[i]);
            sum = sum/(totalNum+1);
        }
        return vector;
    }

    public static void main(String[] args) {
        int totalNum = 4;
        ThirdPart thirdPart = new ThirdPart(totalNum);
        //创建两个大整数m1,m2:
        BigInteger m1 = new BigInteger("32");
        BigInteger m2 = new BigInteger("2216");
        BigInteger m3 = new BigInteger("15");
        BigInteger m4 = new BigInteger("354");
        BigInteger max = new BigInteger("4096");
        BigInteger m[] = new BigInteger[]{m1, m2, m3, m4};

        User user1 = new User(m1,4,2,thirdPart.getP(),max,thirdPart.getG(),thirdPart.getN());
        User user2 = new User(m2,4,2,thirdPart.getP(),max,thirdPart.getG(),thirdPart.getN());
        User user3 = new User(m3,4,2,thirdPart.getP(),max,thirdPart.getG(),thirdPart.getN());
        User user4 = new User(m4,4,2,thirdPart.getP(),max,thirdPart.getG(),thirdPart.getN());
        User[] user = {user1, user2, user3, user4};

        for (int i = 0; i < user.length ; i++) {
            int[] personRand = user[i].getPersonRand();
            BigInteger pos = user[i].getPosRandNum();
            BigInteger neg = user[i].getNegRandNum();
            for (int j = 0; j < personRand.length; j++) {
                if (j % 2 == 0) {
                    //System.out.println(user[personRand[j]].updateG(pos).toString());
                    user[personRand[j]].updateG(pos);
                } else {
                   // System.out.println(user[personRand[j]].updateG(neg).toString());
                    user[personRand[j]].updateG(neg);
                }
            }
        }
//
//        System.out.println(user1.vectorToInt());
//        System.out.println(user2.vectorToInt());
//        System.out.println(user3.vectorToInt());
//        System.out.println(user4.vectorToInt());

        //测试求和转换
//        int sum = Stream.of(user4, user3, user2, user1).mapToInt(User::vectorToInt).sum();
//        System.out.println(sum);
//        int[] a = thirdPart.intToVector(sum);

        BigInteger sum =
                (user1.En().multiply(user2.En()).multiply(user3.En()).multiply(user4.En())).mod(thirdPart.getN().multiply(thirdPart.getN()));
        System.out.println(sum.toString());
        int b = thirdPart.De(sum).intValue()%thirdPart.getP().intValue();
        System.out.println(b);
        int[] a = thirdPart.intToVector(thirdPart.De(sum).intValue());
        System.out.println(Arrays.toString(a));
//        int[] a =
//                thirdPart.intToVector(user4.vectorToInt()+user3.vectorToInt()+user2.vectorToInt()+user1.vectorToInt());
//        System.out.println(Arrays.toString(a));
        System.out.println("user1 is number:"+user1.showSort(a));
        System.out.println("user2 is number:"+user2.showSort(a));
        System.out.println("user3 is number:"+user3.showSort(a));
        System.out.println("user4 is number:"+user4.showSort(a));


        //单用户加密解密测试
//        System.out.println("原文是:");
//        System.out.println(m1);
//
//        System.out.println("密文是:");
//        System.out.println(user1.En().toString());
//
//        System.out.println("解密后是:");
//        System.out.println(thirdPart.De(user1.En()));
//
//        System.out.println("解密后的向量是:");
//        System.out.println(Arrays.toString(thirdPart.intToVector(thirdPart.De(user1.En()).intValue())));
//        //System.out.println(Arrays.toString(thirdPart.intToVector(thirdPart.De(user2.En()).intValue())));

    }
}
//
