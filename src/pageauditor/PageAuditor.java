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
    Element body;
    private String filepath;
    private String docTitle;
    private Elements links;
    private Elements images;
    private Elements spans;
    private Elements divs;
    private Elements ps;
    private Elements titleE;

    public void audit(String filename, String dTitle) throws IOException {
        File input = new File(filename);
        if (input.exists()) {
            doc = Jsoup.parse(input, "UTF-8");
            body = doc.getElementsByTag("body").first();

            // Set elements for program
            links = body.getElementsByTag("a");
            images = body.getElementsByTag("img");
            spans = body.getElementsByTag("span");
            divs = body.getElementsByTag("div");
            ps = body.getElementsByTag("p");
            titleE = doc.select("title");
            docTitle = dTitle.replace(",", "");
            filepath = filename.replace(",", "");

            // Write the line
            writeCSV(docTitle + ",");                  // Title
            writeCSV(getHTMLTitle() + ",");            // HTML Title
            writeCSV(numBHLinks() + ",");              // BH Links
            writeCSV(numBXLinks() + ",");              // BX Links
            writeCSV(numBadTargets() + ",");           // Bad Link Targets
            writeCSV(numEmptyLinks() + ",");           // Empty Links
            writeCSV(numBHImages() + ",");             // BH Images
            writeCSV(numBadImageWidth() + ",");        // Bad Image Width
            writeCSV(numBolds() + ",");                // Bolds
            writeCSV(numSpans() + ",");                // Spans
            writeCSV(numDivs() + ",");                 // Divs
            writeCSV(checkHeaders() + ",");            // Headers
            writeCSV(checkFilePath() + ","); // File Path
            writeCSV("\n");
        }
    }
    
    public void printHeader() {
        System.out.println("Title,HTML Title,BH Links,Box Links,Bad Link Targets,Empty Links,BH Images,Image Width,Bolds,Spans,Divs,Header Order,Filepath,");
    }
    
    private String checkFilePath() {
        if (filepath.contains("Course Files")) {
            return "Good: " + filepath;
        } else {
            return "Bad: " + filepath;
        }
    }

    private String getHTMLTitle() {
        if (titleE.isEmpty()) {
            return "ERROR: COULD NOT READ TITLE";
        } else {
            String title = titleE.first().text().replace(",", "");
            if (title == null ? docTitle == null : title.toLowerCase().equals(docTitle.toLowerCase())) {
                return "Match!";
            } else {
              return "No Match: " + title;  
            }
        }
    }

    private int numBHLinks() {
        int bhlinksCounter = 0;
        for (Element a : links) {
            String href = a.attr("href");
            if (href.toLowerCase().contains("brainhoney")) {
                bhlinksCounter++;
            }
        }
        return bhlinksCounter;
    }

    private int numBHImages() {
        int bhimgesCounter = 0;
        for (Element img : images) {
            String src = img.attr("src");
            if (src.toLowerCase().contains("brainhoney")) {
                bhimgesCounter++;
            }
        }
        return bhimgesCounter;
    }

    private int numBXLinks() {
        int bxlinksCounter = 0;
        for (Element a : links) {
            String href = a.attr("href");
            if (href.toLowerCase().contains("box.com")) {
                bxlinksCounter++;
            }
        }
        return bxlinksCounter;
    }

    private int numEmptyLinks() {
        int emptyLinks = 0;
        for (Element a : links) {
            boolean noHref = a.attr("href").isEmpty();
            boolean noLinkText = a.text().isEmpty();
            boolean hasHref = a.hasAttr("href");
            if (noHref == true || noLinkText == true || hasHref == false) {
                emptyLinks++;
            }
        }
        return emptyLinks;
    }

    private String checkHeaders() {
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
        if ("123456".indexOf(headers) == 0) {
            return "Good: " + headers;
        } else {
            return "Bad: " + headers;
        }
    }

    private int numBadTargets() {
        int tgCounter = 0;
        for (Element a : links) {
            String target = a.attr("target");
            String blank = "_blank";
            if (!target.toLowerCase().contains(blank.toLowerCase())) {
                tgCounter++;
            }
        }
        return tgCounter;
    }

    private int numBadImageWidth() {
        int imgCounter = 0;
        for (Element img : images) {
            String width = img.attr("width");
            if (!width.toLowerCase().contains("%")) {
                String src = img.attr("src");
                if (!src.toLowerCase().contains("banner")) {
                    imgCounter++;
                }

            }
        }
        return imgCounter;
    }

    private int numSpans() {
        int spanCounter = 0;
        for (Element span : spans) {
            spanCounter++;
        }
        return spanCounter;
    }

    private int numDivs() {
        int divCounter = 0;
        for (Element div : divs) {
            if (!div.hasAttr("id")) {
                divCounter++;
            }
        }
        return divCounter;
    }

    private int numBolds() {
        int bCounter = 0;
        for (Element p : ps) {
            String style = p.attr("style");
            if (style.toLowerCase().contains("bold".toLowerCase())) {
                bCounter++;
            }
        }
        for (Element span : spans) {
            String spanStyle = spans.attr("style");
            if (spanStyle.toLowerCase().contains("bold".toLowerCase())) {
                bCounter++;
            }
        }
        return bCounter;
    }

    public void writeCSV(String text) {
        System.out.print(text);
    }

    
}
