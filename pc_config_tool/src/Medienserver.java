import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Medienserver {

	private final String s1 = "[Unit]\nDescription=test nfs mount script\nRequires=network-online.service\nAfter=network-online.service\nBefore=kodi.service\n\n[Mount]\nWhat=";
	private final String s2 = "\nWhere=/storage/";
	private final String s3 = "\nOptions=\nType=nfs\n\n[Install]\nWantedBy=multi-user.target";
	private String file;

	public Medienserver(String file) {
		this.file = file;
	}

	public boolean getState() {
		boolean b = false;
		try {
			FileInputStream fis = new FileInputStream(file);
			if (-1 == fis.read()) {
				b = false;
			} else {
				b = true;
			}
			fis.close();
		} catch (IOException e) {
			new STDErrFrame("Fehler bei Medienserver getState");
		}
		return b;
	}

	public String ein() {
		String strLine;
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			while ((strLine = br.readLine()) != null) {
				if (strLine.contains("What")) {
					br.close();
					return strLine.substring(6);
				}
			}
			br.close();
		} catch (IOException e) {
			new STDErrFrame("Fehler beim Lesen bei Medienserver");
		}
		return null;
	}

	public void aus(String in) {
		String art;
		if (file.contains("videos")) {
			art = "/storage/movies";
		} else if (file.contains("pictures")) {
			art = "/storage/pictures";
		} else {
			art = "/storage/music";
		}
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			bw.write(s1 + in + s2 + art + s3);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			new STDErrFrame("Fehler bei Medienserver schreiben");
		}
	}

	public void leer() {
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
			bw.write("");
			bw.flush();
			bw.close();
		} catch (IOException e) {
			new STDErrFrame("Fehler bei Medienserver schreiben");
		}
	}
}
