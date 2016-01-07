/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package courseauditor;

import CourseCleaner.CourseCleaner;
import java.io.*;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

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
        fixthecourse();
    }

    public static void fixthecourse() throws FileNotFoundException, IOException {
        String content = new Scanner(new File("imsmanifest.xml")).useDelimiter("\\Z").next();
        Document xmlDoc = Jsoup.parse(content, "", Parser.xmlParser());
        Elements resources = xmlDoc.select("resource");
        Elements items = xmlDoc.getElementsByTag("item");
        CourseCleaner course = new CourseCleaner();
        String title;
        for (Element e : resources) {
            String type = e.attr("d2l_2p0:material_type");
            if ("content".equals(type)) {
                String fpath = e.attr("href");
                for (Element d : items) {
                    if (d.hasAttr("identifierref") && (d.attr("identifierref").equals(e.attr("identifier")) && fpath.contains(".html"))) {
                        title = d.child(0).ownText();
                        File input = new File(fpath);
                        if (input.exists()) {
                            course.cleanDoc(fpath, title);
                            Writer writer = new PrintWriter(fpath);
                            System.out.println("Document Successfuly Cleaned");
                            System.out.println("\tFile Name: " + fpath);
                            writer.write(course.getfixedsource());
                            writer.close();
                            System.out.println("\tFile Saved\n");
                        }

                    }
                }
            }
        }
    }
}
