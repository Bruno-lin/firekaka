package dom;

import java.util.ArrayList;
import java.util.Map;

public class ElementNode extends Node {

    public ElementNode(String tag_name, Map<String,String> attrs, ArrayList<Node> children) {
        node_type = "element";
        this.tag_name = tag_name;
        this.attrs = attrs;
        this.children = children;
    }

    public ElementNode() {

    }
}
