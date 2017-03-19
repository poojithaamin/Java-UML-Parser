package umlparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class UmlClassParser {
	File srcFolder;
	String outputFile;
    HashMap<String, Boolean> map;
    HashMap<String, String> mapClassConn;
	ArrayList<CompilationUnit> compilationunitArray;
	String output;
	
	UmlClassParser(File srcFolder, String outputFile){
		this.srcFolder = srcFolder;
		this.outputFile = outputFile;
		System.out.println("JavaParser Module");
	}

	public void parse() throws Exception{
		compilationunitArray=readFiles(srcFolder);
        for (CompilationUnit cu : compilationunitArray)
            output= getDetails(cu);
		System.out.println("Output is "+output);
		
	}
	
   
    private String getDetails(CompilationUnit cu2) {
    	String output="";
        	List<TypeDeclaration<?>> gt = cu2.getTypes();
            for (Node n : gt) {
                ClassOrInterfaceDeclaration coi = (ClassOrInterfaceDeclaration) n;
                if (coi.isInterface()){
                	System.out.println(coi.getName()+" "+coi.isInterface());
                	output+=" "+coi.getName();
                }
                else{
                	System.out.println(coi.getName()+" "+coi.isInterface());
                	output+="["+coi.getName()+"|";
                	List<BodyDeclaration<?>> bd = ((TypeDeclaration<?>) n).getMembers();                	
                	for(BodyDeclaration<?> b: bd)
    				{
                		System.out.println("b is "+b.toString());
    					if(b instanceof FieldDeclaration)
    					{											
    						EnumSet<Modifier> modifier = ((FieldDeclaration) b).getModifiers();		
    						if(modifier.contains(Modifier.PUBLIC))
    						{
    						output+="+";
    						System.out.println("output is "+output);
    						}
    						else if(modifier.contains(Modifier.PRIVATE))	
    						{
    						output+="-";
    						System.out.println("output is "+output);
    						
    						}
    						
    						System.out.println("name is"+((FieldDeclaration) b).getChildNodes().get(0).toString());
    						String fieldName = ((FieldDeclaration) b).getChildNodes().get(0).toString();
    						System.out.println("variable is "+fieldName);
    						output+=fieldName+':';
    						System.out.println("output is "+output+':');
    						
    					}
                }
            }
            
        }       
        return output;
    }
    
	public ArrayList<CompilationUnit> readFiles(File srcFolder) throws Exception{
		ArrayList<CompilationUnit> compilationunitArray = new ArrayList<CompilationUnit>();
		List<String> results = new ArrayList<String>();
		File[] srcFiles = srcFolder.listFiles();
		System.out.println(srcFiles[0]);
		FileReader data = null;
        try{
		for (File file : srcFiles) {			
		    if (file.isFile() && file.getName().endsWith(".java")) {
		        results.add(file.getName());
		        data = new FileReader(file);	
		        //FileInputStream in = new FileInputStream(file);
		        System.out.println(file.getName());
		        CompilationUnit compilationUnit = JavaParser.parse(data);
		        //CompilationUnit compilationUnit = JavaParser.parse(in);
		        //System.out.println(compilationUnit.getClassByName("A"));
		       // System.out.println(compilationUnit.getTypes());
		        System.out.println(compilationUnit.toString());
		        compilationunitArray.add(compilationUnit);
		        
		    }
		}
		
        }
        finally {
           data.close();
        }
        return compilationunitArray;
	}
}
