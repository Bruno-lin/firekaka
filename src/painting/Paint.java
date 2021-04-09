package painting;

import layout.Dimensions;
import layout.LayoutBox;
import layout.Rect;
import layout.Type;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Paint {
    /**
     * 为了构建显示列表，我们需要遍历布局树，为每个盒子生成对应的图形绘制命令。
     * 我们先绘制盒子的背景，然后在背景上绘制边框和内容。
     */
    public ArrayList<DisplayCommand> build_display_list(LayoutBox layout_root) {
        ArrayList<DisplayCommand> list = new ArrayList<>();
        render_layout_box(list, layout_root);
        return list;
    }

    public void render_layout_box(ArrayList<DisplayCommand> list, LayoutBox layout_box) {
        render_background(list, layout_box);
        render_borders(list, layout_box);
        // TODO: 渲染文字
        for (LayoutBox child : layout_box.children) {
            render_layout_box(list, child);
        }
    }

    /**
     * 默认情况下，同一位置有多个HTML元素的话（如子节点在父容器里面），需要按照它们出现的顺序叠放起来——如果两个元素有重叠，后面的绘制时就会（部分）遮住前面的。
     * 我们的显示列表恰好满足这个条件，因为列表里元素的前后顺序就是它们在DOM树中出现的顺序。
     * 如果还要支持z-index属性的话，元素显示顺序可能会变化，我们就得相应地重新对列表进行排序。
     * 背景绘制比较简单，就是一个实心矩形。如果没指定背景色，背景就是透明的，也就不需要专门生成一个绘图命令了。
     */
    public void render_background(ArrayList<DisplayCommand> list, LayoutBox layout_box) {
        Color color = get_color(layout_box, "background");
        if (color == null) {
            return;
        }
        list.add(new DisplayCommand(color, layout_box.dimensions.border_box()));
    }

    /**
     * 返回CSS的`name`属性所对应的颜色，这里的`name`变量一般是`background`、`border-color`之类的属性
     */
    public Color get_color(LayoutBox layout_box, String name) {
        Type type = layout_box.boxType.type;
        switch (type) {
            case BlockNode:
            case InlineNode:
                if (!layout_box.boxType.styledNode.getAttValue(name).toString().equals("none")) {
                    return string_to_color(layout_box.boxType.styledNode.getAttValue(name).toString());
                } else {
                    break;
                }
            default:
                break;
        }
        return null;
    }

    private Color string_to_color(String s) {

        int r = Integer.parseInt(s.substring(1, 3), 16);
        int g = Integer.parseInt(s.substring(3, 5), 16);
        int b = Integer.parseInt(s.substring(5, 7), 16);
        return new Color(r, g, b);
    }

    /**
     * 提取border颜色和大小、位置，加入到显示列表
     */
    public void render_borders(ArrayList<DisplayCommand> list, LayoutBox layout_box) {
        Color color = get_color(layout_box, "border-color");
        if (color == null) {
            // border没颜色的话直接返回即可
            return;
        }

        Dimensions d = layout_box.dimensions;
        Rect border_box = d.border_box();
        // 左边框
        Rect left_border = new Rect(border_box.x, border_box.y, d.border.left, border_box.height);
        list.add(new DisplayCommand(color, left_border));

        // 右边框
        Rect right_border = new Rect(border_box.x + border_box.width - d.border.right, border_box.y, d.border.right, border_box.height);
        list.add(new DisplayCommand(color, right_border));

        // 顶边框
        Rect top_border = new Rect(border_box.x, border_box.y, border_box.width, d.border.top);
        list.add(new DisplayCommand(color, top_border));

        // 底边框
        Rect bottom_border = new Rect(border_box.x, border_box.y + border_box.height - d.border.bottom, border_box.width, d.border.bottom);
        list.add(new DisplayCommand(color, bottom_border));
    }

    /**
     * 先构建显示列表，然后将其栅格化，返回一个画布,再创建图片
     */
    public BufferedImage paint(LayoutBox layout_root, int canvas_width, int canvas_height) {
        ArrayList<DisplayCommand> display_list = build_display_list(layout_root);
        Canvas canvas = new Canvas(canvas_width, canvas_height);
        for (DisplayCommand item : display_list) {
            canvas.paint_item(item);
        }

        // 创建图片

        BufferedImage image = new BufferedImage(canvas_width, canvas_height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < canvas_height; y++) {
            for (int x = 0; x < canvas_width; x++) {
                Color color = canvas.pixels.get(x + y * canvas_width);
                image.setRGB(x, y, color.getRGB());
            }
        }
        return image;
    }
}
