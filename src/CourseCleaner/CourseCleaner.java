/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CourseCleaner;

import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;




// THIS DOCUMENT IS BEING OBSOLETED IN FAVOR OF COURSEDOCUMENT



/**
 *
 * @author gbjohnson
 */
public class CourseCleaner {

    private Document doc, finaldoc, newdoc;
    private Element body;
    private String returnString, title, orgunitid;

    public void cleanDoc(String filename, String titletext, String oui) throws IOException {
        File input = new File(filename);

        orgunitid = oui;

        title = titletext;
        if (input.exists()) {
            doc = Jsoup.parse(input, "UTF-8");
            body = doc.getElementsByTag("body").first();
            parseClean(doc.toString());
            parseClean(doc.toString());
        }
    }

    private String parseClean(String input) {
        // Sync Title
        doc = Jsoup.parse(input, "UTF-8");
        doc = Jsoup.parse(divWhiteList(doc.toString()), "UTF-8");
        if (!doc.select("title").isEmpty()) {
            doc.select("title").first().text(title);
        } else {
            doc.select("head").after("<title>" + title + "</title>");
        }

        // Remove bolds
        doc.select("span[style*='font-weight: bold']").wrap("<strong></strong>");
        doc.select("span[style*='font-weight: bold']").removeAttr("style");
        doc.select("p[style*='font-weight: bold']").wrap("<strong></strong>");
        doc.select("p[style*='font-weight: bold']").removeAttr("style");

        //Remove empty divs
        doc.select("div:not([id]):empty").unwrap();

        // Replace other divs with p tags
        doc.select("div:not([id])").wrap("<p></p>");
        doc.select("div:not([id])").unwrap();

        // Add alt attribute to images
        doc.select("img:not([alt])").attr("alt", "");

        // Replace outdated tags
        doc.select("b").wrap("<strong></strong>");
        doc.select("b").unwrap();
        doc.select("i").wrap("<em></em>");
        doc.select("i").unwrap();

        // Remove spans
        //doc.select("span:not([style])").unwrap();
        doc.select("span:not([style])").unwrap();

        // Links with bad tags
        doc.select("a:not([target='_blank'])").attr("target", "_blank");

        // Empty Tags
        doc.select("strong:empty, em:empty, br, b:empty").unwrap();

        // Set correct height and width on youtube videos
        doc.select("iframe[src*='youtube.com/embed/']").attr("height", "500px");
        doc.select("iframe[src*='youtube.com/embed/']").attr("width", "100%");

        // Set the correct height and with on equilla videos
        doc.select("iframe[src*='content.byui.edu/file/']").attr("height", "500px");
        doc.select("iframe[src*='content.byui.edu/file/']").attr("width", "100%");

        // Rip callender links
        doc.select("a[href*=\"/d2l/le/calendar/\"]").unwrap();
        // Links with no href
        doc.select("a:not([href])").unwrap();

        
        return divWhiteList(doc.toString());
    }
    
    private String divWhiteList(String input) {
        Document divdoc = Jsoup.parse(input, "UTF-8");
        Elements divs = doc.select("div[id]");
        for (Element e : divs) {
            if ( e.hasAttr("id") ) {
                if ("body".equals(e.attr("id")) || "header".equals(e.attr("id")) || "main".equals(e.attr("id"))  || "article".equals(e.attr("id"))  || "decorative".equals(e.attr("id"))  || "container".equals(e.attr("id"))  ) {
                    
                } else {
                    e.removeAttr("id");
                }
            }
        }
        return divdoc.toString();
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
    
    private String replaceStringLooper (String input) {
        newdoc = Jsoup.parse(input, "UTF-8");
        
        for ( int i = 0; i < 5; i++ ) {
            newdoc = Jsoup.parse(replaceStrings(newdoc.toString()), "UTF-8");
        }
        return newdoc.toString();
    }

    public String getfixedsource() {
        replaceStringLooper(doc.toString());
        return newdoc.toString();
    }
}
