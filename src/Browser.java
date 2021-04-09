import acm.graphics.GImage;
import acm.program.GraphicsProgram;
import css.CSSParser;
import css.Stylesheet;
import dom.Node;
import html.HTMLParser;
import layout.LayoutBox;
import painting.Paint;
import style.StyledNode;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Browser extends GraphicsProgram{
    private static final int APPLICATION_WIDTH = 800;
    private static final int APPLICATION_HEIGHT = 600;
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 600;

    String htmlInput;
    String cssInput;
    public JTextField addressBar;
    public JButton goButton;

    public void init() {
        addressBar = new JTextField(15);
        addressBar.setEditable(true);
        addressBar.addActionListener(this);
        add(addressBar, NORTH);

        goButton = new JButton("GO");
        goButton.addActionListener(this);
        add(goButton, NORTH);
    }

    public void run() {
        // 设置窗口和画布大小
        setWidth(APPLICATION_WIDTH);
        setHeight(APPLICATION_HEIGHT);

        setCanvasWidth(CANVAS_WIDTH);
        setCanvasHeight(CANVAS_HEIGHT);
    }


    public void actionPerformed(ActionEvent e) {
        if ("GO".equals(e.getActionCommand())) {
            String url = addressBar.getText().trim();
            try {
                // 读取HTML和CSS文件
                htmlInput = Files.readString(Path.of("res/" + url + "/index.html"), StandardCharsets.UTF_8);
                cssInput = Files.readString(Path.of("res/" + url + "/style.css"), StandardCharsets.UTF_8);
                // 渲染
                render();
            } catch (IOException exception) {
                JOptionPane.showMessageDialog(this, "文件不存在", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "未知命令", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void render(){
        HTMLParser htmlParser = new HTMLParser();
        Node domNode = htmlParser.parse(htmlInput);

        CSSParser cssParser = new CSSParser();
        Stylesheet stylesheet = cssParser.parse(cssInput);

        StyledNode styledRoot = new StyledNode(domNode, stylesheet);
        LayoutBox layoutBoxRoot = new LayoutBox(styledRoot);

        // 根据窗口宽度计算
        layoutBoxRoot.layoutTree(CANVAS_WIDTH);


        Paint paint = new Paint();
        BufferedImage image = paint.paint(layoutBoxRoot, CANVAS_WIDTH, CANVAS_HEIGHT);
        GImage gImage = new GImage(image);
        add(gImage);
    }
}
