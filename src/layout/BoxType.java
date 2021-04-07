package layout;

import style.StyledNode;

public class BoxType {
    StyledNode styledNode;
    Type type;

    /**
     * 非匿名块级盒子所对应的 块级节点、内联节点
     */
    public BoxType(StyledNode styledNode, Type type) {
            this.styledNode = styledNode;
            this.type = type;
    }

    /**
     * 匿名块级盒子
     */
    public BoxType(Type type){
        this.type = type;
    }
}
