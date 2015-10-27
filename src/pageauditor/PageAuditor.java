/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pageauditor;

import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**

 @author gbjohnson
 */
public class PageAuditor {

    Document doc;
    String fname;
    Element body;

    public void audit(String filename) throws IOException {
        File input = new File(filename);
        fname = filename;
        doc = Jsoup.parse(input, "UTF-8");
        body = doc.getElementsByTag("body").first();
        System.out.println("HTML Title, File Name, BrainHoney Links, BrainHoney Images, Box Links");
        printMetrics();
        checkLinksAndImages();
        //checkForBox();
        //checkForDeprecatedTags();
    }

    public void printMetrics() {

        Element titleElement = doc.select("title").first();
        String title = titleElement.text();
        writeCSV(title);
        writeCSV(fname);
    }

    public void checkLinksAndImages() {
        Elements links = body.getElementsByTag("a");
        int bhlinksCounter = 0;
        for (Element a : links) {
            String href = a.attr("href");
            String bh = "brainhoney";
            if (href.toLowerCase().contains(bh.toLowerCase())) {
                bhlinksCounter++;
            }
        }

        int bxlinksCounter = 0;
        for (Element a : links) {
            String href = a.attr("href");
            String bx = "box.com";
            if (href.toLowerCase().contains(bx.toLowerCase())) {
                bxlinksCounter++;
            }
        }

        Elements images = body.getElementsByTag("img");
        int bhimgesCounter = 0;
        for (Element img : images) {
            String src = img.attr("src");
            String bh = "brainhoney";
            if (src.toLowerCase().contains(bh.toLowerCase())) {
                bhimgesCounter++;
            }
        }

        writeCSV(bhlinksCounter + "");
        writeCSV(bhimgesCounter + "");
        writeCSV(bxlinksCounter + "");

    }

    public void checkForBox() {
        Elements images = body.getElementsByTag("img");

    }

    public void checkForDeprecatedTags() {
        // Looks for <acronym>, <applet>, <basefont>, <big>, <center>, <dir>,
        // <font>, <frame>, <frameset>, <isindex>, <noframes>, <s>, <strike>, 
        // <tt>, <u>.
    }

    public void writeCSV(String text) {
        System.out.print(text + ",");
    }

}
