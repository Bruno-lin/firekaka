package layout;

import style.Display;
import style.StyledNode;

import java.util.ArrayList;

public class LayoutBox {
    public Dimensions dimensions;
    public BoxType boxType;
    public ArrayList<LayoutBox> children;

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
    public LayoutBox build_layout_tree(StyledNode styledNode) {
        LayoutBox root = new LayoutBox(styledNode);

        for (StyledNode child : styledNode.children) {
            switch (child.display()) {
                case Block:
                    root.children.add(build_layout_tree(child));
                case Inline:
                    root.get_inline_container().children.add(build_layout_tree(child));
                default:
                    break;
            }
        }
        return root;
    }

    /**
     * 处理内联子节点
     */
    public LayoutBox get_inline_container() {
        switch (boxType.type) {
            case BlockNode:
                //如果已经创建过了匿名块级盒子，直接使用。否则创建一个新的匿名块级盒子。
                if (!children.get(children.size() - 1).boxType.type.equals(Type.AnonymousBlock)) {
                    LayoutBox anonymousBlock = new LayoutBox();
                    children.add(anonymousBlock);
                }
                return children.get(children.size() - 1);
            default:
                return this;
        }
    }
}
