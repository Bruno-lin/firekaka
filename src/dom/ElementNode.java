package dom;

import java.util.*;
import java.util.stream.Collectors;

public class ElementNode extends Node {

    public ElementNode(String tag_name, Map<String,String> attrs, ArrayList<Node> children) {
        node_type = "element";
        this.tag_name = tag_name;
        this.attrs = attrs;
        this.children = children;
    }

    public String get_id() {
        String s = attrs.get("id");
        return Objects.requireNonNullElse(s, "");
    }

    public Set<String> get_class_array() {
        String className = Objects.requireNonNullElse(attrs.get("class"), "");
        return Arrays.stream(className.split(" ")).collect(Collectors.toSet());
    }
    public ElementNode() {
        super();
    }
}
