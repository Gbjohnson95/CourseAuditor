/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package courseauditor;

import java.io.File;
import java.io.IOException;
import pageauditor.PageAuditor;

/**

 @author gbjohnson
 */
public class CourseAuditor {

    /**
     @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        PageAuditor test = new PageAuditor();
        test.printHeader();
        
        File file = new File("Course Files");
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.exists()) {
                String filename = f.getName();
                String html = ".html";
                if (filename.toLowerCase().contains(html.toLowerCase())){
                    test.audit(f.getPath());
                }
            }
        }
    }
    
}
