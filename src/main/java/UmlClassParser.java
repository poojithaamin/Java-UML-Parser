package umlparser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class UmlClassParser {
	File srcFolder;
	String outputFile;
    HashMap<String, Boolean> map;
    HashMap<String, String> mapClassConn;
	ArrayList<CompilationUnit> compilationunitArray;
	String yumlCode;
	
	UmlClassParser(File srcFolder, String outputFile){
		this.srcFolder = srcFolder;
		this.outputFile = outputFile;
		System.out.println("JavaParser Module");
        map = new HashMap<String, Boolean>();
        //mapClassConn = new HashMap<String, String>();
        //yumlCode = "";
	}

	public void parse() throws Exception{
		compilationunitArray=readFiles(srcFolder);
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
		        new MethodVisitor().visit(compilationUnit, null);
		    }
		}
		
        }
        finally {
           data.close();
        }
        return compilationunitArray;
	}
}
