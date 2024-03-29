package com.ve.lib.common.utils.encrypt;

/**
 * @Author weiyi
 * @Date 2022/5/18
 * @Description current project lockit-android
 * 浅析MD5加密
 * https://blog.csdn.net/qq_42154576/article/details/90081845
 * <p>
 * MD5消息摘要算法(MD5 Message-Digest Algorithm),一种被广泛使用的密码散列函数，可以产生出一个128位（16字节）的散列值（hash value），用于确保信息传输完整一致。
 * MD5加密是一种不可逆的加密算法，不可逆加密算法的特征是加密过程中不需要使用密钥，输入明文后由系统直接经过加密算法处理成密文，这种加密后的数据是无法被解密的，只有重新输入明文，并再次经过同样不可逆的加密算法处理，得到相同的加密密文并被系统重新识别后，才能真正解密。
 */
public class MD5Util {

    /*
     *四个链接变量 标准幻数（按大端字节序存储-高位字节排放在内存的低地址端(即该值的起始地址),低位字节排放在内存的高地址端）
     */
    private final int A = 0x67452301;//01234567
    private final int B = 0xefcdab89;//89abcdef
    private final int C = 0x98badcfe;//fedcba98
    private final int D = 0x10325476;//76543210

    /*
     *ABCD的临时变量
     */
    private int Atemp, Btemp, Ctemp, Dtemp;

    /*
     *常量T
     *公式:floor(abs(sin(i+1))×(2pow32)  pow：2的32次幂
     */
    private final int T[] = {
            0xd76aa478, 0xe8c7b756, 0x242070db, 0xc1bdceee, 0xf57c0faf,
            0x4787c62a, 0xa8304613, 0xfd469501, 0x698098d8, 0x8b44f7af,
            0xffff5bb1, 0x895cd7be, 0x6b901122, 0xfd987193, 0xa679438e,
            0x49b40821, 0xf61e2562, 0xc040b340, 0x265e5a51, 0xe9b6c7aa,
            0xd62f105d, 0x02441453, 0xd8a1e681, 0xe7d3fbc8, 0x21e1cde6,
            0xc33707d6, 0xf4d50d87, 0x455a14ed, 0xa9e3e905, 0xfcefa3f8,
            0x676f02d9, 0x8d2a4c8a, 0xfffa3942, 0x8771f681, 0x6d9d6122,
            0xfde5380c, 0xa4beea44, 0x4bdecfa9, 0xf6bb4b60, 0xbebfbc70,
            0x289b7ec6, 0xeaa127fa, 0xd4ef3085, 0x04881d05, 0xd9d4d039,
            0xe6db99e5, 0x1fa27cf8, 0xc4ac5665, 0xf4292244, 0x432aff97,
            0xab9423a7, 0xfc93a039, 0x655b59c3, 0x8f0ccc92, 0xffeff47d,
            0x85845dd1, 0x6fa87e4f, 0xfe2ce6e0, 0xa3014314, 0x4e0811a1,
            0xf7537e82, 0xbd3af235, 0x2ad7d2bb, 0xeb86d391
    };

    /*
     *向左位移数,计算方法未知
     */
    private final int s[] = {
            7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
            5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20,
            4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
            6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21
    };

    /*
     *初始化函数
     */
    private void init() {
        Atemp = A;
        Btemp = B;
        Ctemp = C;
        Dtemp = D;
    }

    /*
     *移动一定位数
     */
    private int shift(int a, int s) {
        return (a << s) | (a >>> (32 - s));//右移的时候，高位一定要补零，而不是补充符号位
    }

    /*
     *主循环
     * 辅助方法
     *  我们定义四个辅助方法。
     *  F(x,y,z) = (x & y) | ((~x) & z)
     *  G(x,y,z) = (x & z) | (y & (~z))
     *  H(x,y,z) = x ^ y ^ z
     *  I(x,y,z) = y ^ (x | (~z))
     *  其中，x，y，z长度为32bit
     */
    private void MainLoop(int M[]) {
        int F, g;
        int a = Atemp;
        int b = Btemp;
        int c = Ctemp;
        int d = Dtemp;
        for (int i = 0; i < 64; i++) {
            if (i < 16) {
                F = (b & c) | ((~b) & d);
                g = i;
            } else if (i < 32) {
                F = (d & b) | ((~d) & c);
                g = (5 * i + 1) % 16;
            } else if (i < 48) {
                F = b ^ c ^ d;
                g = (3 * i + 5) % 16;
            } else {
                F = c ^ (b | (~d));
                g = (7 * i) % 16;
            }
            int tmp = d;
            d = c;
            c = b;
            b = b + shift(a + F + T[i] + M[g], s[i]);
            a = tmp;
        }
        Atemp = a + Atemp;
        Btemp = b + Btemp;
        Ctemp = c + Ctemp;
        Dtemp = d + Dtemp;
    }

    /*
     *填充函数
     *处理后应满足bits≡448(mod512),字节就是bytes≡56（mode64)
     *填充方式为先加一个1,其它位补零
     *最后加上64位的原来长度
     */
    private int[] add(String str) {
        int num = ((str.length() + 8) / 64) + 1;//以512位，64个字节为一组
        int strByte[] = new int[num * 16];//64/4=16，所以有16个整数
        for (int i = 0; i < num * 16; i++) {//全部初始化0
            strByte[i] = 0;
        }
        int i;
        for (i = 0; i < str.length(); i++) {
            strByte[i >> 2] |= str.charAt(i) << ((i % 4) * 8);//一个整数存储四个字节，小端序
        }
        strByte[i >> 2] |= 0x80 << ((i % 4) * 8);//尾部添加1
        /*
         *添加原长度，长度指位的长度，所以要乘8，然后是小端序，所以放在倒数第二个,这里长度只用了32位
         */
        strByte[num * 16 - 2] = str.length() * 8;
        return strByte;
    }

    /*
     *调用函数
     */
    public String getMD5(String source) {
        init();
        int strByte[] = add(source);
        for (int i = 0; i < strByte.length / 16; i++) {
            int num[] = new int[16];
            for (int j = 0; j < 16; j++) {
                num[j] = strByte[i * 16 + j];
            }
            MainLoop(num);
        }
        return changeHex(Atemp) + changeHex(Btemp) + changeHex(Ctemp) + changeHex(Dtemp);
    }

    /*
     *整数变成16进制字符串
     */
    private String changeHex(int a) {
        String str = "";
        for (int i = 0; i < 4; i++) {
            str += String.format("%2s", Integer.toHexString(((a >> i * 8) % (1 << 8)) & 0xff)).replace(' ', '0');

        }
        return str;
    }

    /*
     *单例
     */
    private static MD5Util instance;

    public static MD5Util getInstance() {
        if (instance == null) {
            instance = new MD5Util();
        }
        return instance;
    }

    private MD5Util() {
    }

    ;


    public static void main(String[] args) {
        String data = "";
        String str = MD5Util.getInstance().getMD5(data).toUpperCase();
        System.out.println(str);
    }
}