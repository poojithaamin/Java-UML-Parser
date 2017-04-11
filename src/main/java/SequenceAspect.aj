package org.poojitha.aop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.sourceforge.plantuml.SourceStringReader;

public aspect SequenceAspect {
	public String messageVariable = "";
	public int sequence=0;
	
	pointcut function():call(void org.poojitha.aop.*.attach*(*));
		//call(void org.poojitha.aop.AOPDemo.method*(*));
			
	after(): function(){
		System.out.println("aspect after:");
	}
	
	before(): function(){
		System.out.println("aspect before:");		
		String fromClass = null;	
		String toClass = null;
		String splitFromClass = null;
		String methodName = null;
		String returnType = null;
		sequence++;
		
		String[] splitToClass=thisJoinPoint.getSignature().toString().split("\\.");		
		try {
			splitFromClass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName()).toString();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fromClass = splitFromClass.substring(splitFromClass.lastIndexOf('.') + 1);
		methodName = splitToClass[splitToClass.length-1];
		toClass = splitToClass[splitToClass.length-2];
		returnType = splitToClass[0].split(" ")[0];
		
		System.out.println("Method name is " +splitToClass[splitToClass.length-1]);
		System.out.println("name is " +thisJoinPoint.getStaticPart( ).getSourceLocation( ));
		System.out.println("from Class is " +fromClass);
		System.out.println("Variable is "+thisJoinPoint.getSignature().toString());
		System.out.println("to Class is " +splitToClass[splitToClass.length-2]);
		
		messageVariable+=fromClass+" ->"+ toClass+": "+sequence+" "+methodName+" : "+returnType+"\n";
		generateSequence(messageVariable);
	}
	
	public void generateSequence(String str){
		StringBuilder plantUmlSource = new StringBuilder();	
		
	    plantUmlSource.append("@startuml\n");
	    
	    String finalVal=str;
	    finalVal=str+"\n";
	    System.out.println("finalval is "+finalVal);
	    
	    plantUmlSource.append(finalVal);	            	          

       //plantUmlSource.append("Alice -> Bob: Authentication Request\n");

       //plantUmlSource.append("Bob --> Alice: Authentication Response\n");

       plantUmlSource.append("@enduml");

	    SourceStringReader reader = new SourceStringReader(plantUmlSource.toString());
	    
	    System.out.println(plantUmlSource.toString());

	    FileOutputStream output = null;
	    
		try {
			output = new FileOutputStream(new File("E:/SJSU/SEM1/202-SSE/PP/UmlParserSeqNew.png"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block		
		}

	    try {
			reader.generateImage(output);
		} catch (IOException e) {
			// TODO Auto-generated catch block		
		}
	}
}
