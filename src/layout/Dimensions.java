package layout;

public class Dimensions {
    public Rect content;
    public EdgeSizes padding;
    public EdgeSizes border;
    public EdgeSizes margin;

    public Dimensions() {
        content = new Rect();
        padding = new EdgeSizes();
        border = new EdgeSizes();
        margin = new EdgeSizes();
    }
    // 内容区域加内边距（padding）
    public Rect padding_box() {
        return content.expanded_by(padding);
    }
    // 内容区域加内边距（padding）和边框（border）
    public Rect border_box() {
        return padding_box().expanded_by(border);
    }
    // 内容区域加内边距（padding）、边框（border）、外边距（margin）
    public Rect margin_box() {
        return border_box().expanded_by(margin);
    }
}
