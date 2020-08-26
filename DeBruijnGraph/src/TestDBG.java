import java.io.*;

/**
 * DeBruijnGraph
 * 2020/8/6 9:32
 * DeBruijnGraph算法启动类
 *
 * @author Chan
 * @since
 **/
public class TestDBG {
    /**
     * 原串
     */
    public static String DATA_FILE = "./data.txt";
    /**
     * 子串文件
     */
    public static String INPUT_FILE = "./input/reads.txt";
    /**
     * 拼接后的字符串
     */
    public static String OUTPUT_FILE = "./output/result_str.txt";

    public static void main(String[] args) throws IOException {
        if (args.length == 2) {
            System.out.println("input file path:" + args[0]);
            System.out.println("output file path:" + args[1]);
            TestDBG.INPUT_FILE = args[0];
            TestDBG.OUTPUT_FILE = args[1];
        } else {
            System.out.println("input file path:" + TestDBG.INPUT_FILE);
            System.out.println("output file path:" + TestDBG.OUTPUT_FILE);
        }

        long startTime = System.currentTimeMillis();
        //getString();
        //Kmers 大小
        int k = 30;
        DeBruijnGraph dbg = new DeBruijnGraph(INPUT_FILE, k);
        try {
            String res = dbg.getEulerianPath();
            writeResultString(res);
//            System.out.println("String length:" + res.length());
//            System.out.print("newStr:" + res.substring(0, 500));
////            System.out.println(res.substring(FileUtil.SIZE - 500));
        } catch (Exception e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("运行时间：" + (float)(endTime - startTime)/1000 + "s");
    }

    /**
     * 获取测试原串
     */
    public static void getString() {
        System.out.print("oldStr:");
        File file = new File(INPUT_FILE);
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String str = br.readLine();
            System.out.print(str.substring(0, 500));
            System.out.println(str.substring(FileUtil.SIZE - 500));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将拼接完成字符串写入文件
     * @param newStr 结果字符串
     */
    public static void writeResultString(String newStr) {
        File outputFile = new File(OUTPUT_FILE);
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(outputFile);
            bw = new BufferedWriter(fw);
            bw.write(newStr);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
