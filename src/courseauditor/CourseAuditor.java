/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package courseauditor;

import CourseCleaner.CourseCleaner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Scanner;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
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
    private static String printString;

    public static void main(String[] args) throws IOException, ZipException {
        // Make reports folder
        //File reports = new File("Reports");
        //if (!reports.exists()) {
        //    reports.mkdir();
        //}

        //parseManifestAndRunCCT("Reports/Audit.csv");
        //printDates("Reports/Dates.csv");
        getZipsAndRunCourses();

        //parseManifestAndRunCCT("Audit.csv", "B 211");
    }

    public static void getZipsAndRunCourses() throws IOException, ZipException {
        printString = "Org Unit,Date,Title,HTML Title,Bad OUI,Calender Links,Non-Dymanmic Links,BH Links,Box Links,Benjamin Links,Bad Link Targets,Empty Links,BH Images,Image Width,Bolds,Spans,Bad Tags,Divs,BHVars,Mentions Saturday,Header Order,Template,Filepath\n";
        File curDur = new File(".");

        File[] curDurFiles = curDur.listFiles();

        for (File f : curDurFiles) {
            if (f.isFile() && f.getName().endsWith(".zip")) {
                ZipFile zf = new ZipFile(f);
                File outFolder = new File(f.getName().replace(".zip", ""));
                if (!outFolder.exists()) {
                    outFolder.mkdir();
                }
                zf.extractAll(f.getName().replace(".zip", ""));
                parseManifestAndRunCCT("Audit.csv", f.getName().replace(".zip", ""));
            }
        }
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
                            try (Writer writer = new PrintWriter(fpath)) {
                                writer.write(course.getfixedsource());
                            }
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

        Element manifest = xmlDoc.select("manifest").first();
        String title;
        String orgunitid = manifest.attr("identifier").substring(4);
        String date_start, date_end, date_due;
        PageAuditorCI audit = new PageAuditorCI();
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

    public static void parseManifestAndRunCCT(String resultname, String path) throws IOException {
        String content = new Scanner(new File(path + "/imsmanifest.xml")).useDelimiter("\\Z").next();
        Document xmlDoc = Jsoup.parse(content, "", Parser.xmlParser());
        Elements resources = xmlDoc.select("resource");
        Elements items = xmlDoc.getElementsByTag("item");
        Element manifest = xmlDoc.select("manifest").first();
        String title;
        String orgunitid = manifest.attr("identifier").substring(4);
        PageAuditorCCT audit = new PageAuditorCCT();
        for (Element e : resources) {
            String type = e.attr("d2l_2p0:material_type");
            if ("content".equals(type)) {
                String fpath = e.attr("href");
                for (Element d : items) {
                    if (d.hasAttr("identifierref") && (d.attr("identifierref").equals(e.attr("identifier")) && fpath.contains(".html"))) {
                        title = d.child(0).ownText();
                        audit.audit(path + "/" + fpath, title, orgunitid);
                        printString += audit.getMetrics();
                    }
                }
            }
        }
        try (PrintWriter out = new PrintWriter(new FileOutputStream(new File(resultname), true))) {
            out.print(printString);
        }
        printString = "";
    }
}
