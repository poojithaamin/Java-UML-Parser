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
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.Type;
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
		UmlDiagram.generatePNG(output, outputFile);
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
    						EnumSet<Modifier> fieldModifier = ((FieldDeclaration) b).getModifiers();		
    						if(fieldModifier.contains(Modifier.PUBLIC))
    						{
    						output+="+";
    						System.out.println("output is "+output);
    						}
    						else if(fieldModifier.contains(Modifier.PRIVATE))	
    						{
    						output+="-";
    						System.out.println("output is "+output);
    						
    						}
    						
    						/*
    						System.out.println("name is"+((FieldDeclaration) b).getChildNodes().get(0).toString());
    						String fieldName = ((FieldDeclaration) b).getChildNodes().get(0).toString();
    						System.out.println("variable is "+fieldName);
    						output+=fieldName+':';
    						System.out.println("output is "+output+':');
    						//System.out.println("type is"+((FieldDeclaration) b).getClass()
    						 * */
    						 
                            
    						NodeList<VariableDeclarator> variableData= ((FieldDeclaration) b).getVariables();
    						for (Node n1 : variableData) {
    							VariableDeclarator v = (VariableDeclarator) n1;
    							String fieldName=v.getType().toString();
    							String fieldType=v.getName().toString();
    							System.out.println("Finally"+v.getType());
    							output+=fieldName+':';
    							output+=fieldType+';';
    							System.out.println("Finally"+v.getName());
    						}
    						
    					}
    					
    					if(b instanceof MethodDeclaration) 
    					{
    						EnumSet<Modifier> methodModifier = ((MethodDeclaration) b).getModifiers();	
    						if(methodModifier.contains(Modifier.PUBLIC))
    						{
    						output+="+";
    						System.out.println("output is "+output);
    						}  
    						else if(methodModifier.contains(Modifier.PRIVATE))	
    						{
    						output+="-";
    						System.out.println("output is "+output);
    						
    						}
    						
    						String methodName = ((MethodDeclaration) b).getName().toString();
    						String methodReturnType =((MethodDeclaration) b).getType().toString();
    						NodeList<Parameter> methodParameter= ((MethodDeclaration) b).getParameters();
    						if (methodParameter.isEmpty()){
	    							output+=methodName+"("+")";
	    							output+=":"+methodReturnType+";";
    						}
    						else{
		    						for (Node n2 : methodParameter) {
		    							String methodParam = n2.toString();
		    							System.out.println("parameter"+methodParam);
		    							output+=methodName+"("+methodParam+")";
		    							output+=":"+methodReturnType+";";
		    						    }
    						}
    						
    					}
    					
    					
    				}
                }
            }  
        output+=']';
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
