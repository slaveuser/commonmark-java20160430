package org.commonmark.ext.gfm.strikethrough;

import org.commonmark.Extension;
import org.commonmark.html.HtmlRenderer;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.test.RenderingTestCase;
import org.junit.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class StrikethroughTest extends RenderingTestCase {

    private static final Set<Extension> EXTENSIONS = Collections.singleton(StrikethroughExtension.create());
    private static final Parser PARSER = Parser.builder().extensions(EXTENSIONS).build();
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder().extensions(EXTENSIONS).build();

    @Test
    public void oneTildeIsNotEnough() {
        assertRendering("~foo~", "<p>~foo~</p>\n");
    }

    @Test
    public void twoTildesYay() {
        assertRendering("~~foo~~", "<p><del>foo</del></p>\n");
    }

    @Test
    public void fourTildesNope() {
        assertRendering("foo ~~~~", "<p>foo ~~~~</p>\n");
    }

    @Test
    public void unmatched() {
        assertRendering("~~foo", "<p>~~foo</p>\n");
        assertRendering("foo~~", "<p>foo~~</p>\n");
    }

    @Test
    public void threeInnerThree() {
        assertRendering("~~~foo~~~", "<p>~<del>foo</del>~</p>\n");
    }

    @Test
    public void twoInnerThree() {
        assertRendering("~~foo~~~", "<p><del>foo</del>~</p>\n");
    }

    @Test
    public void twoStrikethroughsWithoutSpacing() {
        assertRendering("~~foo~~~~bar~~", "<p><del>foo</del><del>bar</del></p>\n");
    }

    @Test
    public void strikethroughWholeParagraphWithOtherDelimiters() {
        assertRendering("~~Paragraph with *emphasis* and __strong emphasis__~~",
                "<p><del>Paragraph with <em>emphasis</em> and <strong>strong emphasis</strong></del></p>\n");
    }

    @Test
    public void insideBlockQuote() {
        assertRendering("> strike ~~that~~",
                "<blockquote>\n<p>strike <del>that</del></p>\n</blockquote>\n");
    }

    @Test
    public void delimited() {
        Node document = PARSER.parse("~~foo~~");
        Strikethrough strikethrough = (Strikethrough) document.getFirstChild().getFirstChild();
        assertEquals("~~", strikethrough.getOpeningDelimiter());
        assertEquals("~~", strikethrough.getClosingDelimiter());
    }

    @Override
    protected String render(String source) {
        return RENDERER.render(PARSER.parse(source));
    }
}
