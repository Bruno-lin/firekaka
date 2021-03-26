package dom;

import java.util.List;

public class Node {
    // 所有节点共用数据:
    List<String> children;

    //每个节点类型的数据:
    TextNode node_type;

    public Node() {
    }
}
