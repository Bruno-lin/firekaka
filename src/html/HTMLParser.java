package html;

import dom.ElementNode;
import dom.Node;
import dom.TextNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class HTMLParser {
    int pos;        //当前位置
    String input;       //输入的html文本


    public HTMLParser() {
        pos = 0;        //初始化位置
    }

    /**
     * 读取当前字符而不consume它
     */
    private char next_char() {
        return input.charAt(pos);
    }

    /**
     * 下一个字符是否以给定的字符串开始
     */
    private boolean start_with(int n, char[] c) {
        return input.substring(pos, pos + n).equals(c);
    }

    /**
     * 如果读完了所有输入的字符，则返回true。
     */
    private boolean eof() {
        return pos >= input.length() - 1;
    }

    /**
     * 返回当前字符，并将 input的位置 推进到下一个字符。
     */
    private char consume_char() {
        pos++;
        return input.charAt(pos - 1);
    }

    /**
     * 消耗字符，直到`test`返回false为止
     *
     * @param predicate
     */
    private String consume_while(Predicate<Character> predicate, char c) {
        StringBuilder sb = new StringBuilder();
        while (!eof() && predicate.test(c)) {
            sb.append(consume_char());
        }
        return sb.toString();
    }

    /**
     * 读取多个（也可能是零个）空白字符
     */
    private void consume_whitespace() {
        if (eof()) {
            return;
        }
        while (Character.isWhitespace(next_char())) {
            consume_char();
        }
    }


    /**
     * 解析标签
     */
    private String parse_tag_name() {
        StringBuilder sb = new StringBuilder();
        while (Pattern.matches("[a-zA-Z0-9]", String.valueOf(next_char()))) {
            sb.append(consume_char());
        }
        return sb.toString();
    }

    /**
     * 解析单个节点
     */

    private Node parse_node() {
        if (next_char() == '<') {
            return parse_element();
        } else {
            return parse_text();
        }
    }

    /**
     * 解析文字节点
     */

    private TextNode parse_text() {
        String text = consume_while(c -> next_char() != c, '<');
        return new TextNode(text);
    }

    /**
     * 解析元素节点
     */

    private ElementNode parse_element() {
        //开始标签
        assert (consume_char() == '<');
        String tag_name = parse_tag_name();
        Map<String, String> attrs = parse_attributes();
        assert (consume_char() == '>');
        //内容
        ArrayList<Node> children = parse_nodes();
        //结束标签
        assert (consume_char() == '<');
        assert (consume_char() == '/');
        assert (parse_tag_name().equals(tag_name));
        assert (consume_char() == '>');

        return new ElementNode(tag_name, attrs, children);

    }

    /**
     * 解析属性，返回字符串对，要求属性名后面紧跟‘=’；
     */

    private String[] parse_attr() {
        String name = parse_tag_name();
        assert (consume_char() == '=');
        String value = parse_attr_value();
        String[] parseAttr = new String[]{name, value};
        return parseAttr;
    }

    /**
     * 解析一个引用的值
     */
    private String parse_attr_value() {
        char open_quote = consume_char();
        assert (open_quote == '"' || open_quote == '\'');
        String value = consume_while(c -> next_char() != c, open_quote);
        assert (consume_char() == open_quote);
        return value;
    }

    /**
     * 将标签分成属性和值
     */
    private Map<String, String> parse_attributes() {
        Map<String, String> attrMap = new HashMap<>();
        while (true) {
            consume_whitespace();
            if (next_char() == '>') {
                break;
            }
            attrMap.put(parse_attr()[0], parse_attr()[1]);
        }
        return attrMap;
    }

    /**
     * 解析兄弟（同级）节点
     */
    private ArrayList<Node> parse_nodes() {
        ArrayList<Node> nodes = new ArrayList<>();
        while (true) {
            consume_whitespace();
            if (eof() || start_with(2, new char[]{'<', '/'})) {
                break;
            }
            nodes.add(parse_node());
        }
        return nodes;
    }

    /**
     * 解析一个HTML文档，并返回根元素。
     */
    public Node parse(String source) {
        input = source;
        ArrayList<Node> nodes = parse_nodes();
        /**如果文档中包含一个根元素，只需返回它。否则，创建一个**/
        if (nodes.size() == 1) {
            return nodes.get(0);
        } else {
            return new ElementNode("html", new HashMap<>(), nodes);
        }
    }
}
