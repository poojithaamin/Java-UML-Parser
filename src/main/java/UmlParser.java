package umlparser;

import java.io.File;

public class UmlParser {

	public static void main(String[] args) throws Exception {
		System.out.println(args[0]);
        if (args.length == 3) {
        		File srcFolder = new File(args[0]);
        		if(!srcFolder.exists()) {
                	System.err.println ("Invalid folder");
                	System.exit(0);
        		}
        		else {
				System.out.println(+','+args[1]);
				if (args[2]=="class"){ 	
					UmlClassParser jp = new UmlClassParser(srcFolder,args[1]);
					jp.parse();	
			        }
				else if(args[2]=="sequence"){
			    }	                	
                }
        }        		
        else {
        		System.err.println ("Invalid Arguments. Enter the source folder and output file name");
        }     	

	}

}
