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
        printMetrics();
        checkLinksAndImages();
        checkBolds();
        writeCSV("\n");
    }

    public void printMetrics() {

        Elements titleElement = doc.getElementsByTag("title");
        String title = titleElement.first().text();
        writeCSV(title + ",");
        writeCSV(fname + ",");
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
        
        int tgCounter = 0;
        for (Element a : links) {
            String target = a.attr("target");
            String blank = "_blank";
            if (!target.toLowerCase().contains(blank.toLowerCase())) {
                tgCounter++;
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

        writeCSV(bhlinksCounter + ",");
        writeCSV(bhimgesCounter + ",");
        writeCSV(tgCounter + ",");
        writeCSV(bxlinksCounter + ",");

    }

    public void checkBolds() {
        Elements ps = body.getElementsByTag("p");
        int bCounter = 0;
        for (Element p : ps) {
            String style = p.attr("style");
            String bold = "bold";
            if (style.toLowerCase().contains(bold.toLowerCase())) {
                bCounter++;
            }
        }
        
        Elements spans = body.getElementsByTag("span");
        for (Element span : spans) {
            String spanStyle = spans.attr("style");
            String bold = "bold";
            if (spanStyle.toLowerCase().contains(bold.toLowerCase())) {
                bCounter++;
            }
        }
        writeCSV(bCounter + ", ");
    }

    public void writeCSV(String text) {
        System.out.print(text);
    }

    public void printHeader() {
        System.out.println("HTML Title,File Name,BH Links,BH Images,Bad Link Targets,Box Links,Bolds");
    }

}
