import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * DeBruijnGraph
 * 2020/8/11 8:29
 * debruijngraph
 *
 * @author Chan
 * @since
 **/
public class DeBruijnGraph {
    /**
     * 线程数
     */
    public static final int THREAD_NUM = 10;

    /**
     * 哈希表，存放DBG的边
     */
    private ConcurrentHashMap<Node, Node> g;
    /**
     * 哈希表存放所有km1er的节点
     */
    private ConcurrentHashMap<String, Future<Node>> nodes;
    /**
     * 生产者队列，读取reads打包成List共消费者切分
     */
    private BlockingQueue<List<String>> queue = new LinkedBlockingQueue<>(20);

    /**
     * Kmer的k值
     */
    private int k;
    /**
     * 生产者是否已经消费完
     */
    private boolean readsEnd = false;
    /**
     * 头尾节点
     */
    private Node head;
    private Node tail;
    /**
     * 半平衡节点数量
     */
    private int nSemi = 0;
    /**
     * 平衡节点数量
     */
    private int nBal = 0;
    /**
     * 既不是半平衡也不是平衡节点数量
     */
    private int nNeither = 0;

    /**
     *初始化DBG，开启生产者主线程，读取文件
     * 开启消费者线程，构造dbg
     * @param fileName 读取reads文件
     * @param k kmer大小
     * @throws IOException
     */
    public DeBruijnGraph(String fileName, int k) throws IOException {
        this.k = k;
        this.g = new ConcurrentHashMap<>();
        this.nodes = new ConcurrentHashMap<>();

        Producer producer = new Producer(this, fileName);
        producer.start();

        for (int i = 0; i < THREAD_NUM; i++) {
            Consumer c = new Consumer(this, this.k);
            c.start();
            if (i == THREAD_NUM-1) {
                try {
                    c.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        this.count();
    }

    /**
     * 计算DBG中的平衡节点，半平衡节点以及其他节点
     */
    public void count() {
        this.nBal = 0;
        this.nSemi = 0;
        this.nNeither = 0;
        for (Future<Node> future:nodes.values()) {
            Node node = null;
            try {
                node = future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (node.isBalanced()) {
                this.nBal ++;
            } else if (node.isSemiBalanced()) {
                if (node.getInDegree() == node.getOutDegree() + 1) {
                    this.tail = node;
                } else if (node.getOutDegree() == node.getInDegree() + 1) {
                    this.head = node;
                }
                this.nSemi ++;
            } else {
                this.nNeither ++;
            }
        }
    }

    //获取节点数量
    public int nodesNum() {
        return this.nodes.size();
    }

    //获取边的数量
    public int edgesNum() {
        return this.g.size();
    }

    //判断是否有欧拉路径
    public boolean hasEulerianPath() {
        return this.nNeither == 0 && this.nSemi == 2;
    }

    //判断是不是欧拉环路，一个圆圈
    public boolean hasEulerianCycle() {
        return this.nNeither == 0 && this.nSemi == 0;
    }

    //判断是不是欧拉性质的
    public boolean isEulerian() {
        return this.hasEulerianPath() || this.hasEulerianCycle();
    }

    public String getEulerianPath() throws Exception {
        if (!this.isEulerian()) {
            throw new Exception("not eulerian");
        }
        if (this.hasEulerianPath()) {
            if (this.head == null || this.tail == null) {
                throw new Exception("head is none or tail is none");
            }
        }

        StringBuilder tour = new StringBuilder();
        Node src = this.head;
        //遍历DBG
        visit(tour, src);
//        System.out.println("map size:" + this.g.size());
        return tour.toString();
    }

    /**
     * dfs遍历dbg
     * @param tour 生成拼接字符串
     * @param n 当前节点
     */
    public void visit(StringBuilder tour, Node n) {
        Node next = this.g.get(n);
        tour.append(n.toString());
        while (next != null) {
            tour.append(next.toString().substring(k-2));
            next = this.g.get(next);
        }
    }

    /**
     * 使用proxy对象来包装真正的node对象，解决多线程重复new对象问题
     * @param km1mer
     * @return
     */
    public Node getNode(String km1mer) {
        Future<Node> future = this.nodes.get(km1mer);
        if (future == null) {
            Callable<Node> callable = new Callable<Node>() {
                @Override
                public Node call() throws Exception {
                    return new Node(km1mer);
                }
            };
            FutureTask<Node> task = new FutureTask<>(callable);

            future = this.nodes.putIfAbsent(km1mer, task);
            //当nodes哈希表中含有km1mer节点时，future = null
            if (future == null) {
                future = task;
                task.run();
            }
        }

        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static String chop(String str, int i, int k) {
        return str.substring(i, i+k);
    }

    public ConcurrentHashMap<Node, Node> getG() {
        return g;
    }

    public void setG(ConcurrentHashMap<Node, Node> g) {
        this.g = g;
    }

    public ConcurrentHashMap<String, Future<Node>> getNodes() {
        return nodes;
    }

    public void setNodes(ConcurrentHashMap<String, Future<Node>> nodes) {
        this.nodes = nodes;
    }

    public boolean isReadsEnd() {
        return readsEnd;
    }

    public void setReadsEnd(boolean readsEnd) {
        this.readsEnd = readsEnd;
    }

    /**
     * 生产者线程
     */
    class Producer extends Thread {
        DeBruijnGraph dbg;
        String fileName;

        public Producer(DeBruijnGraph dbg, String fileName) {
            this.dbg = dbg;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            producer();
        }

        /**
         * 生产者生产reads List,1000条打包一次
         */
        private void producer() {
            File file = new File(this.fileName);
            FileReader fr = null;
            BufferedReader br = null;

            try {
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                String str = null;
                System.out.print("已处理*百万条：");
                List<String> strList = new ArrayList<>();
                int count = 0;
                while((str = br.readLine()) != null) {
                    count ++;
                    strList.add(str);
                    try {
                        if (count % 1000 == 0) {
                            queue.put(strList);
                            strList = new ArrayList<>();
                            if (count % 1000000 == 0) {
                                System.out.print(count/1000000 + "-");
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                this.dbg.setReadsEnd(true);
                System.out.println("\nreads num:" + count);
                br.close();
                fr.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 消费者线程，取走队列中的数据，拼接dbg
     */
    class Consumer extends Thread {
        private DeBruijnGraph dbg;
        private int k;

        public Consumer(DeBruijnGraph dbg, int k) {
            this.dbg = dbg;
            this.k = k;
        }


        @Override
        public void run() {
            consumer();
        }

        private void consumer() {
            while (true) {
                try {
                    List<String> readsList = queue.take();
                    ConcurrentHashMap<Node, Node> graph = this.dbg.getG();
                    //根据reads将节点插入dbg图
                    for (int strIndex = 0; strIndex < readsList.size(); strIndex++) {
                        String str = readsList.get(strIndex);
                        for (int i = 0; i < str.length()-k+1; i++) {
                            String kmer = this.dbg.chop(str, i, k);
                            String km1L = kmer.substring(0, k-1);
                            String km1R = kmer.substring(1);
                            Node nodeL, nodeR;
                            nodeL = this.dbg.getNode(km1L);
                            nodeR = this.dbg.getNode(km1R);
                            if (!graph.containsKey(nodeL)) {
                                nodeL.setOutDegree(1);
                                nodeR.setInDegree(1);
                                graph.put(nodeL, nodeR);
                            }

                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (queue.size() == 0 && this.dbg.isReadsEnd()) {
                    break;
                }
            }
        }
    }

}
