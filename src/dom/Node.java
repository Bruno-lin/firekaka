package dom;

import java.util.ArrayList;
import java.util.Map;

public class Node {
    //标签
    public String tag_name;
    // 所有节点共用数据:
    public ArrayList<Node> children;
    //每个节点类型的数据:
    public String node_type;
    //存储节点里选择器和值
    public Map<String, String> attrs;

    public Node() {

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return html_convert(this, sb, 0).toString();
    }

    private StringBuilder html_convert(Node node, StringBuilder sb, int num) {
        String indent = "  ";
        sb.append(indent.repeat(num)).append("<").append(node.tag_name);
        if (node.attrs != null) {
            for (Map.Entry<String, String> entry : node.attrs.entrySet()) {
                sb.append(" ").append(entry.getKey()).append("=").append("\"").append(entry.getValue()).append("\"");
            }
        }
        sb.append(">\n");
        for (Node no : node.children) {
            if (no.node_type.equals("text")) {
                sb.delete(sb.length() - 1, sb.length());
                sb.append(no.toString());
                num = 0;
                continue;
            }
            sb = html_convert(no, sb, num + 1);
        }
        sb.append(indent.repeat(num)).append("</").append(node.tag_name).append(">\n");
        return sb;
    }
}
