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

public class XML_Settings {

	Document doc;
	File f;
	Element root;
	XMLOutputter out;
	FileWriter fw;

	public XML_Settings(String file) {
		try {
			f = new File("src\\" + file);
			doc = new SAXBuilder().build(f);
			root = doc.getRootElement();
			out = new XMLOutputter();
		} catch (JDOMException | IOException e) {
			new STDErrFrame("Fehler beim Erstellen XML-Settings");
		}
	}

	public String ein_aus(String value_neu, String id) {
		List<Element> alle = root.getChildren("setting");
		String value_alt = null;
		for (Element e : alle) {
			List<Attribute> a_alle = e.getAttributes();
			for (Attribute a : a_alle) {
				if (a.getName() == "id") {
					if (a.getValue().contentEquals(id)) {
						for (Attribute a_gef : a_alle) {
							if (a_gef.getName() == "value") {
								value_alt = a_gef.getValue();
								if (value_neu != null) {
									a_gef.setValue(value_neu);
								}
							}
						}
					}
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
			new STDErrFrame("Fehler bei XMl schreiben");
		}
	}
}
