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

/**
 *
 * @author gbjohnson
 */
public class CourseCleaner {

    private Document doc, finaldoc;
    private Element body;
    private String returnString, title;

    public void cleanDoc(String filename, String titletext) throws IOException {
        File input = new File(filename);

        title = titletext;
        if (input.exists()) {
            doc = Jsoup.parse(input, "UTF-8");
            body = doc.getElementsByTag("body").first();
            cleancode();
        }
    }

    private void cleancode() {
        // Sync Title
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
        doc.select("div:empty[id!='main'][id!='header'][id!='article']").unwrap();

        // Replace other divs with p tags
        doc.select("div[id!='main'][id!='header'][id!='article']").wrap("<p></p>");
        doc.select("div[id!='main'][id!='header'][id!='article']").unwrap();

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
        
        // P tags encllosed by other p tags
        //doc.select("p > p").unwrap();
        
        
        returnString = doc.toString();
        returnString = returnString.replaceAll("(</?(?:b|i|u|p|div)>)\\1+", "$1").replaceAll("</(b|i|u|p|div)><\\1>", "");
        returnString = returnString.replaceAll("&amp;", "&");
        returnString = returnString.replaceAll("&nbsp;", " ");
        returnString = returnString.replaceAll("&ldquo;", "\"");
        returnString = returnString.replaceAll("&rdquo;", "");
        returnString = returnString.replaceAll("&lsquo;", "");
        returnString = returnString.replaceAll("&rsquo;", "");
        returnString = returnString.replaceAll("&ndash;", "");
        returnString = returnString.replaceAll("&mdash;", "");
        returnString = returnString.replaceAll("<p></p>", "");
        returnString = returnString.replaceAll("<a></a>", "");
        returnString = returnString.replaceAll("<a> </a>", "");
        returnString = returnString.replaceAll("<h1></h1>", "");
        returnString = returnString.replaceAll("<h2></h2>", "");
        returnString = returnString.replaceAll("<h3></h3>", "");
        returnString = returnString.replaceAll("<h4></h4>", "");
        returnString = returnString.replaceAll("<h5></h5>", "");
        returnString = returnString.replaceAll("<h6></h6>", "");
        returnString = returnString.replaceAll("<p> </p>", "");
        returnString = returnString.replaceAll("<h1> </h1>", "");
        returnString = returnString.replaceAll("<h2> </h2>", "");
        returnString = returnString.replaceAll("<h3> </h3>", "");
        returnString = returnString.replaceAll("<h4> </h4>", "");
        returnString = returnString.replaceAll("<h5> </h5>", "");
        returnString = returnString.replaceAll("<h6> </h6>", "");
        
    }

    public String getfixedsource() {
        return returnString;
    }
}
