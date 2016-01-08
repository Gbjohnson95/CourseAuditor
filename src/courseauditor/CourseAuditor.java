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
        //parseManifestAndRun("before.csv");
        //fixthecourse();
        parseManifestAndRun("after.csv");
    }

    public static void fixthecourse() throws FileNotFoundException, IOException {
        String content = new Scanner(new File("imsmanifest.xml")).useDelimiter("\\Z").next();
        Document xmlDoc = Jsoup.parse(content, "", Parser.xmlParser());
        Elements resources = xmlDoc.select("resource");
        Elements items = xmlDoc.getElementsByTag("item");
        Element manifest = xmlDoc.select("manifest").first();
        String orgunitid = manifest.attr("identifier").substring(4);
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
                            course.cleanDoc(fpath, title, orgunitid);
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

    public static void parseManifestAndRun(String resultname) throws IOException {
        String content = new Scanner(new File("imsmanifest.xml")).useDelimiter("\\Z").next();
        Document xmlDoc = Jsoup.parse(content, "", Parser.xmlParser());
        Elements resources = xmlDoc.select("resource");
        Elements items = xmlDoc.getElementsByTag("item");
        String printString = "Title,HTML Title,Bad OUI,Calender Links,BH Links,Box Links,Benjamin Links,Bad Link Targets,Empty Links,BH Images,Image Width,Bolds,Spans,Bad Tags,Divs,Br,BHVars,Mentions Saturday,Header Order,Template,Filepath\n";
        //String printString = "Title,Benjamin Links,Course\n";
        Element manifest = xmlDoc.select("manifest").first();

        String title;
        String orgunitid = manifest.attr("identifier").substring(4);
        //System.out.print("OrgUnitId: " + orgunitid);
        //*
        String date_start, date_end, date_due;
        PageAuditor audit = new PageAuditor();
        for (Element e : resources) {
            String type = e.attr("d2l_2p0:material_type");
            if ("content".equals(type)) {
                String fpath = e.attr("href");
                for (Element d : items) {
                    if (d.hasAttr("identifierref") && (d.attr("identifierref").equals(e.attr("identifier")) && fpath.contains(".html"))) {
                        date_start = d.attr("date_start");
                        date_end   = d.attr("date_end");
                        date_due   = d.attr("date_due");
                        System.out.println(date_start + date_end + date_due);
                        title = d.child(0).ownText();
                        audit.audit(fpath, title, orgunitid);
                        printString += audit.getMetrics();
                    }
                }
            }
        }
        try (PrintWriter out = new PrintWriter(resultname)) {
            out.print(printString);
        }
    }
}
