package dom;

public class TextNode extends Node {
    String text;

    public TextNode(String text) {
        node_type = "text";
        this.tag_name = "text_";
        this.text = text;
    }

    public TextNode() {
        super();
    }

    @Override
    public String toString() {
        return text;
    }
}
