package layout;

public class Rect {
    public int x;
    public int y;
    public int width;
    public int height;

    public Rect() {
    }

    public Rect(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rect expanded_by(EdgeSizes edge) {
        Rect rect = new Rect(x - edge.left,y - edge.top,
                width + edge.left + edge.right,height + edge.top + edge.bottom);
        return rect;
    }
}
