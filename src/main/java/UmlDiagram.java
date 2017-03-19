package umlparser;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.io.*;

public class UmlDiagram {
	public static void generatePNG(String result, String outFile) throws IOException {
		int len;
        String yumlUrl="https://yuml.me/diagram/plain/class/"+result;
        System.out.println(yumlUrl);
		URL url = new URL(yumlUrl);
		InputStream input = url.openStream();
		OutputStream output = new FileOutputStream(outFile);

		byte[] b = new byte[2048];
		while ((len = input.read(b)) != -1) {
			output.write(b, 0, len);
		}

		input.close();
		output.close();
}
}

