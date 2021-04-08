package layout;

import css.Value;
import style.StyledNode;

public class BoxType {
    public StyledNode styledNode;
    public Type type;

    /**
     * 非匿名块级盒子所对应的 块级节点、内联节点
     */
    public BoxType(StyledNode styledNode, Type type) {
        this.styledNode = styledNode;
        assert type != null;
        this.type = type;
    }

    /**
     * 这个函数在CSS中先查询第一个参数所代表的属性.
     * 如果没找到就再查询第二个参数代表的属性，如果还没找到，就把第三个参数作为默认值返回。
     */

//    public Value lookup(String name, String fallback_name, Value option) {
////        System.out.println(specified_values);
//        if (specified_values.get(name) != null) {
//            return specified_values.get(name);
//        } else if (specified_values.get(fallback_name) != null) {
//            return specified_values.get(fallback_name);
//        } else {
//            return option;
//        }
//    }
    public Value lookup(String s1, String s2, String default_) {
        if (!styledNode.getAttValue(s1).toString().equals("none")) {
            return styledNode.getAttValue(s1);
        }
        if (!styledNode.getAttValue(s2).toString().equals("none")) {
            return styledNode.getAttValue(s2);
        }
        return new Value(default_);
    }
}
