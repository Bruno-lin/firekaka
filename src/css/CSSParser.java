package css;

import parser.Parser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Pattern;

public class CSSParser extends Parser {

    /**
     * 构造函数
     */
    public CSSParser() {
    }

    /**
     * 解析整个CSS样式表。
     */
    public Stylesheet parse(String source) {
        this.input = source;
        Stylesheet stylesheet = new Stylesheet(parse_rules());
        return stylesheet;
    }

    /**
     * parse规则
     */
    public ArrayList<Rule> parse_rules() {

        ArrayList<Rule> rules = new ArrayList<>();
        while (!eof()) {
            rules.add(parse_rule());
        }
        return rules;
    }

    /**
     * parse一个规则
     */
    public Rule parse_rule() {
        Rule rule = new Rule(parse_selectors(), parse_declarations());
        return rule;
    }

    /**
     * parse分好的选择器
     */
    public Selector parse_selector() {
        Selector selector = new Selector("", null, new ArrayList<>());
        while (!eof()) {
            char c = next_char();
            if (c == '#') {
                consume_char();
                selector.id = parse_identifier();
            } else if (c == '.') {
                consume_char();
                selector.class_array.add(parse_identifier());
            } else if (c == '*') {
                consume_char();
            } else if (valid_identifier_char(c)) {
                selector.tag_name = parse_identifier();
            } else {
                break;
            }
        }
        return selector;
    }

    /**
     * 解析多个选择器
     */
    public ArrayList<Selector> parse_selectors() {
        ArrayList<Selector> selectors = new ArrayList<>();
        while (true) {
            selectors.add(parse_selector());
            consume_whitespace();
            if (start_with(1, new char[]{','})) {
                consume_char();
                consume_whitespace();
            } else if (start_with(1, new char[]{'{'})) {
                break;
            }
        }
        //首先返回具有最高优先级的选择器，以用于匹配。
        selectors.sort(Comparator.comparingInt(Selector::specificity));
        return selectors;
    }

    /**
     * 判断char是否有效
     */
    public boolean valid_identifier_char(char c) {
        if (Pattern.matches("[A-Za-z0-9]", String.valueOf(c))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 解析一个`<property>：<value>;`声明。
     */
    public Declaration parse_declaration() {
        String property_name = parse_identifier();
        assert consume_char() == ':';
        consume_whitespace();
        String value = parse_value();
        consume_whitespace();
        assert consume_char() == ';';
        Declaration declaration = new Declaration(property_name, value);
        return declaration;
    }

    /**
     * 解析包含在`{...}`中的声明列表
     */
    public ArrayList<Declaration> parse_declarations() {
        assert consume_char() == '{';
        ArrayList<Declaration> declarations = new ArrayList<>();
        while (true) {
            consume_whitespace();
            if (next_char() == '}') {
                consume_char();
                break;
            }
            declarations.add(parse_declaration());
        }
        return declarations;
    }

    /**
     * 解析值，如关键词、颜色、数字
     */
    public String parse_value() {
        return consume_while(c -> next_char() != c, ';');
    }

    /**
     * 解析标识符
     */
    public String parse_identifier() {
        StringBuilder sb = new StringBuilder();
        consume_whitespace();
        while (Pattern.matches("[A-Za-z0-9\\-]", String.valueOf(next_char()))) {
            sb.append(consume_char());
        }
        return sb.toString();
    }
}
