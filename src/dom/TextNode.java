package dom;

public class TextNode extends Node {
    String text;
    ElementNode elementNode;

    public TextNode(String text) {
        this.text = text;
    }

    public TextNode(){
    }
}