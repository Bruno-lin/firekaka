package painting;

import layout.Rect;

import java.awt.*;

/**
 * 开始绘制之前，我们要遍历布局树，构建一个显示列表。
 * 列表中是一系列的图形操作，如“画一个圆圈”、“画一个文本字符串”，等等。
 * 对于我们的项目而言，其实就是“画一个矩形”。
 */
public class DisplayCommand {
    public Color color;
    public Rect rect;

    /**
     * Robinson的显示列表是一个DisplayCommands向量。
     * 目前只有一个类型的DisplayCommands，即一个纯色的矩形。
     */
    public DisplayCommand(Color color, Rect rect){
        this.color = color;
        this.rect = rect;
    }
}
