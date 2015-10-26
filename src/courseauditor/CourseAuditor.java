/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package courseauditor;

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
        test.audit("test.html");
    }
    
}
