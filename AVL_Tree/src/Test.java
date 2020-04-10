import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        AvlTree tree = new AvlTree();
        //测试LL，RR
//        int[] array = new int[]{5,3,6,2,1};
//        int[] array = new int[]{3,2,5,4,6};
        //测试LR
//        int[] array = new int[]{3,1,2};
//        int[] array = new int[]{5,2,6,1,4,3};
//        int[] array = new int[]{5,2,6,1,3,4};
        //测试RL
//        int[] array = new int[]{1,3,2};
//        int[] array = new int[]{2,1,5,6,4,3};
//        int[] array = new int[]{2,1,5,6,3,4};

        //总测试
        int[] array = new int[]{12,4,1,3,7,8,10,9,2,11,6,5};
        int[] del_array = new int[]{4,12,8};
        System.out.println("******************************************************");
        for (int item:array) {
            tree.addNode(item);
            System.out.println("添加节点"+item);
            tree.show();
        }
        for (int item:del_array) {
            tree.deleteNode(item);
            System.out.println("删除节点"+item);
            tree.show();
        }
    }
}
