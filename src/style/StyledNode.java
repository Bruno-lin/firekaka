package style;

import css.*;
import dom.ElementNode;
import dom.Node;

import java.util.*;

public class StyledNode {

    private Node domNode;
    private Map<String, String> propertyMap;
    private ArrayList<StyledNode> children;

    /**
     * 设置整个DOM树的样式，返回一棵StyledNode树
     */
    public StyledNode(Node domNode, Stylesheet stylesheet) {
        Map<String, String> propertyMap;
        if (domNode.node_type.equals("element")) {
            propertyMap = specified_values((ElementNode) domNode, stylesheet);
        } else {
            propertyMap = new LinkedHashMap<>();
        }
        ArrayList<StyledNode> children = new ArrayList<>();
        if (domNode.children != null) {
            for (Node child : domNode.children) {
                children.add(new StyledNode(child, stylesheet));
            }
        }
        this.domNode = domNode;
        this.propertyMap = propertyMap;
        this.children = children;
    }

    /**
     * 元素节点与选择器是否匹配
     */

    public boolean matches(ElementNode elementNode, Selector selector) {
        if (!elementNode.tag_name.equals(selector.tag_name)) {
            return false;
        }
        if (!elementNode.getID().equals(selector.id) && !selector.id.equals("")) {
            return false;
        }

        if (selector.class_array.size() == 0) {
            return true;
        }
        for (String s : selector.class_array) {
            if (!elementNode.getClassName().contains(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 对于树中的当前元素，我们都需要在样式表中搜索匹配的规则。
     */
    public MatchedRule match_rule(ElementNode elementNode, Rule rule) {
        //将rule的每一个选择器都与selector配对
        for (Selector selector : rule.getSelectors()) {
            // 找到第一个（即优先级最高的）选择器
            System.out.println(elementNode.tag_name);
            System.out.println("///");
            System.out.println(selector.tag_name);
            System.out.println(matches(elementNode, selector));
            if (matches(elementNode, selector)) {
                MatchedRule matchedRule = new MatchedRule(selector.specificity(), rule);
//                System.out.println(matchedRule);
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
    public Map<String, String> specified_values(ElementNode elementNode, Stylesheet stylesheet) {
        Map<String, String> values = new LinkedHashMap<>();
        ArrayList<MatchedRule> rules = matching_rules(elementNode, stylesheet);
        //按照优先级从低到高遍历
        rules.sort(Comparator.comparingInt(matchedRule -> matchedRule.specificity));
        Collections.reverse(rules);
        for (MatchedRule matchRule : rules) {
            for (Declaration declaration : matchRule.rule.getDeclarations()) {
                if (!values.containsKey(declaration.getName())) {
                    values.put(declaration.getName(), declaration.getValue());
                }
            }
        }
        return values;
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
        Map<String, String> map = styledNode.propertyMap;
        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                sb.append(" ").append(entry.getKey()).append("=").append("\"").append(entry.getValue()).append("\"");
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