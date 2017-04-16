package org.poojitha.aop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.sourceforge.plantuml.SourceStringReader;

public aspect SequenceAspect {
	
	public String messageVariable = "";
	public int sequence=0;
	String fromClass = "";
	String methodName = "";
	String leftClass="";	
	String joinPointType="";
	String maxValue="";	
	String returnType = "";
	String splitFromClass = "";
	String[] splitToClass = null;
	String toClass = "";
	
	HashMap<String, Integer> parentMap = new HashMap<String, Integer>();
	HashMap<String, String> childMap = new HashMap<String, String>();
	HashMap<String, String> doneMap = new HashMap<String, String>();
	HashMap<String, String> startMap = new HashMap<String, String>();
	
	pointcut function():execution(String org.poojitha.aop.*.getState*())||call(void org.poojitha.aop.*.attach*(*))||call(void org.poojitha.aop.*.setState*(*))||execution(void org.poojitha.aop.*.notifyObservers())||execution(void org.poojitha.aop.*.update())||call(void org.poojitha.aop.*.showState());
	
	after(): function(){
		System.out.println("After function() Actual method is "+ thisJoinPoint.getSignature().toString());		
		System.out.println("**********************aspect after:**********************");
		setFromToDetails(thisJoinPoint);
		
		if(joinPointType.trim().equals("call")) {		
			System.out.println("Target is:"+thisJoinPoint.getTarget());
			doneMap.put(fromClass+methodName+sequence, "0");
			System.out.println("Map value is "+doneMap.get(fromClass+methodName));
     	}
		
		else if(joinPointType.trim().equals("execution")) {	
			Map.Entry<String, String> maxEntry = null;
			for (Map.Entry<String, String> entry : childMap.entrySet())
			{
			    if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
			    {
			    	if(!doneMap.containsKey(entry.getKey()))
			        maxEntry = entry;
			    }
			}
			System.out.println("Target is:"+thisJoinPoint.getTarget());
			doneMap.put(maxEntry.getKey().toString(), "0");
			startMap.remove(maxEntry.getValue());
			System.out.println(maxEntry.getKey().toString()+ "Map value is "+doneMap.get(maxEntry.getKey().toString()));		    
		} 
		
	}
	
	
	before(): function(){
		System.out.println("**********************aspect before:**********************");									
		Integer level=0;
		System.out.println("Object type"+thisJoinPoint.getTarget().getClass());
		setFromToDetails(thisJoinPoint);
		System.out.println("fromClass is "+fromClass);
		System.out.println("toClass is "+toClass);
		
		//if type is call then insert into parent map with sequence number
		//set the messagevariable with from and to class
		//add sequence and toClass to startMap
		if(joinPointType.trim().equals("call")) {
			System.out.println("Entered here1");		
			sequence++;			
			parentMap.put(fromClass+methodName+sequence, sequence);
			messageVariable+=fromClass+" ->"+ toClass+": "+parentMap.get(fromClass+methodName+sequence)+" "+methodName+" : "+returnType+"\n";		
			leftClass=toClass;
			startMap.put(Integer.toString(sequence), toClass);
		}
		
		//if type is execute increment level
		//get max sequence value from childmap for which the execution is not complete
		//get the leftclass from startmap with max sequence value
		//if no child is present yet, get max value from parentmap
		//make childmap entry and set messageVariable
		//insert into startMap with sequence value and toClass
		else if(joinPointType.trim().equals("execution")) {
		    System.out.println("Entered here2");	
			level++;
			maxValue = "";		
			
			for (String key : childMap.keySet()) {
				System.out.println("childMap key is"+key);
				if (doneMap.containsKey(key)) {
					System.out.println("Already deleted "+key);  			    
				} 
				else{
				    	String value=childMap.get(key);
					    if (value.compareToIgnoreCase(maxValue) >0) {
					        maxValue = value;
					    }  
				     }
				System.out.println("maxValue inner "+maxValue);    
				}
			
			Map.Entry<String, String> maxEntry = null;
			for (Map.Entry<String, String> entry : startMap.entrySet())
			{
			    if (maxEntry == null || entry.getKey().compareTo(maxEntry.getKey()) > 0)
			    {			    	
			        maxEntry = entry;
			    }			    
			}	
			leftClass=maxEntry.getValue();
			
			if (maxValue=="") {				
				int maxValue1=0;
				for (int value : parentMap.values()) {			
				    if (value > maxValue1) {
				        maxValue1 = value;
			        }
			    maxValue=Integer.toString(maxValue1);
			    }
				System.out.println("maxValue outer "+maxValue); 								
				System.out.println("toClass is "+toClass);
				childMap.put(leftClass+methodName+maxValue+"."+level, sequence+"."+level);
				messageVariable+=leftClass+" ->"+ toClass+": "+childMap.get(leftClass+methodName+maxValue+"."+level)+" "+methodName+" : "+returnType+"\n";						
								
				startMap.put(sequence+"."+level, toClass);
				System.out.println("messageVariable inner is "+messageVariable);
			}		
			else{
				if (childMap.containsValue(maxValue+"."+level))
					level++;			
				System.out.println("toClass is "+toClass);
				childMap.put(leftClass+methodName+maxValue+"."+level, maxValue+"."+level);
				messageVariable+=leftClass+" ->"+ toClass+": "+childMap.get(leftClass+methodName+maxValue+"."+level)+" "+methodName+" : "+returnType+"\n";												
				startMap.put(maxValue+"."+level, toClass);
			}
		}	
		System.out.println("GENERATESEQUENCE INVOKED");
		generateSequence(messageVariable);
	}
	
	//set joinPointdetails	
	public void setFromToDetails(org.aspectj.lang.JoinPoint thisJoinPoint){		
		splitToClass=thisJoinPoint.getSignature().toString().split("\\.");			
		try {
			splitFromClass = Class.forName(Thread.currentThread().getStackTrace()[2].getClassName()).toString();
		} catch (ClassNotFoundException e) {			
			e.printStackTrace();
		}
		fromClass = splitFromClass.substring(splitFromClass.lastIndexOf('.') + 1);		
		methodName = splitToClass[splitToClass.length-1];
		joinPointType=thisJoinPoint.toString().split("\\(")[0];
		returnType = splitToClass[0].split(" ")[0];	
		toClass=thisJoinPoint.getTarget().getClass().getName();		
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
		}
	    try {
			reader.generateImage(output);
		} catch (IOException e) {				
		}
	}
}
