/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pageauditor;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author gbjohnson
 */
public class PageAuditor {

    private Document doc;
    private Element body;
    private String filepath;
    private String docTitle;
    private Elements links;
    private Elements bs;
    private Elements is;
    private Elements images;
    private Elements spans;
    private Elements divs;
    private Elements ps;
    private Elements titleE;
    private Elements brs;
    private Element banner;
    private String htmlString;

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
            is = body.getElementsByTag("i");
            bs = body.getElementsByTag("b");
            ps = body.getElementsByTag("p");
            brs = body.getElementsByTag("br");
            htmlString = doc.toString();
            titleE = doc.select("title");

            // Remove commas from the titles
            docTitle = dTitle.replace(",", "");
            filepath = filename.replace(",", "");

            // Writes the line
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
            writeCSV(numBadTags() + ",");              // <b>/<i>
            writeCSV(numDivs() + ",");                 // Divs
            writeCSV(numBrs() + ",");                  // Breaks
            writeCSV(countBHVars() + ",");             // Checks for BH variables
            writeCSV(mentionsSaturday() + ",");        // Page mentions saturday
            writeCSV(checkHeaders() + ",");            // Headers
            writeCSV(getTemplateName() + ",");         // Template name
            writeCSV(checkFilePath() + ",");           // File Path
            writeCSV("\n");
        }
    }

    /**
     * Prints the table headers.
     *
     */
    public void printHeader() {
        System.out.println("Title,HTML Title,BH Links,Box Links,Bad Link Targets,Empty Links,Special Letters,BH Images,Image Width,Bolds,Spans,Divs,Br,BHVars,Mentions Saturday,Header Order,Template,Filepath,");
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
            if (title == null ? docTitle == null : title.toLowerCase().trim().equals(docTitle.trim().toLowerCase())) {
                return "Match!";
            } else {
                return "No Match: " + title;
            }
        }
    }

    private String getTemplateName() {
        String returnString = "";
        for (Element img : images) {
            if (img.attr("alt").toLowerCase().contains("banner")) {
                if (img.attr("src").contains("largeBanner")) {
                    returnString = "Large";
                }
                if (img.attr("src").contains("smallBanner")) {
                    returnString = "Small";
                }
            }
        }
        return returnString;
    }

    private String mentionsSaturday() {
        String returnString = "No";
        Pattern dueSaturday = Pattern.compile("[sS]aturday");
        Matcher m = dueSaturday.matcher(htmlString);
        while (m.find()) {
            returnString = "Yes";
        }
        return returnString;
    }

    private int numBrs() {
        int brCounters = 0;
        return brs.size();
    }

    private int countBHVars() {
        int BHVarsCounter = 0;
        Pattern findvars = Pattern.compile("\\$[A-Za-z]+\\S\\$");
        Matcher m = findvars.matcher(htmlString);
        while (m.find()) {
            BHVarsCounter++;
        }
        return BHVarsCounter;
    }

    private int numBadTags() {
        return bs.size() + is.size();
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
        String headers = "";
        if (!body.getElementsByTag("h1").isEmpty()) {
            headers += "1";
        }
        if (!body.getElementsByTag("h2").isEmpty()) {
            headers += "2";
        }
        if (!body.getElementsByTag("h3").isEmpty()) {
            headers += "3";
        }
        if (!body.getElementsByTag("h4").isEmpty()) {
            headers += "4";
        }
        if (!body.getElementsByTag("h5").isEmpty()) {
            headers += "5";
        }
        if (!body.getElementsByTag("h6").isEmpty()) {
            headers += "6";
        }
        if ("123456".indexOf(headers) == 0) {
            return "Good: " + headers;
        } else if (headers == "") {
            return "Bad: ";
        } else {
            return "Bad: " + headers;
        }
    }

    private int numBadTargets() {
        int tgCounter = 0;
        for (Element a : links) {
            if (!a.attr("target").toLowerCase().contains("_blank".toLowerCase())) {
                tgCounter++;
            }
        }
        return tgCounter;
    }

    private int numBadImageWidth() {
        int imgCounter = 0;
        for (Element img : images) {
            String width = img.attr("width");
            if (!width.toLowerCase().contains("%") && !img.attr("src").toLowerCase().contains("banner")) {
                imgCounter++;
            }
        }
        return imgCounter;
    }

    private int numSpans() {
        return spans.size();
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
        Pattern dueSaturday = Pattern.compile("font-weight\\: bold");
        Matcher m = dueSaturday.matcher(htmlString);
        while (m.find()) {
            bCounter++;
        }
        return bCounter;
    }

    public void writeCSV(String text) {
        System.out.print(text);
    }
}
