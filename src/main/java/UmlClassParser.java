package umlparser;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

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
import com.github.javaparser.ast.type.ClassOrInterfaceType;

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
		/*Get Class and Interface list*/
		getClassInterfaceName(compilationunitArray);
		/*Loop through individual file*/
        for (CompilationUnit cu : compilationunitArray)
            output+= getDetails(cu); 
        /*Get association multiplicity*/
        getAssociationValue();
        output=output.replace("?","");
        /*Generate the diagram*/
		UmlDiagram.generatePNG(output, outputFile);
	}
	
	/* Get class and interface list */
   private void getClassInterfaceName(ArrayList<CompilationUnit> compilationunitArray){	
	    for (CompilationUnit cu2 : compilationunitArray) {
	    	List<TypeDeclaration<?>> gt1 = cu2.getTypes();  
	    	for (Node n : gt1) {
	    		ClassOrInterfaceDeclaration coi1 = (ClassOrInterfaceDeclaration) n;
	    		listClassInterface.put(coi1.getName().toString(), coi1.isInterface());	
	    	}	    
	    }	   	    	  
   }
    
   /* Get the complete details of the java files */
    private String getDetails(CompilationUnit cu2) {
    	    String intOutput="";
    	    String interfaceMapping="";
    	    String extendsMapping="";
    	    String usesMapping="";
    	    String fieldMapping="";
    	    String methodMapping="";
        	replaceGetSet.clear();
        	List<TypeDeclaration<?>> gt = cu2.getTypes();        
            for (Node n : gt) {
                ClassOrInterfaceDeclaration coi = (ClassOrInterfaceDeclaration) n;
                String className=coi.getName().toString();                
                if (coi.isInterface()){
                	fieldMapping+="[<<interface>>;"+coi.getName()+"]";
                }
                else{
                	System.out.println(coi.getName()+" "+coi.isInterface());
                	fieldMapping+="["+coi.getName()+"|";                
                	List<BodyDeclaration<?>> bd = ((TypeDeclaration<?>) n).getMembers();                	
                	for(BodyDeclaration<?> b: bd){                		
                		String fieldMod="";
                		//Field declaration
    					if(b instanceof FieldDeclaration && b instanceof TypeDeclaration==false){									
    						//Field modifier
    						EnumSet<Modifier> fieldModifier = ((FieldDeclaration) b).getModifiers();
    						//if(fieldModifier.contains(Modifier.PUBLIC)||fieldModifier.contains(Modifier.PRIVATE)){
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
                                
    							//java source does not exist for the type, add as attribute
    							if ((!listClassInterface.containsKey(fieldType)) && (!fieldType.contains("<")) && (fieldMod=="+" || fieldMod=="-")){
    								fieldMapping+=fieldMod+fieldName+':'+fieldType+';';
    								fieldMapping=fieldMapping.replace("[]", "(*)");        							        							        							
    							}    						
    							//if java source exists for the type, get the association
    							 if (listClassInterface.containsKey(fieldType)) {
    								if(listClassInterface.get(fieldType)==true)    								
    								  mapAssociationValue(className,"<<interface>>;"+fieldType,"?-1");
    								else
    									mapAssociationValue(className,fieldType,"?-1");	
    							}
    							
    							//if java source exists for the type, get the association
    							else if(fieldType.contains("<") && listClassInterface.containsKey(fieldType.substring(fieldType.indexOf("<")+1,fieldType.indexOf(">"))))
    							{     							  	
    							  fieldType=fieldType.substring(fieldType.indexOf("<")+1,fieldType.indexOf(">")); 
    							  
    							  if(listClassInterface.get(fieldType)==true) 
    								  mapAssociationValue(className,"<<interface>>;"+fieldType,"?-0..*");
    							  else
    								  mapAssociationValue(className,fieldType,"?-0..*");
    							}
    							
    							//if no java source, put class as attribute
    							else if(fieldType.contains("<") && !listClassInterface.containsKey(fieldType.substring(fieldType.indexOf("<")+1,fieldType.indexOf(">"))))
    							{     							  	
    							  fieldType=fieldType.substring(fieldType.indexOf("<")+1,fieldType.indexOf(">"));  
    							  if (fieldMod=="+" || fieldMod=="-")
    							  fieldMapping+=fieldMod+fieldName+':'+fieldType+';';
    							}
    							
    							else  {
    							 							
    						    }    							
    						 }    					  
    					   //}
    					}
    					 
    					/* Method declaration */
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
		    						System.out.println("Printing method body "+((MethodDeclaration) b).getChildNodes());
		    						List<Node> methodBody=((MethodDeclaration) b).getChildNodes();
		    						methodMapping+=methodMod+methodName+"(";
		    						System.out.println("After compiling"+methodMapping);
		    						NodeList<Parameter> methodParameter= ((MethodDeclaration) b).getParameters();
		    						if (methodParameter.isEmpty()){
		    							methodMapping+=")";
		    							methodMapping+=":"+methodReturnType+";";
		    						}
		    						else{
		    							    System.out.println("I am here");
				    						for (Node n2 : methodParameter) {
				    							String methodParamName = n2.getChildNodes().get(0).toString();
				    							String methodParamType = n2.getChildNodes().get(1).toString();					    							
				    							parameterList+=methodParamName+":"+methodParamType+" ";	
				    							System.out.println("parameterList is "+parameterList);
				    							if (listClassInterface.containsKey(methodParamType)){
				    								usesMapping=usesInterfaceClass(className,methodParamType);				    								
				    							   }
				    						    }
				    						methodMapping+=parameterList+")"+":"+methodReturnType+";";;
		    						}
		    						
		    						for(Node i : methodBody){
		    							for (String key : listClassInterface.keySet()){
		    								if(i.toString().replaceAll("(?m)^.*System.out.println.*", "").contains(key) && listClassInterface.get(key)==true)	{	    									
		    							      System.out.println(className+" @@@"+i.toString()+" uses***"+key +i.toString().contains(key));
		    								  usesMapping=usesInterfaceClass(className,key);
		    								}
		    						}
		    						}
		    						methodMapping=methodMapping.replace("[]", "(*)");
		    				}  
    					}    				
    				}
    					
    					if(b instanceof ConstructorDeclaration) {
    						//Method modifier
    						String methodMod="";
    						EnumSet<Modifier> methodModifier = ((ConstructorDeclaration) b).getModifiers();	
    						if(methodModifier.contains(Modifier.PUBLIC)){
    						methodMod="+";    						    						
		    						//Method name and parameter list
		    						String parameterList = "";
		    						String methodName = ((ConstructorDeclaration) b).getName().toString();		    						 
		    						methodMapping+=methodMod+methodName+"(";
		    						NodeList<Parameter> methodParameter= ((ConstructorDeclaration) b).getParameters();
		    						if (methodParameter.isEmpty()){
		    							methodMapping+=")";		    							
		    						}
		    						else{
				    						for (Node n2 : methodParameter) {
				    							String methodParamName = n2.getChildNodes().get(0).toString();
				    							String methodParamType = n2.getChildNodes().get(1).toString();					    							
				    							parameterList+=methodParamName+":"+methodParamType+" ";		    
				    							if (listClassInterface.containsKey(methodParamType)){
				    								usesMapping=usesInterfaceClass(className,methodParamType);					    								
				    							   }
				    						    }
				    						methodMapping+=parameterList+")"+";";;
		    						}		    						
		    				}    				
    				  }    					
                 }
            } 
                if (!coi.getImplementedTypes().isEmpty())
                {
                	System.out.println(className+" has interfaces ");
                	interfaceMapping=implementsInterface(className,coi.getImplementedTypes() );
        		}
                
                if (!coi.getExtendedTypes().isEmpty())
                {
                	System.out.println(className+" extends ");
                	extendsMapping=extendsClass(className,coi.getExtendedTypes());
        		}
                
                
         }
            intOutput="|"+fieldMapping+"|"+methodMapping+"],"+interfaceMapping+extendsMapping+usesMapping+",";
            //Replace private attribute have getter and setter to public
        	for (String fieldname : replaceGetSet) {
        		intOutput=intOutput.replaceAll("-"+fieldname,"+"+fieldname);
        	}        	        
            return intOutput;
    }
    
    public String implementsInterface(String className, NodeList<ClassOrInterfaceType> interfaceList ){
    	String interfaceMapping="";        
        for (int i = 0; i < interfaceList.size(); i++) {
			System.out.println(interfaceList.get(i));
			interfaceMapping+=",[<<interface>>;"+interfaceList.get(i)+"]^-.-"+"["+className+"]";			
        }
        return interfaceMapping;
    }

    public String extendsClass(String className, NodeList<ClassOrInterfaceType> extendsList ){
    	String extendsMapping="";        
        for (int i = 0; i < extendsList.size(); i++) {
			System.out.println(extendsList.get(i));
			extendsMapping+=",["+extendsList.get(i)+"]^-"+"["+className+"]";			
        }
        return extendsMapping;
    }  
    
    public String usesInterfaceClass(String className, String methodParamType){
    	String usesMapping="";       
    	System.out.println("&&&&&&&&&&&&&&&"+className+methodParamType);
        if(listClassInterface.get(methodParamType)==false) {			
			//usesMapping+=",["+className+"]uses -.->["+methodParamType+"]";			
        }
        else if(listClassInterface.get(methodParamType)==true) {			
			usesMapping+=",["+className+"]uses -.->[<<interface>>;"+methodParamType+"]";
        }
        return usesMapping;
    }     
    
    public void mapAssociationValue(String className1, String className2, String value)
    {
    	String c1c2="["+className1+"]"+"["+className2+"]";
    	String c2c1="["+className2+"]"+"["+className1+"]";
    	if(association.containsKey(c1c2)||association.containsKey(c2c1))  {   		  
    		  String assoValue=(association.get(c1c2)!=null)?association.get(c1c2) :association.get(c2c1); 
    		  System.out.println("Old Association value is "+assoValue); 
    		  value=value.replace("?-", "");
    		  assoValue=assoValue.replace("?", value); 
    		  if (association.get(c1c2)!=null) {
	    		  association.put("["+className1+"]"+"["+className2+"]", assoValue);
	    		  System.out.println("New Association value is "+assoValue); 
    		  }
    		  else{
    			  association.put("["+className2+"]"+"["+className1+"]", assoValue);
	    		  System.out.println("New Association value is "+assoValue); 
    		  }
    			  
    	}
    	else{    	
    		association.put("["+className1+"]"+"["+className2+"]", value);
    		System.out.println("Association inserted for"+className1+className2);
    	}
    }
    
    public void getAssociationValue(){
    	 for (String key : association.keySet()) {
    		   String finalInput=key.replace("][", "]"+association.get(key)+"[");
    		   System.out.println(finalInput);
    		   output+=finalInput+",";
    		}

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
