import java.util.LinkedList;
import java.util.List;

public class AvlTree {
    //根节点
    private Node root;

    /**
     * 添加节点
     * @param value
     * @return
     */
    public boolean addNode(int value) {
        //判断根节点是否为空，创建根节点
        if (root == null) {
            root = new Node(value);
            return true;
        }
        Node parent = root;
        Node child = root;
        while (child != null) {
            if (value < parent.getValue()) {
                child = parent.getLeft();
            } else if (value > parent.getValue()) {
                child = parent.getRight();
            } else {
                //找到了相同值的节点，添加节点失败
                return false;
            }
            if (child != null) parent = child;
        }
        Node node = new Node(value);
        node.setParent(parent);
        if (node.getValue() < parent.getValue()) {
            parent.setLeft(node);
            if (parent.getRight() == null) {
                //增加左孩子节点，无兄弟节点，计算树的平衡状态
                addBalance(node);
            } else {
                //增加左孩子节点，有兄弟节点，平衡因子+1
                parent.addFactor();
            }
        } else if (node.getValue() > parent.getValue()) {
            parent.setRight(node);
            if (parent.getLeft() == null) {
                //增加右孩子节点，无兄弟节点，计算树的平衡状态
                addBalance(node);
            } else {
                //增加右孩子节点，有兄弟节点，平衡因子-1
                parent.subFactor();
            }
        }
        //成功添加节点
        return true;
    }

    /**
     * 查找平衡树中对应值的节点
     * @param value
     * @return
     */
    public Node getNode(int value) {
        Node node = root;
        while (node != null) {
            if (value < node.getValue()) {
                node = node.getLeft();
            } else if (value > node.getValue()) {
                node = node.getRight();
            } else {
                //找到对应值的节点
                return node;
            }
        }
        return null;
    }

    /**
     * 删除节点
     * 1.无子节点，分为有兄弟节点，无兄弟节点（是否根节点），均需要判断删除后是否影响平衡
     * 2.有一个子节点，子节点上移，判断平衡
     * 3.有两个子节点，删除左子树中的最大节点，将该节点的值替换掉要删除的节点
     * @param value
     * @return
     */
    public void deleteNode(int value) {
        Node current = getNode(value);
        if (current == null) {
            return;
        }
        Node parent = current.getParent();
        if (!current.hasChild()) {
            //无子节点
            if (parent == null) {
                root = null;
                return;
            }
            if (current.hasBrother()) {
                //有兄弟节点，高度不变,判断父节点是否平衡
                isParentBalance(current);
                parent.deleteChild(current);
            }else {
                //无兄弟节点，高度减一
                deleteBalance(current);
                parent.deleteChild(current);
            }
        } else if (current.getLeft() != null && current.getRight() == null) {
            //只有一个左孩子,把左孩子上移
            if (parent == null) {
                //是根节点
                this.root = current.getLeft();
                return;
            }
            deleteBalance(current);
            if (current == parent.getLeft()) {
                parent.setLeft(current.getLeft());
            } else {
                parent.setRight(current.getLeft());
            }
            current.getLeft().setParent(parent);
        } else if (current.getRight() != null && current.getLeft() == null) {
            //只有一个右孩子，把右孩子上移
            if (parent == null) {
                //是根节点
                this.root = current.getRight();
                return;
            }
            deleteBalance(current);
            if (current == parent.getLeft()) {
                parent.setLeft(current.getRight());
            } else {
                parent.setRight(current.getRight());
            }
            current.getRight().setParent(parent);
        } else {
            //有两个子节点，删除左子树中的最大节点，将该节点的值替换掉要删除的节点
            Node largeNode = current.getLeftLargestChild();
            int temp = largeNode.getValue();
            this.deleteNode(temp);
            current.setValue(temp);
        }
        current = null; //GC回收
    }

    /**
     *增加节点，修改父节点平衡因子，依次向上遍历修改平衡因子
     * 调整不平衡子树，各种旋转实现
     * @param node
     */
    private void addBalance(Node node) {
        Node parent = node.getParent();
        //记录当前节点
        Node current = node;
        //记录上一个节点,判断不平衡类型做准备
        Node prev = node;
        Boolean bal = true;
        while (parent != null && bal) {
            if (parent.getLeft() == current && (Math.abs(parent.getFactor()+1) <= 1)){
                //判断是否左孩子，以及父节点平衡因子+1后是否保持平衡，平衡则继续向上遍历，直至根节点
                //向上调节平衡因子
                parent.addFactor();
                prev = current;
                current = parent;
                if (parent.getFactor() == 0) {
                    //如果父节点的平衡因子为0，不需要再向上遍历，新增加节点不破坏平衡性
                    parent = null;
                } else {
                    //继续向上遍历
                    parent = parent.getParent();
                }
            } else if (parent.getRight() == current && (Math.abs(parent.getFactor()-1) <= 1)) {
                //判断是否右孩子，以及父节点平衡因子-1后是否保持平衡，平衡则继续向上遍历，直至根节点
                parent.subFactor();
                prev = current;
                current = parent;
                if (parent.getFactor() == 0) {
                    parent = null;
                } else {
                    parent = parent.getParent();
                }
            } else {
                //找到了不平衡节点
                bal = false;
            }
        }
        //递归到了根节点或者新增节点不破坏平衡，不需要调节平衡
        if (parent == null) return;
        //发现不平衡节点，根据不平衡情况进行旋转操作
        treeRotate(parent, current, prev);
    }

    /**
     * 判断节点删除后父节点是否平衡
     * @param current
     */
    private void isParentBalance(Node current) {
        Node parent = current.getParent();
        if (current == parent.getLeft()) {
            if (parent.getFactor() -1 < -1) {
                deleteBalance(current);
            } else {
                parent.subFactor();
            }
        } else if (current == parent.getRight()) {
            if (parent.getFactor() +1 > 1) {
                deleteBalance(current);
            } else {
                parent.addFactor();
            }
        }
    }


    /**
     * 删除操作时调节树的avl平衡因子
     * @param node
     */
    private void deleteBalance(Node node) {
        Node current = node;
        Node parent = node.getParent();
        Node prev = node;

        while (parent != null){
            prev = current;
            if (current == parent.getLeft() && parent.getFactor() - 1 >= -1) {
                parent.subFactor();
                if (parent.getFactor() == -1) {
                    return;
                }
                current = parent;
                parent = parent.getParent();
            } else if (current == parent.getRight() && parent.getFactor() + 1 <= 1) {
                parent.addFactor();
                if (parent.getFactor() == 1) {
                    return;
                }
                current = parent;
                parent = parent.getParent();
            } else {
                //删除后不平衡，需要进行旋转操作
                Node brother = node.getBrother();
                Node child;
                if (brother.getFactor() == 1) {
                    //LL或者RL
                    child = brother.getLeft();
                } else if (brother.getFactor() == -1) {
                    //RR或者LR
                    child = brother.getRight();
                } else if (parent.getFactor() == -1) {
                    //LL或者RL
                    child = brother.getLeft();
                } else {
                    //LR或者RR
                    child = brother.getRight();
                }
                treeRotate(parent, brother, child);
                current = parent;
                parent = parent.getParent();
            }
        }
    }

    /**
     * 分类讨论，判断LL，RR，LR，RL等情况进行对应旋转
     * @param parent
     * @param current
     * @param prev
     */
    private void treeRotate(Node parent, Node current, Node prev) {
        if (current == parent.getLeft() && prev == current.getLeft()) {
            //LL型
            leftLeftRotate(current);
        } else if (current == parent.getRight() && prev == current.getRight()) {
            //RR型
            rightRightRotate(current);
        } else if (current == parent.getLeft() && prev == current.getRight()) {
            //LR型
            leftRightRotate(current);
        } else if (current == parent.getRight() && prev == current.getLeft()) {
            rightLeftRotate(current);
        }
    }

    //LL旋转
    private void leftLeftRotate(Node current) {
        //传递进来的是不平衡节点的左孩子
        Node parent = current.getParent();

        setGrandParent(current, parent);
        parent.setParent(current);
        parent.setLeft(current.getRight());
        if (current.getRight() != null) {
            current.getRight().setParent(parent);
        }
        current.setRight(parent);

        //LL旋转后原不平衡节点及其左孩子平衡因子均为0，祖父节点以上不变，旋转后树高度不变
        current.setFactor(0);
        parent.setFactor(0);
    }

    //RR旋转
    private void rightRightRotate(Node current) {
        //传进来的是不平衡节点的右孩子
        Node parent = current.getParent();
        setGrandParent(current, parent);
        parent.setParent(current);
        parent.setRight(current.getLeft());
        if (current.getLeft() != null) {
            current.getLeft().setParent(parent);
        }
        current.setLeft(parent);

        //RR旋转后原不平衡节点及其右孩子的平衡因子均为0，祖父节点不变，树高度不变
        current.setFactor(0);
        parent.setFactor(0);
    }

    //LR旋转
    private void leftRightRotate(Node current) {
        Node parent = current.getParent();
        Node child = current.getRight();

        //current的平衡因子经过balance函数，肯定为1，只需要查看child的平衡因子
        if (!child.hasChild()) {
            parent.setFactor(0);
            current.setFactor(0);
        } else if (child.getFactor() == 1) {
            //有左孩子的情况，直接计算旋转后的平衡因子
            parent.setFactor(-1);
            current.setFactor(0);
        } else if (child.getFactor() == -1) {
            //有右孩子的情况，直接计算旋转后的平衡因子
            parent.setFactor(0);
            current.setFactor(1);
        }

        //第一次左旋
        current.setRight(child.getLeft());
        if (child.getLeft() != null) {
            child.getLeft().setParent(current);
        }
        current.setParent(child);
        child.setLeft(current);
        child.setParent(parent);
        parent.setLeft(child);

        //第二次右旋
        setGrandParent(child, parent);
        parent.setParent(child);
        parent.setLeft(child.getRight());
        if (child.getRight() != null) {
            child.getRight().setParent(parent);
        }
        child.setRight(parent);
    }

    //RL旋转
    private void rightLeftRotate(Node current) {
        Node parent = current.getParent();
        Node child = current.getLeft();

        //current的平衡因子经过balance函数，肯定为1，只需要查看child的平衡因子
        if (!child.hasChild()) {
            parent.setFactor(0);
            current.setFactor(0);
        } else if (child.getFactor() == 1) {
            //有左孩子的情况，直接计算旋转后的平衡因子
            parent.setFactor(0);
            current.setFactor(-1);
        } else if (child.getFactor() == -1) {
            //有右孩子的情况，直接计算旋转后的平衡因子
            parent.setFactor(1);
            current.setFactor(0);
        }

        //第一次右旋
        child.setParent(parent);
        parent.setRight(child);
        current.setLeft(child.getRight());
        if (child.getRight()!=null) {
            child.getRight().setParent(current);
        }
        current.setParent(child);
        child.setRight(current);

        //第二次左旋
        setGrandParent(child, parent);
        parent.setRight(child.getLeft());
        if (child.getLeft() != null) {
            child.getLeft().setParent(parent);
        }
        parent.setParent(child);
        child.setLeft(parent);
    }

    private void setGrandParent(Node current, Node parent) {
        if (parent.getParent() != null && parent.getParent().getLeft() == parent) {
            //父节点是祖父节点的左孩子
            current.setParent(parent.getParent());
            parent.getParent().setLeft(current);
        } else if (parent.getParent() != null && parent.getParent().getRight() == parent) {
            //父节点是祖父节点的右孩子
            current.setParent(parent.getParent());
            parent.getParent().setRight(current);
        } else {
            //父节点是根节点
            this.root = current;
            current.setParent(null);
        }
    }

    private static void writeArray(Node currNode, int rowIndex, int columnIndex, String[][] res, int treeDepth) {
        // 保证输入的树不为空
        if (currNode == null) return;
        // 先将当前节点保存到二维数组中
        res[rowIndex][columnIndex] = String.valueOf(currNode.getValue());

        // 计算当前位于树的第几层
        int currLevel = ((rowIndex + 1) / 2);
        // 若到了最后一层，则返回
        if (currLevel == treeDepth) return;
        // 计算当前行到下一行，每个元素之间的间隔（下一行的列索引与当前元素的列索引之间的间隔）
        int gap = treeDepth - currLevel - 1;

        // 对左儿子进行判断，若有左儿子，则记录相应的"/"与左儿子的值
        if (currNode.getLeft() != null) {
            res[rowIndex + 1][columnIndex - gap] = "/";
            writeArray(currNode.getLeft(), rowIndex + 2, columnIndex - gap * 2, res, treeDepth);
        }

        // 对右儿子进行判断，若有右儿子，则记录相应的"\"与右儿子的值
        if (currNode.getRight() != null) {
            res[rowIndex + 1][columnIndex + gap] = "\\";
            writeArray(currNode.getRight(), rowIndex + 2, columnIndex + gap * 2, res, treeDepth);
        }
    }


    /**
     * 打印整棵树
     */
    public void show() {
        if (root == null) System.out.println("EMPTY!");
        // 得到树的深度
        int treeDepth = root.height();

        // 最后一行的宽度为2的（n - 1）次方乘3，再加1
        // 作为整个二维数组的宽度
        int arrayHeight = treeDepth * 2 - 1;
        int arrayWidth = (2 << (treeDepth - 2)) * 3 + 1;
        // 用一个字符串数组来存储每个位置应显示的元素
        String[][] res = new String[arrayHeight][arrayWidth];
        // 对数组进行初始化，默认为一个空格
        for (int i = 0; i < arrayHeight; i ++) {
            for (int j = 0; j < arrayWidth; j ++) {
                res[i][j] = " ";
            }
        }

        // 从根节点开始，递归处理整个树
        // res[0][(arrayWidth + 1)/ 2] = (char)(root.val + '0');
        writeArray(root, 0, arrayWidth/ 2, res, treeDepth);

        // 此时，已经将所有需要显示的元素储存到了二维数组中，将其拼接并打印即可
        for (String[] line: res) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < line.length; i ++) {
                sb.append(line[i]);
                if (line[i].length() > 1 && i <= line.length - 1) {
                    i += line[i].length() > 4 ? 2: line[i].length() - 1;
                }
            }
            System.out.println(sb.toString());
        }
        System.out.println("******************************************************");
    }
}
