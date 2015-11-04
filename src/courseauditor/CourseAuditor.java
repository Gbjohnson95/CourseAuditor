/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package courseauditor;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import pageauditor.PageAuditor;

/**
 *
 * @author gbjohnson
 */
public class CourseAuditor {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        parseManifestAndRun();
    }

    public static void parseManifestAndRun() throws IOException {
        String content = new Scanner(new File("imsmanifest.xml")).useDelimiter("\\Z").next();
        Document xmlDoc = Jsoup.parse(content, "", Parser.xmlParser());
        Elements resources = xmlDoc.select("resource");
        Elements items = xmlDoc.getElementsByTag("item");

        String title;
        PageAuditor audit = new PageAuditor();
        audit.printHeader();
        for (Element e : resources) {
            String type = e.attr("d2l_2p0:material_type");
            if ("content".equals(type)) {
                String ident = e.attr("identifier");
                String fpath = e.attr("href");
                for (Element d : items) {
                    if (d.hasAttr("identifierref")) {
                        String ident1 = d.attr("identifierref");
                        if (ident1.equals(ident)) {
                            if (fpath.contains(".html")) { // Only html gets passed to the html
                                title = d.child(0).ownText();
                                audit.audit(fpath, title);
                            }
                        }
                    }
                }
            }
        }
    }
}
