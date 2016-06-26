import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class FTP {
	String[] remoteDirectory;
	String localDirectory = "src/";
	FTPClient ftp;
	String[] files;

	public FTP(String[] ip) {
		try {
			byte[] addr = { (byte) Integer.parseInt(ip[0]), (byte) Integer.parseInt(ip[1]),
					(byte) Integer.parseInt(ip[2]), (byte) Integer.parseInt(ip[3]) };
			InetAddress inet = InetAddress.getByAddress(addr);
			String userId = "root";
			String password = "openelec";
			remoteDirectory = new String[12];
			remoteDirectory[0] = "/.kodi/userdata";
			remoteDirectory[1] = remoteDirectory[0];
			remoteDirectory[2] = "/.kodi/userdata/addon_data/plugin.programm.xbmcmail";
			remoteDirectory[3] = remoteDirectory[2];
			remoteDirectory[4] = "/.kodi/userdata/addon_data/weather.openweathermap.extended";
			remoteDirectory[5] = "/.kodi/addons/script.mqtt.subscribe/resources";
			remoteDirectory[6] = remoteDirectory[5];
			remoteDirectory[7] = "/.kodi/addons/service.mqtt.publisher/resources";
			remoteDirectory[8] = remoteDirectory[7];
			remoteDirectory[9] = "/.config/system.d";
			remoteDirectory[10] = remoteDirectory[9];
			remoteDirectory[11] = remoteDirectory[9];
			files = new String[12];
			files[0] = "RssFeeds.xml";
			files[1] = "_RssFeeds.xml";
			files[2] = "settings.xml";
			files[3] = "_settings.xml";
			files[4] = "settings.xml";
			files[5] = "settings.xml";
			files[6] = "_settings.xml";
			files[7] = "settings.xml";
			files[8] = "_settings.xml";
			files[9] = "storage-pictures.mount";
			files[10] = "storage-music.mount";
			files[11] = "storage-videos.mount";

			ftp = new FTPClient();
			ftp.connect(inet);
			if (!ftp.login(userId, password)) {
				ftp.logout();
				new STDErrFrame("Fehler bei ftp-Verbindung");
			}
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				new STDErrFrame("Fehler bei ftp-Verbindung");
			}
			ftp.enterLocalPassiveMode();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public <T> void holen() {
		try {
			for (int i = 0; i < remoteDirectory.length; i++) {
				ftp.changeWorkingDirectory(remoteDirectory[i]);
				System.out.println("Current directory is " + ftp.printWorkingDirectory());
				FTPFile[] ftpFiles = ftp.listFiles();
				if (ftpFiles != null && ftpFiles.length > 0) {
					for (FTPFile file : ftpFiles) {
						if (!file.isFile()) {
							continue;
						}
						if (file.getName().contentEquals(files[i])) {
							System.out.println("File is " + file.getName());
							String subs[] = remoteDirectory[i].split(Pattern.quote("/"));
							String sub_a = localDirectory;
							for (String sub : subs) {
								sub_a = sub_a + "\\" + sub;
							}
							sub_a = sub_a + "\\";
							Files.createDirectories(Paths.get(sub_a));
							OutputStream output = new FileOutputStream(sub_a + "\\" + file.getName());
							ftp.retrieveFile(file.getName(), output);
							output.close();
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void senden() {
		try {
			for (int i = 0; i < remoteDirectory.length; i++) {
				ftp.changeWorkingDirectory(remoteDirectory[i]);
				FTPFile[] ftpFiles = ftp.listFiles();
				if (ftpFiles != null && ftpFiles.length > 0) {
					for (FTPFile file : ftpFiles) {
						if (!file.isFile()) {
							continue;
						}
						if (file.getName().contentEquals(files[i])) {
							ftp.deleteFile(file.getName());
							System.out.println("gelöscht: " + file.getName());
						}
					}
				}
			}
			for (int j = 0; j < remoteDirectory.length; j++) {
				String subs[] = remoteDirectory[j].split(Pattern.quote("/"));
				String sub_a = localDirectory;
				for (String sub : subs) {
					sub_a = sub_a + "\\" + sub;
				}
				sub_a = sub_a + "\\";
				File src = new File(sub_a);
				File[] src_files = src.listFiles();
				for (File f : src_files) {
					if (f.getName().contentEquals(files[j])) {
						ftp.changeWorkingDirectory(remoteDirectory[j]);
						FTPFile file = new FTPFile();
						file.setName(f.getName());
						InputStream input = new FileInputStream(sub_a + file.getName());
						ftp.storeFile(file.getName(), input);
						input.close();
						System.out.println("hochgeladen: " + file.getName());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void finalize() {
		try {
			ftp.logout();
			ftp.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}