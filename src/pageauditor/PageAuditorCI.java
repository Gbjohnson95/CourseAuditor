/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pageauditor;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
public class PageAuditorCI {

    private Document doc;
    private Element body;
    private String filepath, docTitle, htmlString, oui, date_start, date_end, date_due;
    private Elements bs, is, images, spans, divs, titleE, brs, links;

    public void audit(String filename, String dTitle, String orgunitid) throws IOException {
        File input = new File(filename);
        oui = orgunitid;
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
            brs = body.getElementsByTag("br");
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
                = docTitle + "," // Title
                + getHTMLTitle() + "," // HTML Title
                + wrongcourselinks() + "," // Links pointing outside of course
                + numBHLinks() + "," // BH Links
                + numBXLinks() + "," // Box Links
                + benjaminLinks() + "," // Benjamin Links
                + numEmptyLinks() + "," // Empty Links
                + numBHImages() + "," // BH Images
                + numBolds() + "," // Bolds
                + (bs.size() + is.size() + spans.size() + numDivs() + brs.size()) + ",\n"; // Bad Tags

        return printString;
    }

    private String checkFilePath() {
        if (filepath.contains("Course Files") || filepath.contains("Content Files")) {
            return "Good: " + filepath;
        } else {
            return "Bad: " + filepath;
        }
    }

    private String dateParser(String dateString) {
        return dateString.replaceAll("T", dateString);
    }

    private String benjaminLinks() {
        int bhlinksCounter = 0;
        bhlinksCounter = links.stream().map((a) -> a.attr("href")).filter((href) -> (href.toLowerCase().contains("courses.byui.edu"))).map((_item) -> 1).reduce(bhlinksCounter, Integer::sum);
        return bhlinksCounter + "";
    }

    private String wrongcourselinks() {
        int wronglinkscounter = 0;
        for (Element a : links) {
            if (a.attr("href").contains("/d2l/") && !a.attr("href").contains(oui)) {
                wronglinkscounter++;
            }
        }
        return wronglinkscounter + "";
    }

    private String callinks() {
        int callinkscounter = 0;
        for (Element a : links) {
            if (a.attr("href").contains("/d2l/le/calendar/")) {
                callinkscounter++;
            }
        }
        return callinkscounter + "";
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

    private int numBHLinks() {
        int bhlinksCounter = 0;
        bhlinksCounter = links.stream().map((a) -> a.attr("href")).filter((href) -> (href.toLowerCase().contains("brainhoney"))).map((_item) -> 1).reduce(bhlinksCounter, Integer::sum);
        return bhlinksCounter;
    }

    private int numBHImages() {
        int bhimgesCounter = 0;
        bhimgesCounter = images.stream().map((img) -> img.attr("src")).filter((src) -> (src.toLowerCase().contains("brainhoney"))).map((_item) -> 1).reduce(bhimgesCounter, Integer::sum);
        return bhimgesCounter;
    }

    private int numBXLinks() {
        int bxlinksCounter = 0;
        bxlinksCounter = links.stream().map((a) -> a.attr("href")).filter((href) -> (href.toLowerCase().contains("box.com"))).map((_item) -> 1).reduce(bxlinksCounter, Integer::sum);
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
        if (headers == "") {
            headers = "NONE";
        }
        if ("123456".indexOf(headers) == 0) {
            return "Good: " + headers;
        } else {
            return "Bad: " + headers;
        }
    }

    private int numBadTargets() {
        int tgCounter = 0;
        tgCounter = links.stream().filter((a) -> (!a.attr("target").toLowerCase().contains("_blank".toLowerCase()))).map((_item) -> 1).reduce(tgCounter, Integer::sum);
        return tgCounter;
    }

    private int numDivs() {
        int divCounter = 0;
        divCounter = divs.stream().filter((div) -> (!div.hasAttr("id"))).map((_item) -> 1).reduce(divCounter, Integer::sum);
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
}
