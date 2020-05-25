import java.util.LinkedList;
import java.util.List;

//矩阵链相乘
public class MatrixChainMultiplication {
    //m[i][j]表示的是从矩阵链从i到j需要的乘积次数
    int[][] m;
    //s[i][j]表示矩阵链从i到j相乘需要的最小乘积次数的分割k
    int[][] seg;
    //迭代方法实现
    public int matrixMulti(int[] p) {
        int n = p.length;
        //链的长度，从2到n-1，n-1是矩阵的数量
        for (int len = 2; len<p.length; len++) {
            //i记录第几个链的开头，有n-r+1，即6-2+1，i的取值是1，2，3，4共4条链
            for (int i = 1; i<n-len+1; i++) {
                //j记录的是链的结尾
                int j = i+len-1;
                m[i][j] = Integer.MAX_VALUE;
                for (int k=i; k<j; k++) {
                    int temp = m[i][k] + m[k+1][j] + p[i-1]*p[k]*p[j];
                    if (temp < m[i][j]) {
                        m[i][j] = temp;
                        //记录乘积次数最小的分割k
                        seg[i][j] = k;
                    }
                }
            }
        }
        return m[1][n-1];
    }
    //递归写法
    public int recurMatrixMul(int[] p, int i, int j) {
        //如果只有一个矩阵，直接返回
        if (i == j) {
            seg[i][j] = i;
            //默认为0
            return m[i][j];
        }
        //不重复计算
        if (m[i][j] != 0) return m[i][j];
        m[i][j] = Integer.MAX_VALUE;
        //一步步寻找最适合分割的k，p[i-1]*p[k]*p[j]是分割后合并需要的乘积
        for (int k=i; k<j; k++) {
            int temp = recurMatrixMul(p, i, k) + recurMatrixMul(p, k+1, j) + p[i-1]*p[k]*p[j];
            if (temp < m[i][j]) {
                m[i][j] = temp;
                seg[i][j] = k;
            }
        }
        return m[i][j];
    }

    public void printPartition(int[] p, int i, int j) {
        if (i == j) {
            System.out.print("A" + i);
            return;
        }
        System.out.print("(");
        printPartition(p, i, seg[i][j]);
        System.out.print("*");
        printPartition(p, seg[i][j] + 1, j);
        System.out.print(")");
    }

    public static void main(String[] args) {
        MatrixChainMultiplication test = new MatrixChainMultiplication();
        //to-do将矩阵链转化为p数组，p数组里面存放的是矩阵的行列，数组大小为矩阵数量+1
        int[] p = new int[]{30,35,15,5,10,20,60,50,40,20};
        int n = p.length;
        test.m = new int[n][n];
        test.seg = new int[n][n];
        //矩阵自己相乘次数为0
        for (int i=0; i<n; i++) {
            test.m[i][i] = 0;
        }
        //打印乘法
        System.out.print("数组P:[");
        for (int num:p) {
            System.out.print(num + ",");
        }
        System.out.println("]");
        System.out.println("矩阵乘积次数：" + test.matrixMulti(p));
//        System.out.println("矩阵乘积次数：" + test.recurMatrixMul(p, 1, n-1));
        //打印加括号情况
        System.out.print("矩阵乘积顺序为：");
        test.printPartition(p, 1, n-1);
    }
}
