import acm.graphics.GImage;
import acm.program.GraphicsProgram;
import css.CSSParser;
import css.Stylesheet;
import dom.Node;
import html.HTMLParser;
import layout.LayoutBox;
import painting.Paint;
import style.StyledNode;

import java.awt.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Browser extends GraphicsProgram {

    private static final int APPLICATION_WIDTH = 800;
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;
    private static final String URL = "website";

    String html;
    String css;

    public void run() {
        // 设置窗口和画布大小
        setWidth(APPLICATION_WIDTH);
        setCanvasWidth(CANVAS_WIDTH);
        setCanvasHeight(CANVAS_HEIGHT);

        // 读取HTML和CSS文件
        try {
            html = Files.readString(Path.of("res/" + URL + "/index.html"), StandardCharsets.UTF_8);
            css = Files.readString(Path.of("res/" + URL + "/style.css"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 渲染
        render();
    }

    public void render() {
        clearCanvas();
        HTMLParser htmlParser = new HTMLParser(html);
        Node root = htmlParser.parse();
        CSSParser cssParser = new CSSParser(css);
        Stylesheet stylesheet = cssParser.parse();
        StyledNode styledRoot = new StyledNode(root, stylesheet);
        LayoutBox layoutBox = new LayoutBox(styledRoot);
        layoutBox.layoutTree(getWidth());
        Paint paint = new Paint();
        Image image = paint.paint(layoutBox, CANVAS_WIDTH, CANVAS_HEIGHT);

        GImage gImage = new GImage(image);
        add(gImage);
    }
}

