package cn.baiyang.origin.examples.pdf;

import com.openhtmltopdf.DOMBuilder;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.openhtmltopdf.slf4j.Slf4jLogger;
import com.openhtmltopdf.util.XRLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;

import org.jsoup.Jsoup;

/**
 *
 * @author hongzhu
 * @version V1.0
 * @since 2019-03-15 15:59
 */
public class OpenHtmltoPdf {
    private static final File FONT_FILE;

    static {
        final URL fontFileUrl = ClassLoader.getSystemResource("font/stsong/chinese.stsong.ttf");
        FONT_FILE = new File(fontFileUrl.getPath());
    }

    public static void main(String[] args) {
        XRLog.setLoggingEnabled(true);
        XRLog.setLoggerImpl(new Slf4jLogger());

        final URL outputUrl = ClassLoader.getSystemResource("output");
        try (OutputStream os = new FileOutputStream(outputUrl.getPath() + "/solution.pdf")) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.useFont(FONT_FILE, "华文宋体");

            final URL templatHtmlUrl = ClassLoader.getSystemResource("template/solution.html");
            final File templateHtmlFile = new File(templatHtmlUrl.getPath());
            final org.jsoup.nodes.Document jsoupDoc = Jsoup.parse(templateHtmlFile, "UTF-8");
            final org.w3c.dom.Document w3cDoc = DOMBuilder.jsoup2DOM(jsoupDoc);
            builder.withW3cDocument(w3cDoc, null);

            builder.toStream(os);
            builder.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
