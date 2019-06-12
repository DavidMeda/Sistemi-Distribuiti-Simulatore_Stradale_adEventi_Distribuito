package vanet;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

import org.graphstream.ui.view.Viewer;

@SuppressWarnings("serial")
public class MainFrameVanet extends JPanel{
	public MainFrameVanet() {
		
	}
	
	public File chooseFile(){
		JFileChooser fc = new JFileChooser();
		
		fc.setFileFilter(new FileFilter() {
		
			@Override
			public String getDescription() {
				return ".xml";
			}
			
			@Override
			public boolean accept(File file) {
				 if (file.isDirectory()) return true;
				    String fname = file.getName().toLowerCase();
				    return fname.endsWith(".xml");
			}
		});

		fc.setCurrentDirectory(new File(System.getProperty("user.dir")+File.separator+"XMLConfig"));
		int n = fc.showOpenDialog(this);
		
		if(n == JFileChooser.APPROVE_OPTION)return fc.getSelectedFile();
		return null;
	
	}

	
	
	
	public static void main(String[] args) {
		MainFrameVanet t = new MainFrameVanet();
		String dir = t.chooseFile().toString();
		
		CityGraph g = CityGraphXMLParser.getGraph("Cosenza",dir);
		
		try {
			
		Viewer viewer = g.display();
		viewer.disableAutoLayout();
		g.startSimulation();
		
		} catch (InterruptedException e) {e.printStackTrace();}
		
//		System.out.println(g.getStatistiche());
	    

		

	    
	}
}
