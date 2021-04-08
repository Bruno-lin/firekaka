package layout;

import css.Value;
import style.StyledNode;

import java.util.*;

public class LayoutBox {

    public Dimensions dimensions;
    public BoxType boxType;
    public List<LayoutBox> children;
    int rootWidth = -1;

    public LayoutBox() {
    }

    //构造布局树
    public LayoutBox(StyledNode styledNode) {

        this.boxType = new BoxType(styledNode, styledNode.display());
        children = new ArrayList<>();
        dimensions = new Dimensions();

        for (StyledNode child : styledNode.children) {
            switch (child.display()) {
                case AnonymousBlock:
                case BlockNode:
                    children.add(new LayoutBox(child));
                    break;
                case InlineNode:
                    Objects.requireNonNull(getInlineContainer()).children.add(new LayoutBox(child));
                    break;
            }
        }
    }

    public void layoutTree(int i) {
        rootWidth = i;
        layout(null);
    }

    /**
     *
     * @return 根据Boxtype来获取容器
     */
    private LayoutBox getInlineContainer() {

        switch (boxType.type) {
            case InlineNode:
            case AnonymousBlock:
                return this;
            case BlockNode:
                //若没有创建过匿名Box
                if (children.get(children.size() - 1).boxType.type != Type.AnonymousBlock) {
                    LayoutBox anonymousBlock = new LayoutBox();
                    anonymousBlock.boxType.type = Type.AnonymousBlock;
                    children.add(anonymousBlock);
                }
                return children.get(children.size() - 1);
        }
        return null;
    }

    /**
     * 对一个盒子和它的后代节点进行布局,块（block）的宽度取决于它的父节点，
     * 高度则取决于子节点。这意味着计算宽度时，我们需要自上而下地遍历树，这样才可以先算出父节点的宽度，
     * 然后再对子节点进行布局。计算高度时则要自下而上遍历树，这样计算完子节点的高度之后才会计算父节点的高度。
     *
     * @param containingBlock 为需要布局的盒子的容器（父盒子）
     */
    public void layout(LayoutBox containingBlock) {

        switch (boxType.type) {
            case BlockNode:
                layoutBlock(containingBlock);
                break;
            case InlineNode:
                layoutInline();
                break;
            case AnonymousBlock:
                break;
        }
    }

    public void layoutInline() {
    }

    public void layoutBlock(LayoutBox containingBlock) {
        // 由于子节点的宽度依赖于父节点，所以需要先计算出当前节点自身的宽度，然后再递归处理子节点
        calculateBlockWidth(containingBlock);
        // 计算盒子位置
        calculateBlockPosition(containingBlock);
        // 对子节点进行布局，本质上最终会递归调用
        layoutBlockChildren();
        // 布局完子节点之后，才能计算当前节点的高度
        calculateBlockHeight();
    }

    private void calculateBlockWidth(LayoutBox containingBlock) {

        // `width`默认是`auto`.
        Value width = boxType.lookup("width", "", "auto");

        // margin、border、padding的初始值都是0
        String default_ = "0px";
        Value marginLeft = boxType.lookup("margin-left", "margin", default_);
        Value marginRight = boxType.lookup("margin-right", "margin", default_);

        Value borderLeft = boxType.lookup("border-left-width", "border-width", default_);
        Value borderRight = boxType.lookup("border-right-width", "border-width", default_);

        Value paddingLeft = boxType.lookup("padding-left", "padding", default_);
        Value paddingRight = boxType.lookup("padding-right", "padding", default_);

        Value[] values = new Value[]{marginLeft, marginRight, borderLeft, borderRight, paddingLeft, paddingRight, width};
        int total = Arrays.stream(values).mapToInt(Value::to_px).sum();
        int pWidth;
        //根节点没有父类，可指定视图大小
        if (containingBlock == null) {
            pWidth = rootWidth;
        } else {
            pWidth = containingBlock.dimensions.content.width;
        }
        // 如果width不是auto，总和超过了块的容器宽度，则auto的margin等同于0
        if (!width.toString().equals("auto") && total > pWidth) {
            if (marginLeft.toString().equals("auto")) {
                marginLeft = new Value(default_);
            }
            if (marginRight.toString().equals("auto")) {
                marginRight = new Value(default_);
            }
        }

        int underFlow = pWidth - total;

        String match = "";
        if (width.toString().equals("auto")) {
            match += "1";
        } else {
            match += "0";
        }
        if (marginLeft.toString().equals("auto")) {
            match += "1";
        } else {
            match += "0";
        }
        if (marginRight.toString().equals("auto")) {
            match += "1";
        } else {
            match += "0";
        }

        switch (match) {
            // 都不是auto，调整右margin
            case "000":
                marginRight = new Value(marginRight.to_px() + underFlow + "px");
                break;
            // 有一个margin是auto，就调整那个margin
            case "001":
                marginRight = new Value(underFlow + "px");
                break;
            case "010":
                marginLeft = new Value(underFlow + "px");
                break;
            // 如果左右margin都是auto，按照左右margin相等的原则进行调整
            case "011":
                marginRight = new Value(underFlow / 2 + "px");
                marginLeft = new Value(underFlow / 2 + "px");
                break;
        }
        // width是auto，则其他的auto设置为0，然后主要调整width就够了
        if (match.charAt(0) == '1') {
            if (match.charAt(1) == '1') {
                marginLeft = new Value(default_);
            }
            if (match.charAt(2) == '1') {
                marginRight = new Value(default_);
            }
            // 下溢出，扩大width
            if (underFlow >= 0) {
                width = new Value(underFlow + "px");
            } else {
                // 上溢出，调整width。但width最多调整到0，不能为负。如果还不够的话，就要调整右margin。
                width = new Value(default_);
                marginRight = new Value(marginRight.to_px() + underFlow + "px");
            }
        }

        dimensions.content.width = width.to_px();
        dimensions.margin.left = marginLeft.to_px();
        dimensions.margin.right = marginRight.to_px();
        dimensions.padding.left = paddingLeft.to_px();
        dimensions.padding.right = paddingRight.to_px();
        dimensions.border.left = borderLeft.to_px();
        dimensions.border.right = borderRight.to_px();
    }

    /**
     * 读取剩余的margin/padding/border信息，并结合包含块（containing block）的大小，来计算当前块在页面中的位置。
     */
    private void calculateBlockPosition(LayoutBox containingBlock) {
        String default_ = "0px";
        // 对于margin-top和margin-bottom，如果设置成了`auto`，则用0替代
        dimensions.margin.top = boxType.lookup("margin-top", "margin", default_).to_px();
        dimensions.margin.bottom = boxType.lookup("margin-bottom", "margin", default_).to_px();

        dimensions.border.top = boxType.lookup("border-top-width", "border-width", default_).to_px();
        dimensions.border.bottom = boxType.lookup("border-bottom-width", "border-width", default_).to_px();

        dimensions.padding.top = boxType.lookup("padding-top", "padding", default_).to_px();
        dimensions.padding.bottom = boxType.lookup("padding-bottom", "padding", default_).to_px();

        int parentX;
        int parentH;
        int parentY;
        if (containingBlock == null) {
            parentX = 0;
            parentH = 0;
            parentY = 0;
        } else {
            parentX = containingBlock.dimensions.content.x;
            parentH = containingBlock.dimensions.content.height;
            parentY = containingBlock.dimensions.content.y;
        }
        dimensions.content.x = parentX + dimensions.margin.left + dimensions.border.left + dimensions.padding.left;
        // 把当前盒子的放在前面盒子的下面
        dimensions.content.y = parentH + parentY + dimensions.margin.top + dimensions.border.top + dimensions.padding.top;

    }

    /**
     * 递归地计算盒子内容的布局。遍历子盒子时，需要记录总的高度，从而可以在定位时（参考上一段代码）计算下一个子节点的y坐标。
     */
    private void layoutBlockChildren() {
        for (LayoutBox child : children) {
            child.layout(this);
            // 记录截止到这个子节点的总高度，从而可以计算下一个子节点的y坐标
            dimensions.content.height = dimensions.content.height + child.dimensions.margin_box().height;
        }
    }

    /**
     * 默认情况下，盒子的高度取决于里面的内容。不过如果盒子的height属性设置了一个指定的高度，则应该以这个高度为准
     */
    private void calculateBlockHeight() {
        if (!boxType.styledNode.getAttValue("height").toString().equals("none")) {
            dimensions.content.height = boxType.styledNode.getAttValue("height").to_px();
        }
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        return sout(this, stringBuilder, 0).toString();
    }

    private StringBuilder sout(LayoutBox layoutBox, StringBuilder stringBuilder, int num) {
        String indent = "  ";
        stringBuilder.append(indent.repeat(num)).append("<").append(layoutBox.boxType.styledNode.domNode.tag_name);
        String borderX = " borderX=\"" + layoutBox.dimensions.border_box().x + "\"";
        String contentY = " contentY=\"" + layoutBox.dimensions.content.y + "\"";
        stringBuilder.append(borderX).append(contentY).append(">\n");
        for (LayoutBox child : layoutBox.children) {
            stringBuilder = sout(child, stringBuilder, num + 1);
        }
        stringBuilder.append(indent.repeat(num)).append("</").append(layoutBox.boxType.styledNode.domNode.tag_name).append(">\n");
        return stringBuilder;
    }
}
