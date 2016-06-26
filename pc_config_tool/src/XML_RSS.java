import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

public class XML_RSS {

	Document doc;
	File f;
	Element root;
	Element child;
	XMLOutputter out;
	FileWriter fw;

	public XML_RSS(String file) {
		try {
			f = new File("src\\" + file);
			doc = new SAXBuilder().build(f);
			root = doc.getRootElement();
			child = root.getChild("set");
			out = new XMLOutputter();

		} catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
	}

	public String[] ein_aus(String[] value_neu) {
		List<Element> alle = child.getChildren("feed");
		String[] value_alt = new String[5];
		for (int i = 0; i < 5; i++) {
			value_alt[i] = "";
		}
		for (int i = 0; i < alle.size(); i++) {
			value_alt[i] = alle.get(i).getText();
		}
		if (value_neu != null) {
			for (int i = 0; i < 5; i++) {
				if (i < alle.size()) {
					alle.get(i).setText(value_neu[i]);
				} else {
					Element b = new Element("feed");
					b.setAttribute(new Attribute("updateinterval", "5"));
					b.setText(value_neu[i]);
					child.addContent(b);
				}
			}
		}
		return value_alt;
	}

	public void changeFW(String fw_n) {
		f.renameTo(f = new File("src\\" + fw_n));
	}

	public void print() {
		try {
			fw = new FileWriter(f);
			out.output(doc, fw);
			fw.close();
		} catch (IOException e1) {
			new STDErrFrame("Fehler bei XML schreiben");
		}
	}
}
