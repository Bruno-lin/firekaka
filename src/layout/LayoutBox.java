package layout;

import css.Value;
import style.Display;
import style.StyledNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class LayoutBox {
    public Dimensions dimensions;
    public BoxType boxType;
    public ArrayList<LayoutBox> children;
    public int browser_width; //根节点窗口大小

    /**
     * 构造函数
     */
    public LayoutBox(StyledNode styledNode) {
        if (styledNode.display().equals(Display.Block)) {
            boxType = new BoxType(styledNode, Type.BlockNode);
        } else if (styledNode.display().equals(Display.Inline)) {
            boxType = new BoxType(styledNode, Type.InlineNode);
        } else {
            boxType = new BoxType(Type.AnonymousBlock);
        }
        dimensions = new Dimensions();
        children = new ArrayList<>();
    }

    /**
     * 构造函数
     */
    public LayoutBox() {
        boxType = new BoxType(Type.AnonymousBlock);
    }

    /**
     * 建立布局树
     */
    public LayoutBox build_layout_tree(LayoutBox box) {
        StyledNode node = box.boxType.styledNode;

        for (StyledNode child : node.children) {
            switch (child.display()) {
                case Block:
                    box.children.add(build_layout_tree(new LayoutBox(child)));
                    break;
                case Inline:
                    box.get_inline_container().children.add(build_layout_tree(new LayoutBox(child)));
                    break;
                default:
                    break;
            }
        }
        return box;
    }

    /**
     * 处理内联（inline）子节点
     */
    public LayoutBox get_inline_container() {
        if (boxType.type.equals(Type.BlockNode)) {
            //如果已经创建过了匿名块级盒子，直接使用。否则创建一个新的匿名块级盒子。
            if (!children.get(children.size() - 1).boxType.type.equals(Type.AnonymousBlock)) {
                LayoutBox anonymousBlock = new LayoutBox();
                children.add(anonymousBlock);
            }
            return children.get(children.size() - 1);
        }
        return this;
    }

    /**
     * 块（block）的宽度取决于它的父节点，高度则取决于子节点。
     * 这意味着计算宽度时，我们需要自上而下地遍历树，这样才可以先算出父节点的宽度，然后再对子节点进行布局。
     * 计算高度时则要自下而上遍历树，这样计算完子节点的高度之后才会计算父节点的高度。
     */
    public void layout_block(LayoutBox containing_block) {
        // 由于子节点的宽度依赖于父节点，所以需要先计算出当前节点自身的宽度，然后再递归处理子节点
        calculate_block_width(containing_block);

        // 计算盒子位置
        calculate_block_position(containing_block);

        // 对子节点进行布局，本质上最终会递归调用
        layout_block_children();

        // 布局完子节点之后，才能计算当前节点的高度
        calculate_block_height();
    }

    /**
     * CSS的width属性和左右边距、边框的值：
     */
    public void calculate_block_width(LayoutBox containing_block) {
        StyledNode style = boxType.styledNode;
        Value zero = new Value("0px");
        Value auto = new Value("auto");

        // `width`默认是`auto`
        Value width = style.lookup("width", "", auto);

        // margin、border、padding的初始值都是0
        Value margin_left = style.lookup("margin-left", "margin", zero);
        Value margin_right = style.lookup("margin-right", "margin", zero);

        Value border_left = style.lookup("border-left-width", "border-width", zero);
        Value border_right = style.lookup("border-right-width", "border-width", zero);

        Value padding_left = style.lookup("padding-left", "padding", zero);
        Value padding_right = style.lookup("padding-right", "padding", zero);

        //将margin、padding、border以及内容区域的宽度加总
        Value[] array_total = new Value[]{margin_left, margin_right, border_left, border_right,
                padding_left, padding_right, width};
        int total = Arrays.stream(array_total).mapToInt(Value::to_px).sum();
        //根节点没有父类，可指定视图大小
        int node_width;
        if (containing_block == null) {
            node_width = browser_width;
        } else {
            node_width = containing_block.dimensions.content.width;
        }
        // 如果width不是auto，总和超过了块的容器宽度，则auto的margin等同于0
        if (!width.equals(auto) && total > node_width) {
            if (margin_left == auto) {
                margin_left = zero;
            }
            if (margin_right == auto) {
                margin_right = zero;
            }
        }
        //比容器小时，会留下一些空间，则称为下溢出（underflow）
        int underflow = node_width - total;

        //调整盒子的大小，来消除上溢和下溢。如果没有属性是auto的，则调整右margin。（这意味着发生溢出时，margin可会被调整为负值）
        boolean bool_1 = width.equals(auto);
        boolean bool_2 = margin_left.equals(auto);
        boolean bool_3 = margin_right.equals(auto);

        // 都不是auto，调整右margin
        if (!bool_1 && !bool_2 && !bool_3) {
            margin_right = new Value((margin_right.to_px() + underflow) + "px");
        } else if (!bool_1 && !bool_2) {
            // 有一个margin是auto，就调整那个margin
            margin_right = new Value(underflow + "px");
        } else if (!bool_1 && !bool_3) {
            margin_left = new Value(underflow + "px");
        } else if (bool_1) {
            // width是auto，则其他的auto设置为0，然后主要调整width就够了
            if (bool_2) {
                margin_left = zero;
            } else if (bool_3) {
                margin_right = zero;
            } else if (underflow >= 0) {
                // 下溢出，扩大width
                width = new Value(underflow + "px");
            } else {
                // 上溢出，调整width。但width最多调整到0，不能为负。如果还不够的话，就要调整右margin。
                width = zero;
                margin_right = new Value((margin_right.to_px() + underflow) + "px");
            }
        } else {
            // 如果左右margin都是auto，按照左右margin相等的原则进行调整
            margin_left = new Value(underflow / 2 + "px");
            margin_right = new Value(underflow / 2 + "px");
        }
        Dimensions d = dimensions;
        d.content.width = width.to_px();

        d.padding.left = padding_left.to_px();
        d.padding.right = padding_right.to_px();

        d.border.left = border_left.to_px();
        d.border.right = border_right.to_px();

        d.margin.left = margin_left.to_px();
        d.margin.right = margin_right.to_px();
    }

    /**
     * 读取剩余的margin/padding/border信息，并结合包含块（containing block）的大小，来计算当前块在页面中的位置。
     */
    private void calculate_block_position(LayoutBox containing_block) {
        StyledNode style = boxType.styledNode;
        Dimensions d = dimensions;
        Value zero = new Value("0px");

        // margin、border、padding的初始值都是0
        // 对于margin-top和margin-bottom，如果设置成了`auto`，则用0替代
        d.margin.top = style.lookup("margin-top", "margin", zero).to_px();
        d.margin.bottom = style.lookup("margin-bottom", "margin", zero).to_px();

        d.border.top = style.lookup("border-top-width", "border-width", zero).to_px();
        d.border.bottom = style.lookup("border-bottom-width", "border-width", zero).to_px();

        d.padding.top = style.lookup("padding-top", "padding", zero).to_px();
        d.padding.bottom = style.lookup("padding-bottom", "padding", zero).to_px();

        d.content.x = containing_block.dimensions.content.x +
                d.margin.left + d.border.left + d.padding.left;
        // 把当前盒子的放在前面盒子的下面
        d.content.y = containing_block.dimensions.content.height + containing_block.dimensions.content.y +
                d.margin.top + d.border.top + d.padding.top;
    }

    /**
     * 递归地计算盒子内容的布局。遍历子盒子时，需要记录总的高度，从而可以在定位时（参考上一段代码）计算下一个子节点的y坐标。
     */
    public void layout_block_children() {
        Dimensions d = dimensions;
        for (LayoutBox child : children) {
            child.layout(this);
            // 记录截止到这个子节点的总高度，从而可以计算下一个子节点的y坐标
            d.content.height = d.content.height + child.dimensions.margin_box().height;
        }
    }

    /**
     * 默认情况下，盒子的高度取决于里面的内容。不过如果盒子的height属性设置了一个指定的高度，则应该以这个高度为准
     */
    public void calculate_block_height() {
        Map<String, Value> style_attr = boxType.styledNode.specified_values;
        if (style_attr.get("height") != null) {
            dimensions.content.height = style_attr.get("height").to_px();
        }
    }

    /**
     * 对一个盒子和它的后代节点进行布局
     */
    public void layout(LayoutBox containing_block) {
        // 块节点
        if (boxType.type == Type.BlockNode) {
            layout_block(containing_block);
        }
    }


    /****************************************************************************************
     * layoutTree(int viewportWidth)函数会以viewportWidth为窗口宽度，计算HTML/CSS所生成的布局。
     * 其中，如果用户没有特别指明，根节点margin、border、padding默认全部为0,即根节点的内容区域
     * （content area）对齐窗口左上角。
     ****************************************************************************************/
    public void layoutTree(int viewportWidth) {
        browser_width = viewportWidth;
        build_layout_tree(this);
        layout(this);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return output(this, sb, 0).toString();
    }


//    @Override
//    public String toString() {
//        return "LayoutBox{" +
//                "dimensions=" + dimensions +
//                ", boxType=" + boxType +
//                ", children=" + children +
//                '}';
//    }

    private StringBuilder output(LayoutBox layoutBox, StringBuilder stringBuilder, int num) {
        String indent = "  ";
        stringBuilder.append(indent.repeat(num)).append("<").append(layoutBox.boxType.styledNode.domNode.tag_name);
        String borderX = " borderX=\"" + layoutBox.dimensions.border_box().x + "\"";
        String contentY = " contentY=\"" + layoutBox.dimensions.content.y + "\"";
        stringBuilder.append(borderX).append(contentY).append(">\n");
        for (LayoutBox child : layoutBox.children) {
            stringBuilder = output(child, stringBuilder, num + 1);
        }
        stringBuilder.append(indent.repeat(num)).append("</").append(layoutBox.boxType.styledNode.domNode.tag_name).append(">\n");
        return stringBuilder;
    }
}
