package org.poojitha.aop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import net.sourceforge.plantuml.SourceStringReader;

public aspect SequenceAspect {
	
	public String messageVariable = "";
	public int sequence=0;
	String fromClass = null;
	String methodName = null;
	String leftClass=null;
	String joinPointValue=null;
	float maxValue=0;
	int levelValue=0;
	HashMap<String, Integer> parentMap = new HashMap<String, Integer>();
	HashMap<String, String> childMap = new HashMap<String, String>();
	HashMap<String, String> doneMap = new HashMap<String, String>();
	
	//pointcut function():call(void org.poojitha.aop.*.showState*())||call(void org.poojitha.aop.*.attach*(*))||call(void org.poojitha.aop.*.setState*(*));//||execution(void org.poojitha.aop.*.notifyObservers());	
	//pointcut function():call(void org.poojitha.aop.*.**(*));//||call(void org.poojitha.aop.*.**());//||execution(void org.poojitha.aop.*.**());			
	pointcut function():call(void org.poojitha.aop.*.attach*(*))||call(void org.poojitha.aop.*.setState*(*))||execution(void org.poojitha.aop.*.notifyObservers())||execution(void org.poojitha.aop.*.update());
	
	after(): function(){
		if(joinPointValue.trim().equals("call")) {
		System.out.println("aspect after:");
		System.out.println("Target is:"+thisJoinPoint.getTarget());
		doneMap.put(fromClass+methodName+sequence, "0");
		System.out.println("Map value is "+doneMap.get(fromClass+methodName));
	}
		else if(joinPointValue.trim().equals("execution")) {
			System.out.println("aspect after:");
			System.out.println("Target is:"+thisJoinPoint.getTarget());
			doneMap.put(leftClass+methodName+maxValue+"."+levelValue, "0");
			System.out.println("Map value is "+doneMap.get(leftClass+methodName+maxValue+"."+levelValue));
		} 
		
	}
	
	
	before(): function(){
		System.out.println("aspect before:");		
			
		String toClass = null;
		String splitFromClass = null;		
		String returnType = null;
		Integer level=0;
		
		joinPointValue=thisJoinPoint.toString().split("\\(")[0];
		String[] splitToClass=thisJoinPoint.getSignature().toString().split("\\.");		
		try {
			splitFromClass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName()).toString();
		} catch (ClassNotFoundException e) {			
			e.printStackTrace();
		}
		
		fromClass = splitFromClass.substring(splitFromClass.lastIndexOf('.') + 1);
		methodName = splitToClass[splitToClass.length-1];
		toClass = splitToClass[splitToClass.length-2];
		returnType = splitToClass[0].split(" ")[0];
	
		System.out.println("JoinPoint is " +joinPointValue);
		System.out.println("Method name is " +splitToClass[splitToClass.length-1]);
		System.out.println("name is " +thisJoinPoint.getStaticPart( ).getSourceLocation( ));
		System.out.println("from Class is " +fromClass);
		System.out.println("Variable is "+thisJoinPoint.getSignature().toString());
		System.out.println("to Class is " +splitToClass[splitToClass.length-2]);
		
		if(joinPointValue.trim().equals("call")) {
		System.out.println("Entered here1");		
		sequence++;	
		//parentMap.put(fromClass+methodName, String.valueOf(sequence));
		parentMap.put(fromClass+methodName+sequence, sequence);
		messageVariable+=fromClass+" ->"+ toClass+": "+parentMap.get(fromClass+methodName+sequence)+" "+methodName+" : "+returnType+"\n";		
		leftClass=toClass;
		}
		
		else if(joinPointValue.trim().equals("execution")) {
	    System.out.println("Entered here2");	
		level++;
		maxValue = 0;
		for (String key : childMap.keySet()) {
			if (doneMap.containsKey(key)) {
			     maxValue = 0;
			    } else{
			    	float value=Float.parseFloat(childMap.get(key));
				    if (value > maxValue) {
				        maxValue = value;
				    }  
					System.out.println("maxValue is "+maxValue);    
					childMap.put(leftClass+methodName+maxValue+"."+level, maxValue+"."+level);
					messageVariable+=leftClass+" ->"+ toClass+": "+childMap.get(leftClass+methodName+maxValue+"."+level)+" "+methodName+" : "+returnType+"\n";		
					levelValue=level;
			    }
			}
		
		if (maxValue==0) {
			for (int value : parentMap.values()) {			
		    if (value > maxValue) {
		        maxValue = value;
		    }	
		}
		System.out.println("maxValue is "+maxValue);    
		childMap.put(leftClass+methodName+maxValue+"."+level, sequence+"."+level);
		messageVariable+=leftClass+" ->"+ toClass+": "+childMap.get(leftClass+methodName+maxValue+"."+level)+" "+methodName+" : "+returnType+"\n";		
		levelValue=level;
		}
		leftClass=toClass;
		}
		generateSequence(messageVariable);
	}
	
	
	
	public void generateSequence(String str){
		StringBuilder plantUmlSource = new StringBuilder();			
	    plantUmlSource.append("@startuml\n");	    
	    String finalVal=str;
	    finalVal=str+"\n";
	    System.out.println("finalval is "+finalVal);
	    
	    plantUmlSource.append(finalVal);	            	          

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
