public class AssemblyLineScheduling {
    int[] line1;
    int[] line2;
    int[] f1;
    int[] f2;

    public int assemblyLineScheduling(int[] time1, int[] time2, int e1, int e2, int t12, int t21) {
        int n = time1.length;
        f1 = new int[time1.length];
        f2 = new int[time2.length];
        line1 = new int[time1.length];
        line2 = new int[time2.length];
        f1[0] = e1 + time1[0];
        line1[n-1] = 1;
        f2[0] = e2 + time1[0];
        line2[n-1] = 2;
        for (int i=1; i<n; i++) {
            //生产线1流水进行i步骤时间与生产线2转过来生产线1进行i步骤时间对比
            if (f1[i-1] + time1[i] <= f2[i-1] + time1[i] + t21) {
                f1[i] = f1[i-1] + time1[i];
                line1[i-1] = 1;
            } else {
                f1[i] = f2[i-1] + time1[i] + t21;
                line1[i-1] = 2;
            }
            //生产线2流水进行i步骤时间与生产线1转过来生产线1进行i步骤时间对比
            if (f2[i-1] + time2[i] <= f1[i-1] + time2[i] + t12) {
                f2[i] = f2[i-1] + time2[i];
                line2[i-1] = 2;
            } else {
                f2[i] = f1[i-1] + time2[i] + t12;
                line2[i-1] = 1;
            }
        }
        return Math.min(f1[n-1], f2[n-1]);
    }

    public static void main(String[] args) {
        int[] time1 = {2,4,3,6,1};
        int[] time2 = {5,2,3,1,7};
        int[] line;
        int n = time1.length;
        AssemblyLineScheduling test  = new AssemblyLineScheduling();
        int min = test.assemblyLineScheduling(time1, time2, 2,3, 2,2);
        System.out.println();
        if (test.f1[n-1] == min) {
            line = test.line1;
        } else {
            line = test.line2;
        }
        System.out.println("最少调度时间：" + min);
        System.out.print("调度结果：[");
        for (int num:line) {
            System.out.print("生产线" + num + ",");
        }
        System.out.print("]");
    }

}
