/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CourseDocument;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
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
public class CourseDocument {

    private Document doc, finaldoc, newdoc;
    private File input;
    private Element body;
    private Elements links, images;
    private String returnString, title, orgunitid, fPath, name, orgUnitId, startDate, endDate, dueDate;

    // Set initial values, all values MUST be filled before moving on.
    public void setInitFilePath(String FilePath) {
        fPath = FilePath;
    }

    public void setInitName(String docName) {
        name = docName;
    }

    public void setInitOrgUnitID(String oui) {
        orgUnitId = oui;
    }

    public void setInitDates(String start, String end, String due) {
        startDate = start;
        endDate = end;
        dueDate = due;
    }

    public void init() throws IOException {
        input = new File(fPath);
        if (input.exists()) {
            doc = Jsoup.parse(input, "UTF-8");
            body = doc.getElementsByTag("body").first();
            links = body.getElementsByTag("a");
            images = body.getElementsByTag("img");
        }
    }

    // Clean the code
    public void clean() throws IOException {
        parseClean(doc.toString());
        parseClean(doc.toString());
        parseClean(doc.toString());
        Writer writer = new PrintWriter(fPath);
        writer.write(doc.toString());
        writer.close();
    }

    private String parseClean(String input) {
        doc = Jsoup.parse(input, "UTF-8");
        if (!doc.select("title").isEmpty()) {
            doc.select("title").first().text(title);
        } else {
            doc.select("head").after("<title>" + title + "</title>");
        }
        Elements divs = doc.select("div[i]");
        for (Element e : divs) {
            if ("body".equals(e.attr("id")) || "header".equals(e.attr("id")) || "main".equals(e.attr("id")) || "article".equals(e.attr("id")) || "decorative".equals(e.attr("id")) || "container".equals(e.attr("id"))) {

            } else {
                e.removeAttr("id");
            }
        }
        doc.select("span[style*='font-weight: bold']").wrap("<strong></strong>");
        doc.select("span[style*='font-weight: bold']").removeAttr("style");
        doc.select("p[style*='font-weight: bold']").wrap("<strong></strong>");
        doc.select("p[style*='font-weight: bold']").removeAttr("style");
        doc.select("div:not([id]):empty").unwrap();
        doc.select("div:not([id])").wrap("<p></p>");
        doc.select("div:not([id])").unwrap();
        doc.select("img:not([alt])").attr("alt", "");
        doc.select("b").wrap("<strong></strong>");
        doc.select("b").unwrap();
        doc.select("i").wrap("<em></em>");
        doc.select("i").unwrap();
        doc.select("span:not([style])").unwrap();
        doc.select("a:not([target='_blank'])").attr("target", "_blank");
        doc.select("strong:empty, em:empty, br, b:empty").unwrap();
        doc.select("iframe[src*='youtube.com/embed/']").attr("height", "500px");
        doc.select("iframe[src*='youtube.com/embed/']").attr("width", "100%");
        doc.select("iframe[src*='content.byui.edu/file/']").attr("height", "500px");
        doc.select("iframe[src*='content.byui.edu/file/']").attr("width", "100%");
        doc.select("a[href*=\"/d2l/le/calendar/\"]").unwrap();
        doc.select("a:not([href])").unwrap();
        doc = Jsoup.parse(replaceStrings(doc.toString()), "UTF-8");
        return doc.toString();
    }

    private String replaceStrings(String input) {
        String output = input;
        output = output.replaceAll("&nbsp;", " ");
        output = output.replaceAll("&ldquo;", "\"");
        output = output.replaceAll("&rdquo;", "");
        output = output.replaceAll("&lsquo;", "");
        output = output.replaceAll("&rsquo;", "");
        output = output.replaceAll("&ndash;", "");
        output = output.replaceAll("&mdash;", "");
        output = output.replaceAll("<p></p>", "");
        output = output.replaceAll("<a></a>", "");
        output = output.replaceAll("<a> </a>", "");
        output = output.replaceAll("<h1></h1>", "");
        output = output.replaceAll("<h2></h2>", "");
        output = output.replaceAll("<h3></h3>", "");
        output = output.replaceAll("<h4></h4>", "");
        output = output.replaceAll("<h5></h5>", "");
        output = output.replaceAll("<h6></h6>", "");
        output = output.replaceAll("<p> </p>", "");
        output = output.replaceAll("<h1> </h1>", "");
        output = output.replaceAll("<h2> </h2>", "");
        output = output.replaceAll("<h3> </h3>", "");
        output = output.replaceAll("<h4> </h4>", "");
        output = output.replaceAll("<h5> </h5>", "");
        output = output.replaceAll("<h6> </h6>", "");
        output = output.replaceAll("  +", "");
        return output;
    }

    // Return what used to be course audit info.
    public String getHtmlTitle() {
        Elements titleE = doc.select("title");
        if (titleE.isEmpty()) {
            return "ERROR: COULD NOT READ TITLE";
        } else {
            String htmlTitle = titleE.first().text().replace(",", "");
            htmlTitle = htmlTitle.replaceAll("\t", "");
            htmlTitle = htmlTitle.replaceAll("\n", "");
            if (htmlTitle == null ? name == null : htmlTitle.toLowerCase().trim().equals(name.trim().toLowerCase())) {
                return "Matching";
            } else {
                return htmlTitle;
            }
        }
    }

    public String getFilePath() {
            return fPath;
    }

    public String verifyHeaders() {
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
        if ("".equals(headers)) {
            headers = "NONE";
        }
        if ("123456".indexOf(headers) == 0) {
            return "Good: " + headers;
        } else {
            return "Bad: " + headers;
        }
    }

    public String numBenLinks() {
        int bhlinksCounter = 0;
        bhlinksCounter = links.stream().map((a) -> a.attr("href")).filter((href) -> (href.toLowerCase().contains("courses.byui.edu"))).map((_item) -> 1).reduce(bhlinksCounter, Integer::sum);
        return bhlinksCounter + "";
    }

    public String numBHLinks() {
        int bhlinksCounter = 0;
        bhlinksCounter = links.stream().map((a) -> a.attr("href")).filter((href) -> (href.toLowerCase().contains("brainhoney"))).map((_item) -> 1).reduce(bhlinksCounter, Integer::sum);
        return bhlinksCounter + "";
    }

    public String numBoxLinks() {
        int bxlinksCounter = 0;
        bxlinksCounter = links.stream().map((a) -> a.attr("href")).filter((href) -> (href.toLowerCase().contains("box.com"))).map((_item) -> 1).reduce(bxlinksCounter, Integer::sum);
        return bxlinksCounter + "";
    }

    public String numCalLinks() {
        int callinkscounter = 0;
        for (Element a : links) {
            if (a.attr("href").contains("/d2l/le/calendar/")) {
                callinkscounter++;
            }
        }
        return callinkscounter + "";
    }
    
    public String numWrongCourseLinks() {
        int wronglinkscounter = 0;
        wronglinkscounter = links.stream().filter((a) -> (a.attr("href").contains("/d2l/") && !a.attr("href").contains(orgUnitId))).map((_item) -> 1).reduce(wronglinkscounter, Integer::sum);
        return wronglinkscounter + "";
    }

    public String numEmptyLinks() {
        int emptyLinks = 0;
        for (Element a : links) {
            boolean noHref = a.attr("href").isEmpty();
            boolean noLinkText = a.text().isEmpty();
            boolean hasHref = a.hasAttr("href");
            if (noHref == true || noLinkText == true || hasHref == false) {
                emptyLinks++;
            }
        }
        return emptyLinks + "";
    }

    public String numBadLinkTargets() {
        int tgCounter = 0;
        tgCounter = links.stream().filter((a) -> (!a.attr("target").toLowerCase().contains("_blank".toLowerCase()))).map((_item) -> 1).reduce(tgCounter, Integer::sum);
        return tgCounter + "";
    }

    public String numDivs() {
        return "";
    }

    public String numCssBolds() {
        int bCounter = 0;
        Pattern dueSaturday = Pattern.compile("font-weight\\: bold");
        Matcher m = dueSaturday.matcher(doc.toString());
        while (m.find()) {
            bCounter++;
        }
        return bCounter + "";
    }

    public String countStringOccurence() {
        return "";
    }

    public String numBadImgWidth() {
        int imgCounter = 0;
        for (Element img : images) {
            String width = img.attr("width");
            if (!width.toLowerCase().contains("%") && !img.attr("src").toLowerCase().contains("banner")) {
                imgCounter++;
            }
        }
        return imgCounter + "";
    }

    public String numBHImg() {
        int bhimgesCounter = 0;
        bhimgesCounter = images.stream().map((img) -> img.attr("src")).filter((src) -> (src.toLowerCase().contains("brainhoney"))).map((_item) -> 1).reduce(bhimgesCounter, Integer::sum);
        return bhimgesCounter + "";
    }

}
