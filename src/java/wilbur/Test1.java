package wilbur;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import com.dickimawbooks.texparserlib.TeXApp;
import com.dickimawbooks.texparserlib.TeXParser;
import com.dickimawbooks.texparserlib.TeXParserListener;
import com.dickimawbooks.texparserlib.TeXReader;
import com.dickimawbooks.texparserlib.latex2latex.LaTeX2LaTeX;
import com.dickimawbooks.texparsertest.TeXParserApp;

/**
 * Unfinished, got stuck ...
 * @author wilbur
 *
 */
public class Test1  {
	
//	static String texFileName = "tex/test-newline.tex"; 
//	static String texFileName = "tex/test-dirty2.tex";
//	static String texFileName = "tex/test-obs.tex";
//	static String texFileName = "tex/test-minimal.tex";
	static String texFileName = "tex/test-math-nested.tex";
	
	
	//static String outDirPath = "D:\\\\tmp\\";
	static String outDirPath = "C:/tmp/";
//	static String outDirPath =  System.getProperty("java.io.tmpdir");

	public static void main(String[] args) throws IOException {
		Path path = FileSystems.getDefault().getPath(texFileName);
		System.out.println("input tex path = " + path.toAbsolutePath().toString());
		
		File file = path.toFile();
		File outDir = FileSystems.getDefault().getPath(outDirPath).toFile();
		
		System.out.println("Creating TeXParserApp");
		TeXApp app = new TeXParserApp();
		
		TeXParserListener listener = new LaTeX2LaTeX(app, outDir);  //(TeXApp texApp, File outDir)
		System.out.println("Created Listener: " + listener.getClass().getSimpleName());
		
		TeXParser parser = new TeXParser(listener);
		System.out.println("Created Parser: " + parser.getClass().getSimpleName());

		TeXReader reader = new TeXReader(file);
		System.out.println("Created Reader: " + reader.getClass().getSimpleName());
		
		System.out.println("Starting Parse");
//		parser.parse(reader);	// did not call beginParse(), not set up writer
		parser.parse(file);
		System.out.println("Finished Parse");
		
	}
	
//	void run() {
//
//	}

}
