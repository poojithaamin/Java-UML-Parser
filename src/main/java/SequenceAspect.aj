
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import net.sourceforge.plantuml.SourceStringReader;

public aspect SequenceAspect {
	
	public String messageVariable = "";
	public int sequence=1;
	String fromClass = "";
	String methodName = "";
	String leftClass="";	
	String joinPointType="";
	String maxValue="";	
	String returnType = "";
	String splitFromClass = "";
	String[] splitToClass = null;
	String toClass = "";
	String prevType="";
	String prevSequence="";
	String mainClass="";
	
	
	int level=0;
		
	HashMap<String, String> childMap = new HashMap<String, String>();
	HashMap<String, String> doneMap = new HashMap<String, String>();
	HashMap<String, String> fromMap = new HashMap<String, String>();	
	

		pointcut function(Object o) :  !within(myAspect) && target(o) 
		&& execution(public * *.*(..))
		 && !execution(*.new(..)) ||(!within(myAspect)
			&& initialization(*.new(..)) && target(o));
		
		//||(!within(myAspect)
		//&& execution(*.new(..)) && target(o));	

		pointcut mainMethod() : execution(public static void main(String[]));

		before() :mainMethod(){
			System.out.println("Main class is "+thisEnclosingJoinPointStaticPart.getSignature().getDeclaringType());
			mainClass=thisEnclosingJoinPointStaticPart.getSignature().getDeclaringType().toString().split(" ")[1];
		}
		
	after(Object o): function(o){
		String afterValue="";
		if (sequence!=0){			
		    for (String key : childMap.keySet()) {				
				if (doneMap.containsKey(key)) {					 			   
				} 
				else{			    	
					    if (key.compareToIgnoreCase(afterValue) >0) {
					    	afterValue = key;
					    }  
				     }				
				}
			}			
		doneMap.put(afterValue, "done");
		//System.out.println("Deleted afterValue"+afterValue);
	}
	
	
	before(Object o): function(o){		
		String sequenceValue="";
		String maxValue="";	
		setFromToDetails(thisJoinPoint);
		if (sequence!=0){			
	    for (String key : childMap.keySet()) {			
			if (doneMap.containsKey(key)) {							    
			} 
			else{			    	
				    if (key.compareToIgnoreCase(maxValue) >0) {
				        maxValue = key;
				    }  
			     }			   
			}
		}			
		if(maxValue.equals("")){
			System.out.println("parent block");
			sequenceValue+=sequence++;
			fromClass=mainClass;
			level=1;
		}
		else{			
			System.out.println("child block");
			String newValue="";			
			newValue+=maxValue+"."+level;
			if (prevSequence.equals(newValue)){	
				System.out.println("Old from class is"+fromClass);
				fromClass=fromMap.get(prevSequence);
				System.out.println("Old from class is"+fromClass);
				String last = newValue.substring(newValue.lastIndexOf('.') + 1);				
				int newLevel = Integer.parseInt(last)+1;				
				sequenceValue=maxValue+"."+newLevel;
			}
			else
				sequenceValue=maxValue+"."+level;			    
		}
		while (doneMap.containsKey(sequenceValue)){
			String last = sequenceValue.substring(sequenceValue.lastIndexOf('.') + 1);				
			int newLevel = Integer.parseInt(last)+1;				
			sequenceValue=maxValue+"."+newLevel;
			if (prevSequence.equals(sequenceValue)){	
				System.out.println("Old from class is"+fromClass);
				fromClass=fromMap.get(prevSequence);
				System.out.println("Old from class is"+fromClass);
			}
		}
	
		
		if(!toClass.equals("")){	    
		System.out.println("\n\n**********************aspect before:**********************"+thisJoinPoint.getSignature());
		System.out.println("fromClass is "+fromClass);
		System.out.println("toClass is "+toClass);
		System.out.println("methodName "+methodName);
		System.out.println("sequenceValue "+sequenceValue);
		childMap.put(sequenceValue, fromClass+toClass+methodName);
		messageVariable+=fromClass+" ->"+ toClass+": "+sequenceValue+" "+methodName+" : "+returnType+"\n";
		fromMap.put(sequenceValue, fromClass);
		fromClass = toClass;	
		prevSequence=sequenceValue;				
		}
		System.out.println("GENERATESEQUENCE INVOKED");
		generateSequence(messageVariable);
	}
	
	//set joinPointdetails	
	public void setFromToDetails(org.aspectj.lang.JoinPoint thisJoinPoint){		
		splitToClass=thisJoinPoint.getSignature().toString().split("\\.");			
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
	    FileOutputStream output = null;	    
		try {
			output = new FileOutputStream(new File("E:/SJSU/SEM1/202-SSE/PP/UmlParserSeq3.png"));
		} catch (FileNotFoundException e) {			
		}
	    try {
			reader.generateImage(output);
		} catch (IOException e) {				
		}
	}
}
