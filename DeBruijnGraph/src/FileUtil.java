import java.io.*;
import java.util.LinkedList;
import java.util.Random;

/**
 * DeBruijnGraph
 * 2020/8/6 20:14
 * 工具类，生成测试数据并抽取子串
 * 设置POWER,随机生成长度为10^POWER的字符串，生成data.txt
 * 抽取60*10^POWER条长度为60-100的子串，生成reads.txt
 *
 * @author Chan
 * @since
 **/
public class FileUtil {
    /**
     * 生成字符串的量级
     */
    public static final int POWER = 3;
    public static final int SIZE = (int)Math.pow(10, POWER);

    /**
     * 读取子串数据
     * @param fileName 读取文件位置
     * @return
     */
    public static LinkedList<String> getData(String fileName) {
        LinkedList<String> res = new LinkedList<>();
        File file = new File(fileName);
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            String str = null;
            while ((str = br.readLine()) != null) {
                res.add(str);
            }
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
        return res;
    }

    /**
     * 抽取子串
     * @param fileName 子串文件位置
     */
    public static void spiltData(String fileName) {
        File fileIn = new File(fileName);
        File fileOut = new File(TestDBG.INPUT_FILE);
        RandomAccessFile raf = null;
        FileWriter fw = null;
        BufferedWriter bw = null;

        byte[] buffer = new byte[100];
        Random random = new Random();
        //随机确定reads开始位置以及长度
        int start=0, length=0;
        try {
            raf = new RandomAccessFile(fileIn, "r");
            System.out.println(raf.length());
            fw = new FileWriter(fileOut, false);
            bw = new BufferedWriter(fw);
            for (int i = 0; i < 60 * SIZE; i++) {
                start = random.nextInt(SIZE-60);
//                length = random.nextInt(40) + 60;
                length = Math.min(random.nextInt(40) + 60, (int)(raf.length() - start));
                //改变文件偏移量
                raf.seek(start);
                //清空buffer
                buffer = new byte[length];
                raf.read(buffer, 0, length);
                bw.write(new String(buffer));
                bw.newLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("start" + start);
            e.printStackTrace();
        } finally {
            try {
                bw.close();
                fw.close();
                raf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 随机生成A-Z的字符串
     */
    public static void initData() {
        char start = 'A';
        Random random = new Random();
        File file = new File(TestDBG.DATA_FILE);
        OutputStreamWriter osw = null;
        char[] tempChars = new char[100];
        try {
            osw = new OutputStreamWriter(new FileOutputStream(file));
            for (int i = 0; i < (int)Math.pow(10, POWER-2); i++) {
                for (int j = 0; j < 100; j++) {
                    tempChars[j] = (char)(start + random.nextInt(26));
                }
                System.out.println(tempChars);
                osw.write(tempChars);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                osw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        FileUtil.initData();
        FileUtil.spiltData(TestDBG.DATA_FILE);
    }
}
