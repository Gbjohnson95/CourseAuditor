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
 *
 * @author gbjohnson
 */
public class PageAuditor {

    Document doc;
    String fname;
    String fpath;
    Element body;
    String docTitle;
    String returnString;

    public void audit(String filename, String dTitle) throws IOException {
        File input = new File(filename);
        if (input.exists()) {
            fname = filename;
            fpath = filename;
            fpath = fpath.replace(",", "");
            doc = Jsoup.parse(input, "UTF-8");
            body = doc.getElementsByTag("body").first();
            docTitle = dTitle;
            docTitle = docTitle.replace(",", "");
            printMetrics();
            checkLinksAndImages();
            checkBolds();
            countDivsSpans();
            checkHeaders();
            printFPath();
            writeCSV("\n");
        }
        /*
        fname = filename;
        fpath = filename;
        fpath = fpath.replace(",", "");
        doc = Jsoup.parse(input, "UTF-8");
        body = doc.getElementsByTag("body").first();
        docTitle = dTitle;
        docTitle = docTitle.replace(",", "");
        printMetrics();
        checkLinksAndImages();
        checkBolds();
        countDivsSpans();
        checkHeaders();
        printFPath();
        writeCSV("\n");
*/
    }

    public void printMetrics() {
        Elements titleE = doc.select("title");
        String title;
        if (titleE.isEmpty()) {
            title = "ERROR: COULD NOT READ TITLE";
        } else {
            title = titleE.first().text();
        }
        title = title.replace(",", "");
        writeCSV(docTitle + ",");
        writeCSV(title + ","); // HTML title
    }

    public void checkHeaders() {
        Elements h1s = body.getElementsByTag("h1");
        Elements h2s = body.getElementsByTag("h2");
        Elements h3s = body.getElementsByTag("h3");
        Elements h4s = body.getElementsByTag("h4");
        Elements h5s = body.getElementsByTag("h5");
        Elements h6s = body.getElementsByTag("h6");
        String headers = "";
        if (!h1s.isEmpty()) {
            headers += "1";
        }
        if (!h2s.isEmpty()) {
            headers += "2";
        }
        if (!h3s.isEmpty()) {
            headers += "3";
        }
        if (!h4s.isEmpty()) {
            headers += "4";
        }
        if (!h5s.isEmpty()) {
            headers += "5";
        }
        if (!h6s.isEmpty()) {
            headers += "6";
        }
        writeCSV(headers + ",");
    }

    public void printFPath() {
        writeCSV(fpath + ",");
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

        int emptyLinks = 0;
        for (Element a : links) {
            boolean noHref = a.attr("href").isEmpty();
            boolean noLinkText = a.text().isEmpty();
            boolean hasHref = a.hasAttr("href");
            if (noHref == true || noLinkText == true || hasHref == false) {
                emptyLinks++;
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

        int imgCounter = 0;
        for (Element img : images) {
            String width = img.attr("width");
            String p = "%";
            if (!width.toLowerCase().contains(p.toLowerCase())) {
                String src = img.attr("src");
                String banner = "Banner";
                if (!src.toLowerCase().contains(banner.toLowerCase())) {
                    imgCounter++;
                }

            }
        }

        writeCSV(bhlinksCounter + ",");
        writeCSV(bhimgesCounter + ",");
        writeCSV(bxlinksCounter + ",");
        writeCSV(tgCounter + ",");

        writeCSV(emptyLinks + ",");
        writeCSV(imgCounter + ",");

    }

    public void countDivsSpans() {
        Elements divs = body.getElementsByTag("div");
        int divCounter = 0;
        for (Element div : divs) {
            if (!div.hasAttr("id")) {
                divCounter++;
            }
        }
        Elements spans = body.getElementsByTag("span");
        int spanCounter = 0;
        for (Element span : spans) {
            spanCounter++;
        }
        writeCSV(divCounter + ",");
        writeCSV(spanCounter + ",");
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
        writeCSV(bCounter + ",");
    }

    public void writeCSV(String text) {
        System.out.print(text);
        writeString(text);
    }

    public void writeString(String text) {
        returnString += text;
    }

    public void printHeader() {
        System.out.println("Title,HTML Title,BH Links,BH Images,Box Links,Bad Link Targets,Empty Links,Image Width,Bolds,Divs,Spans,Header Order,Filepath,");
    }
}
