import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DeBruijnGraph
 * 2020/8/6 9:04
 * De Bruijn Graph的节点
 *
 * @author Chan
 * @since
 **/
public class Node {
    /**
     * km1er字符串
     */
    public String km1mer;
    /**
     * 节点的入度
     */
    private AtomicInteger inDegree;
    /**
     * 节点的出度
     */
    private AtomicInteger outDegree;

    public Node(String km1mer) {
        this.km1mer = km1mer;
        this.inDegree = new AtomicInteger(0);
        this.outDegree = new AtomicInteger(0);
    }

    public int getInDegree() {
        return this.inDegree.get();
    }

    public int getOutDegree() {
        return this.outDegree.get();
    }

    public void setInDegree(int num) {
        this.inDegree.set(num);
    }

    public void setOutDegree(int num) {
        this.outDegree.set(num);
    }

    public void addInDegree() {
        synchronized (inDegree) {
            this.inDegree.incrementAndGet();
        }
    }

    public void addOutDegree() {
        synchronized (outDegree) {
            this.outDegree.incrementAndGet();
        }
    }

    public boolean isSemiBalanced() {
        return Math.abs(this.inDegree.get() - this.outDegree.get()) == 1;
    }

    public boolean isBalanced() {
        return this.inDegree.get() == this.outDegree.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(km1mer, node.km1mer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(km1mer);
    }

    @Override
    public String toString() {
        return this.km1mer;
    }
}
