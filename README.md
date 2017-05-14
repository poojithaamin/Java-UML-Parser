Class Parser

This tool that can be used to generate a Class diagram by parsing the java source code. The java source folder and the output image file path is given as input to the code and the generated image of the class diagram is saved in the specified folder.

Requirements:

a)	Java JDK version 1.8

b)	Internet connection

Tools and Libraries used for building the tool:

a)	JavaParser 3.2.0 - http://javaparser.org/

JavaParser is a simple and lightweight set of tools that is used for getting an Abstract Syntax Tree (AST) by parsing the Java code which can be further processed as per our requirement.

b)	yUML - http://yuml.me/

This is a free online tool which takes the URL generated from the UML Parser application.
The URL contains the class, method, attributes, interface and relationship details in a specific format. This requires an active internet connection to work.

Run/Compile Instructions:

a)	Download the jar file to any directory.

b)	Open the command prompt and go the above directory.

c)	Pass the following arguments to the code:

1.	Full path of the folder that contains the java source files. 

2.	Name of the output png file.

The file will be created in the same folder, where the java source files exists.
Example:
	 java -jar umlparser.jar E:\SJSU\SEM1\202-SSE\PP\srcFolder\test4 new_test4.png

	Github Jar file Location - https://github.com/poojithaamin/Java-UML-Parser/blob/master/ParserJar/UmlParser.jar

	Github Source code  - https://github.com/poojithaamin/Java-UML-Parser/tree/master/UMlParser


Integration of UML Parser with Cloud Scale Web Application:

Team mates from CMPE 281:

1) Vikas Miyani - 011410152

2) Darshit Thesiya - 011424647

	YouTube links – 

https://www.youtube.com/watch?v=znyis6tiX_s

https://www.youtube.com/watch?v=Zsvzb21HLKQ&feature=youtu.be


Sequence Parser
This tool can be used to generate the sequence diagram by performing dynamic analysis of the Java source code. The diagram is generated by executing the java code and applying aspect J. 

Requirements:

a)	PlantUML – Sequence diagram can be generated using simple and intuitive language.

b)	AspectJ Runtime Library

c)	Java JDK version 1.8

Run/Compile Instructions:

•	Using the Jar uploaded in Github for Test Case 4:

a)	Download the Jar to any location.

b)	Run the following command and the image file be created in the Jar file location.

Java – jar UmlSequence.jar

•	For new java source code:

a)	Include the aspect file SequenceAspect.aj in the project.

b)	Compile and Run the code. The aspects will be applied and the sequence diagram will be generated using PlantUML.

