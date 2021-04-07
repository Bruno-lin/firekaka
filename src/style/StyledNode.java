package style;

import css.*;
import dom.ElementNode;
import dom.Node;

import java.util.*;

public class StyledNode {

    public Node domNode;
    public Map<String, Value> specified_values;
    public ArrayList<StyledNode> children;

    /**
     * 设置整个DOM树的样式，返回一棵StyledNode树
     */
    public StyledNode(Node domNode, Stylesheet stylesheet) {
        Map<String, Value> specified_values;
        if (domNode.node_type.equals("element")) {
            specified_values = specified_values((ElementNode) domNode, stylesheet);
        } else {
            specified_values = new LinkedHashMap<>();
        }
        ArrayList<StyledNode> children = new ArrayList<>();
        if (domNode.children != null) {
            for (Node child : domNode.children) {
                children.add(new StyledNode(child, stylesheet));
            }
        }
        this.domNode = domNode;
        this.specified_values = specified_values;
        this.children = children;
    }

    /**
     * 元素节点与选择器是否匹配
     */

    public boolean matches(ElementNode elementNode, Selector selector) {
        if (!elementNode.tag_name.equals(selector.tag_name) ) {
            if (selector.tag_name.equals("") && !elementNode.get_class_array().isEmpty() && !selector.class_array.isEmpty()
                    && elementNode.get_class_array().containsAll(selector.class_array) ) {
                return true;
            }else if (selector.tag_name.equals("") && !elementNode.get_id().equals("") && !selector.id.equals("")
                    && elementNode.get_id().equals(selector.id)){
                return true;
            }
                return false;
        }else if (!elementNode.get_id().equals(selector.id) && !selector.id.equals("")) {
            return false;
        }else if (selector.class_array.size() == 0) {
            return true;
        }else {
            for (String s : selector.class_array) {
                if (!elementNode.get_class_array().contains(s)) {
                    return false;
                }
            }
            return true;
        }
//        if (!elementNode.tag_name.equals(selector.tag_name) && !selector.tag_name.equals("*") && !selector.tag_name.equals("")) {
//            return false;
//        }
//        if (!elementNode.get_id().equals(selector.id) && !selector.id.equals("")) {
//            return false;
//        }
//        if (selector.class_array.size() == 0) {
//            return true;
//        }
//        for (String s : selector.class_array) {
//            if (!elementNode.get_class_array().contains(s)) {
//                return false;
//            }
//        }
//        return true;
    }

    /**
     * 对于树中的当前元素，我们都需要在样式表中搜索匹配的规则。
     */
    public MatchedRule match_rule(ElementNode elementNode, Rule rule) {
        //将rule的每一个选择器都与selector配对
        for (Selector selector : rule.getSelectors()) {
            // 找到第一个（即优先级最高的）选择器

            if (matches(elementNode, selector)) {
//                System.out.println("--1--");
//                System.out.println(elementNode.tag_name);
//                System.out.println(elementNode.get_class_array());
//                System.out.println("--2--");
//                System.out.println(selector.tag_name);
//                System.out.println(selector.class_array);

                MatchedRule matchedRule = new MatchedRule(selector.specificity(), rule);
                return matchedRule;
            }
        }
        return null;
    }

    /**
     * 从样式表中搜索当前元素节点的匹配规则
     */
    public ArrayList<MatchedRule> matching_rules(ElementNode elementNode, Stylesheet stylesheet) {
        ArrayList<MatchedRule> specificities = new ArrayList<>();
        for (Rule rule : stylesheet.getRules()) {
            MatchedRule specificity = match_rule(elementNode, rule);
            if (specificity != null) {
                specificities.add(specificity);
            }
        }
        return specificities;
    }

    /**
     * 计算一个元素的样式指定值，用一个HashMap返回
     */
    public Map<String, Value> specified_values(ElementNode elementNode, Stylesheet stylesheet) {
        Map<String, Value> values = new LinkedHashMap<>();
        ArrayList<MatchedRule> rules = matching_rules(elementNode, stylesheet);
        //按照优先级从低到高遍历
        rules.sort(Comparator.comparingInt(matchedRule -> matchedRule.specificity));
        Collections.reverse(rules);
        for (MatchedRule matchRule : rules) {
            for (Declaration declaration : matchRule.rule.getDeclarations()) {
                if (!values.containsKey(declaration.name)) {
                    values.put(declaration.name, declaration.value);
                }
            }
        }
        return values;
    }

    /**
     * 逐个查看每个DOM节点的display属性,并且获得display的值,没有值返回inline
     */
    public Display display() {
        String value = Objects.requireNonNullElse(specified_values.get("display").toString(), "");
        switch (value) {
            case "block":
                return Display.Block;
            case "none":
                return Display.None;
            default:
                return Display.Inline;
        }
    }

    /**
     * 这个函数在CSS中先查询第一个参数所代表的属性.
     * 如果没找到就再查询第二个参数代表的属性，如果还没找到，就把第三个参数作为默认值返回。
     */

    public Value lookup(String name, String fallback_name, Value option) {
        if (specified_values.get(name) != null) {
            return specified_values.get(name);
        } else if (specified_values.get(fallback_name) != null) {
            return specified_values.get(fallback_name);
        } else {
            return option;
        }
    }

    /**
     * 重新排版
     */

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sout(this, sb, 0).toString();
    }

    private StringBuilder sout(StyledNode styledNode, StringBuilder sb, int num) {
        String indent = "  ";
        sb.append(indent.repeat(num)).append("<").append(styledNode.domNode.tag_name);
        Map<String, Value> map = styledNode.specified_values;
        if (map != null) {
            for (Map.Entry<String, Value> entry : map.entrySet()) {
                sb.append(" ").append(entry.getKey()).append("=").append("\"").append(entry.getValue().toString()).append("\"");
            }
        }
        sb.append(">\n");
        for (StyledNode sn : styledNode.children) {
            if (sn.domNode.node_type.equals("text")) {
                sb.append(indent.repeat(num + 1)).append("<text></text>\n").append(indent.repeat(num));
                num = 0;
                continue;
            }
            sb = sout(sn, sb, num + 1);
        }
        sb.append(indent.repeat(num)).append("</").append(styledNode.domNode.tag_name).append(">\n");
        return sb;
    }
}