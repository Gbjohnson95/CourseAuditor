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
import pageauditor.PageAuditorCCT;
import pageauditor.PageAuditorCI;

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
        // Make reports folder
        File reports = new File("Reports");
        if (!reports.exists()) {
            reports.mkdir();
        }
        
        

        /*
        parseManifestAndRunCI("Reports/CourseAuditCI Pre-Fix.csv");
        parseManifestAndRunCCT("Reports/CourseAuditCCT Pre-Fix.csv");
        fixthecourse();
        parseManifestAndRunCI("Reports/CourseAuditCI Post-Fix.csv");
        parseManifestAndRunCCT("Reports/CourseAuditCCT Post-Fix.csv");
        printDates("Reports/Dates.csv");
        */
    }

    public static void printDates(String resultname) throws IOException {
        String content = new Scanner(new File("imsmanifest.xml")).useDelimiter("\\Z").next();
        Document xmlDoc = Jsoup.parse(content, "", Parser.xmlParser());
        Elements items = xmlDoc.getElementsByTag("item");
        String printString = "Title,Start Date,End Date,Due Date,\n";
        String date_start, date_end, date_due, title;
        for (Element d : items) {
            title = d.child(0).ownText();
            date_start = d.attr("date_start").replace("T", " ");
            date_end = d.attr("date_end").replace("T", " ");
            date_due = d.attr("date_due").replace("T", " ");
            if ((date_start.length() + date_end.length() + date_due.length()) > 0) {
                printString += "\"" + title + "\"," + date_start + "," + date_end + "," + date_due + ",\n";
            }
        }

        try (PrintWriter out = new PrintWriter(resultname)) {
            out.print(printString);
        }
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
                            writer.write(course.getfixedsource());
                            writer.close();
                        }

                    }
                }
            }
        }
    }

    public static void parseManifestAndRunCI(String resultname) throws IOException {
        String content = new Scanner(new File("imsmanifest.xml")).useDelimiter("\\Z").next();
        Document xmlDoc = Jsoup.parse(content, "", Parser.xmlParser());
        Elements resources = xmlDoc.select("resource");
        Elements items = xmlDoc.getElementsByTag("item");
        String printString = "Course Document Title,HTML Title,Wrong OrgUnitID,IL2 Links,Box Links,Benjamin Links,Empty Links,IL2 Images,CSS Bold,Potential Code Mistakes\n";
        //String printString = "Title,Benjamin Links,Course\n";
        Element manifest = xmlDoc.select("manifest").first();

        String title;
        String orgunitid = manifest.attr("identifier").substring(4);
        //System.out.print("OrgUnitId: " + orgunitid);
        //*
        String date_start, date_end, date_due;
        PageAuditorCI audit = new PageAuditorCI();
        for (Element e : resources) {
            String type = e.attr("d2l_2p0:material_type");
            if ("content".equals(type)) {
                String fpath = e.attr("href");
                for (Element d : items) {
                    if (d.hasAttr("identifierref") && (d.attr("identifierref").equals(e.attr("identifier")) && fpath.contains(".html"))) {
                        //System.out.println(date_start + date_end + date_due);
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

    public static void parseManifestAndRunCCT(String resultname) throws IOException {
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
        PageAuditorCCT audit = new PageAuditorCCT();
        for (Element e : resources) {
            String type = e.attr("d2l_2p0:material_type");
            if ("content".equals(type)) {
                String fpath = e.attr("href");
                for (Element d : items) {
                    if (d.hasAttr("identifierref") && (d.attr("identifierref").equals(e.attr("identifier")) && fpath.contains(".html"))) {
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
