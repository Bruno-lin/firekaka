package painting;

import layout.Rect;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class Canvas {
    ArrayList<Color> pixels;
    int width;
    int height;

    /**
     * 显示列表现在已经构建好了，我们接下来要逐个执行DisplayCommand，将它们转换成像素。像素将被保存到画布(Canvas)里面。
     */
    public Canvas(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new ArrayList<>();
        IntStream.range(0, width * height).forEach(i -> pixels.add(new Color(255, 255, 255, 255)));
    }

    /**
     * 在画布上绘制矩形时，只需要对行和列循环遍历就行了。
     */

    public void paint_item(DisplayCommand item) {
        Color color = item.color;
        Rect rect = item.rect;

        // 如果过大，就对矩形进行裁剪
        int x0 = clamp(rect.x, 0, this.width);
        int y0 = clamp(rect.y, 0, this.height);
        int x1 = clamp(rect.x + rect.width, 0, this.width);
        int y1 = clamp(rect.y + rect.height, 0, this.height);

        for (int y = y0; y < y1; y++) {
            for (int x = x0; x < x1; x++) {
                // TODO: 添加对透明度的支持
                this.pixels.set(x + y * this.width, color);
            }
        }
    }

    //检查有没有超出画布边界, 如果过大，就对矩形进行裁剪
    public int clamp(int val, int min, int max) {
        if (val < min) val = min;
        if (val > max) val = max;
        return val;
    }
}
