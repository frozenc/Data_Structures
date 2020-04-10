import java.util.List;
import java.util.Stack;

public class Node {
    //左孩子
    private Node left;
    //右孩子
    private Node right;
    //父节点
    private Node parent;
    //当前节点值
    private int value;
    //平衡因子：左子树高度-右子树高度
    private int factor;

    public Node(int value) {
        this.value = value;
        this.factor = 0;
    }

    public boolean hasBrother() {
        //自己就是根节点，无兄弟节点
        if (this.parent == null) return false;
        //父节点的左右孩子都不为空，则有兄弟节点，否则无兄弟节点
        if (this.parent.getLeft() != null && this.parent.getRight() != null) {
            return true;
        } else {
            return false;
        }
    }

    public Node getBrother() {
        if (this.parent == null) {
            return null;
        }
        if (this.parent.getLeft() == this) {
            return this.parent.getRight();
        } else {
            return this.parent.getLeft();
        }
    }


    public boolean hasChild() {
        if (this.getLeft() != null || this.getRight() != null) {
            return true;
        } else {
            return false;
        }
    }

    public void deleteChild(Node node) {
        if (node == null) return;
        if (node == this.getLeft()) {
            this.setLeft(null);
        } else if (node == this.getRight()) {
            this.setRight(null);
        }
    }

    public Node getLeftLargestChild() {
        //无左孩子直接返回
        if (this.getLeft() == null) {
            return null;
        }
        Node child = this.getLeft();
        while (child.getRight() != null) {
            child = child.getRight();
        }
        return child;
    }

    //计算该节点的高度
    public int height() {
        int num = 1;
        int leftHeight = 0;
        int rightHeight = 0;
        if (this.getLeft() != null) {
            leftHeight = this.getLeft().height();
        }
        if (this.getRight() != null) {
            rightHeight = this.getRight().height();
        }
        num += Math.max(leftHeight, rightHeight);
        return num;
    }

    //平衡因子+1
    public void addFactor() {
        this.factor += 1;
    }

    //平衡因子-1
    public void subFactor() {
        this.factor -= 1;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getFactor() {
        return factor;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

//    @Override
//    public String toString() {
//        return String.valueOf(this.value);
//    }

    public String node2Str(Node node) {
        if (node == null) {
            return  "null";
        }else {
            return String.valueOf(node.getValue());
        }
    }

    @Override
    public String toString() {
        String parentStr;
        if (parent == null) {
            parentStr = "null";
        }else {
            parentStr = String.valueOf(parent.getValue());
        }

        return "Node{" +
                "value=" + value +
                ", left=" + node2Str(left) +
                ", right=" + node2Str(right) +
                ", parent=" + node2Str(parent) +
                ", factor=" + factor +
                '}';
    }
}
