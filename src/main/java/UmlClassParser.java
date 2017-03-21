package umlparser;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	HashMap<String, Boolean> listClassInterface=new HashMap<String, Boolean>();
	HashMap<String, String> association=new HashMap<String, String>();
	List<String> replaceGetSet=new ArrayList<String>();
	
	UmlClassParser(File srcFolder, String outputFile){
		this.srcFolder = srcFolder;
		this.outputFile = outputFile;
		System.out.println("JavaParser Module");
	}
	
	public void parse() throws Exception{
		compilationunitArray=readFiles(srcFolder);
		getClassInterfaceName(compilationunitArray);
        for (CompilationUnit cu : compilationunitArray)
            output+= getDetails(cu);		   
		UmlDiagram.generatePNG(output, outputFile);
	}
	
	
   private void getClassInterfaceName(ArrayList<CompilationUnit> compilationunitArray){	
	    for (CompilationUnit cu2 : compilationunitArray) {
	    	List<TypeDeclaration<?>> gt1 = cu2.getTypes();  
	    	for (Node n : gt1) {
	    		ClassOrInterfaceDeclaration coi1 = (ClassOrInterfaceDeclaration) n;
	    		listClassInterface.put(coi1.getName().toString(), coi1.isInterface());	
	    	}	    
	    }	   	    	  
   }
    
    private String getDetails(CompilationUnit cu2) {
    	    String output="";
        	replaceGetSet.clear();
        	List<TypeDeclaration<?>> gt = cu2.getTypes();        
            for (Node n : gt) {
                ClassOrInterfaceDeclaration coi = (ClassOrInterfaceDeclaration) n;
                String className=coi.getName().toString();
                if (coi.isInterface()){
                	output+=" "+coi.getName();
                }
                else{
                	System.out.println(coi.getName()+" "+coi.isInterface());
                	output+="["+coi.getName()+"|";
                	List<BodyDeclaration<?>> bd = ((TypeDeclaration<?>) n).getMembers();                	
                	for(BodyDeclaration<?> b: bd){                		
                		String fieldMod="";
                		//Field declaration
    					if(b instanceof FieldDeclaration && b instanceof TypeDeclaration==false){									
    						//Field modifier
    						EnumSet<Modifier> fieldModifier = ((FieldDeclaration) b).getModifiers();		
    						if(fieldModifier.contains(Modifier.PUBLIC)){
    						    fieldMod="+";    						
    						}
    						else if(fieldModifier.contains(Modifier.PRIVATE)){
    							fieldMod="-";       							
    						}    						
    						 
                            //Field name and type
    						NodeList<VariableDeclarator> variableData= ((FieldDeclaration) b).getVariables();
    						for (Node n1 : variableData) {
    							VariableDeclarator v = (VariableDeclarator) n1;
    							String fieldName=v.getName().toString();
    							String fieldType=v.getType().toString();

    							if (!listClassInterface.containsKey(fieldType) && !fieldType.contains("<")){
    								output+=fieldMod+fieldName+':'+fieldType+';';
        							output=output.replace("[]", "(*)");        							        							        							
    							}
    							
    							else if (listClassInterface.containsKey(fieldType)) {
    								System.out.println("Association");
    								association.put(className.concat(fieldType), "1-0..*");
    							}
    									
    							else if(listClassInterface.containsKey(fieldType.substring(fieldType.indexOf("<")+1,fieldType.indexOf(">"))))
    							{	
    							  System.out.println("Dependent");
    							}
    							
    							else {
    							 							
    						    }
    							
    						}
    					}
    					 
    					//Method declaration
    					if(b instanceof MethodDeclaration) {
    						//Method modifier
    						String methodMod="";
    						EnumSet<Modifier> methodModifier = ((MethodDeclaration) b).getModifiers();	
    						if(methodModifier.contains(Modifier.PUBLIC)){
    						methodMod="+";    						    						
    						if(((MethodDeclaration) b).getName().toString().startsWith("get")||
    								((MethodDeclaration) b).getName().toString().startsWith("set"))
    						{
    							String fieldname=((MethodDeclaration) b).getName().toString().substring(3);
    							replaceGetSet.add(fieldname.toLowerCase());
    							
    						}
    						else
    						{
		    						//Method name, return Type and parameter list
		    						String parameterList = "";
		    						String methodName = ((MethodDeclaration) b).getName().toString();
		    						String methodReturnType =((MethodDeclaration) b).getType().toString();  
		    						output+=methodMod+methodName+"(";
		    						NodeList<Parameter> methodParameter= ((MethodDeclaration) b).getParameters();
		    						if (methodParameter.isEmpty()){
			    							output+=")";
			    							output+=":"+methodReturnType+";";
		    						}
		    						else{
				    						for (Node n2 : methodParameter) {
				    							String methodParamName = n2.getChildNodes().get(0).toString();
				    							String methodParamType = n2.getChildNodes().get(1).toString();				    							
				    							parameterList+=methodParamName+":"+methodParamType+" ";		    										    							
				    						    }
				    						output+=parameterList+")"+":"+methodReturnType+";";;
		    						}		    						
		    				}  
    					}
    					
    				}
                }
            }  
         }
            output+="],";
            //Replace private attribute have getter and setter to public
        	for (String fieldname : replaceGetSet) {
        		output=output.replaceAll("-"+fieldname,"+"+fieldname);
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
		        System.out.println(file.getName());
		        CompilationUnit compilationUnit = JavaParser.parse(data);
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
