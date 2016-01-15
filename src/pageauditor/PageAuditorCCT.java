/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pageauditor;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

// THIS DOCUMENT IS BEING OBSOLETED IN FAVOR OF COURSEDOCUMENT
/**
 *
 * @author gbjohnson
 */
public class PageAuditorCCT {

    private Document doc;
    private Element body;
    private String filepath, docTitle, htmlString, oui;
    private Elements bs, is, titleE;

    public void audit(String filename, String dTitle, String orgunitid) throws IOException {
        File input = new File(filename);
        oui = orgunitid;
        if (input.exists()) {
            doc = Jsoup.parse(input, "UTF-8");
            body = doc.getElementsByTag("body").first();

            // Set elements for program
            is = body.getElementsByTag("i");
            bs = body.getElementsByTag("b");
            htmlString = doc.toString();
            titleE = doc.select("title");

            // Remove commas from the titles
            docTitle = dTitle.replace(",", "");
            docTitle = docTitle.replaceAll("\t", "");
            docTitle = docTitle.replaceAll("\n", "");
            filepath = filename.replace(",", "");
        }
    }

    /**
     * Returns the string with the metrics.
     *
     * @return
     */
    public String getMetrics() {
        String printString
                = oui + "," // Org Unit
                + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()) + "," // Date String
                + docTitle + "," // Title
                + getHTMLTitle() + "," // HTML Title
                + wrongcourselinks() + "," // Links pointing outside of course
                + countCSSQuery("a[href*=/calendar/]") + "," // Calender links
                + countCSSQuery("a[href*=/home/], a[href*=viewContent], a[href*=/calendar/]") + "," // Incorrect link type
                + countCSSQuery("a[href*=brainhoney]") + "," // BH Links
                + countCSSQuery("a[href*=box.com]") + "," // Box Links
                + countCSSQuery("a[href*=courses.byui.edu]") + "," // Benjamin Links
                + countCSSQuery("a:not([target*=_blank])") + "," // Bad Link Targets
                + numEmptyLinks() + "," // Empty Links
                + countCSSQuery("img[src*=brainhoney]") + "," // BH Images
                + numBadImageWidth() + "," // Image Width
                + regexSearch("font-weight\\: bold") + "," // Bolds
                + countCSSQuery("span") + "," // Spans
                + countCSSQuery("b, i, br") + "," // Bad Tags
                + countCSSQuery("div:not([id])") + "," // Divs
                + regexSearch("\\$[A-Za-z]+\\S\\$") + "," // BHVars
                + regexSearch("[sS]aturday") + "," // Mentions Saturday
                + checkHeaders() + "," // Headers
                + getTemplateName() + "," // Template
                + checkFilePath() + ",\n";  // File Path
        return printString;
    }

    public String countCSSQuery(String query) {
        return doc.select(query).size() + "";
    }

    public String regexSearch(String regex) {
        int matchCounter = 0;
        Pattern findvars = Pattern.compile(regex);
        Matcher m = findvars.matcher(htmlString);
        while (m.find()) {
            matchCounter++;
        }
        return matchCounter + "";
    }

    private String checkFilePath() {
        if (filepath.contains("Course Files") || filepath.contains("Content Files")) {
            return "Good: " + filepath;
        } else {
            return "Bad: " + filepath;
        }
    }

    private String wrongcourselinks() {
        return doc.select("a[href*=/d2l/]").not("[href*=" + oui + "]").size() + "";
    }

    private String getHTMLTitle() {
        if (titleE.isEmpty()) {
            return "ERROR: COULD NOT READ TITLE";
        } else {
            String title = titleE.first().text().replace(",", "");
            title = title.replaceAll("\t", "");
            title = title.replaceAll("\n", "");
            if (title == null ? docTitle == null : title.toLowerCase().trim().equals(docTitle.trim().toLowerCase())) {
                return "Matching";
            } else {
                return title;
            }
        }
    }

    private String getTemplateName() {
        String returnString = "";
        for (Element img : body.getElementsByTag("img")) {
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

    private int numEmptyLinks() {
        int emptyLinks = 0;
        for (Element a : body.getElementsByTag("a")) {
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
        if (headers == "") {
            headers = "NONE";
        }
        if ("123456".indexOf(headers) == 0) {
            return "Good: " + headers;
        } else {
            return "Bad: " + headers;
        }
    }

    private int numBadImageWidth() {
        int imgCounter = 0;
        for (Element img : body.getElementsByTag("img")) {
            String width = img.attr("width");
            if (!width.toLowerCase().contains("%") && !img.attr("src").toLowerCase().contains("banner")) {
                imgCounter++;
            }
        }
        return imgCounter;
    }
}
