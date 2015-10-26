/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pageauditor;

import java.io.File;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**

 @author gbjohnson
 */
public class PageAuditor {

    Document doc;
    String fname;
    public void audit(String filename) throws IOException {
        File input = new File(filename);
        fname = filename;
        doc = Jsoup.parse(input, "UTF-8");
        printMetrics();
        checkForBrainHoney();
        //checkForBox();
        //checkForDeprecatedTags();
    }
    
    private void printMetrics() {
        Element titleElement = doc.select("title").first();
        String title = titleElement.text();
        writeToLog("The File Name is : " + fname);
        writeToLog("The HTML Title is: " + title);
        
    }

    private void checkForBrainHoney() {
        Elements bhlinks = doc.body().getAllElements().select("a[href*='brainhoney']");
        Elements bhimges = doc.select("img[src*='brainhoney']");
        
        
        writeToLog("\t- There are " + doc.select("a[href*='brainhoney']").size() + " BrainHoney links in this document");
        writeToLog("\t- There are " + doc.select("img[src*='brainhoney']").size() + " BrainHoney images in this document");
        
        
    }

    private void checkForBox() {
        Elements bxlinks = doc.select("a[href*='box']");
    }

    private void checkForDeprecatedTags() {
        // Looks for <acronym>, <applet>, <basefont>, <big>, <center>, <dir>,
        // <font>, <frame>, <frameset>, <isindex>, <noframes>, <s>, <strike>, 
        // <tt>, <u>.
    }

    private void writeToLog(String text) {
        System.out.println(text);
    }

}
