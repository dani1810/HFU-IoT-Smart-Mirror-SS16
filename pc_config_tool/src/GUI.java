import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

@SuppressWarnings("serial")
public class GUI extends Frame implements ActionListener, MouseListener {
	private MenuItem m_rec, m_sen;
	private int size_mainMenu = 7;
	private Label l_main[] = new Label[size_mainMenu];
	private Panel p_center[] = new Panel[size_mainMenu];
	private int position_mainMenu = 0;
	private boolean[] states = new boolean[7]; // rss, e-mail, mos_sub, mos_pub,
												// bildms, videoms, musikms
	private FTP ftp;
	private SSH ssh;

	// IP
	private TextField ip_1 = new TextField();
	private TextField ip_2 = new TextField();
	private TextField ip_3 = new TextField();
	private TextField ip_4 = new TextField();
	private Button bt_ip = new Button("OK");

	// RSS
	private Checkbox rss_an_aus = new Checkbox(null, false);
	private TextField rss_fe_1 = new TextField();
	private TextField rss_fe_2 = new TextField();
	private TextField rss_fe_3 = new TextField();
	private TextField rss_fe_4 = new TextField();
	private TextField rss_fe_5 = new TextField();
	private XML_RSS rss_xml;

	// Wetter
	private TextField wet_or_1 = new TextField();
	private TextField wet_or_2 = new TextField();
	private TextField wet_or_3 = new TextField();
	private TextField wet_or_4 = new TextField();
	private TextField wet_or_5 = new TextField();
	private XML_Settings wet_xml;

	// E-Mail
	private Checkbox em_an_aus = new Checkbox(null, false);
	private TextField em_host = new TextField();
	private TextField em_user = new TextField();
	private TextField em_pass = new TextField();
	private Checkbox em_ssl = new Checkbox(null, false);
	private XML_Settings em_xml;

	// Medienserver
	private Checkbox me_bi_an_aus = new Checkbox("an/aus", false);
	private Checkbox me_vi_an_aus = new Checkbox("an/aus", false);
	private Checkbox me_mu_an_aus = new Checkbox("an/aus", false);
	private TextField me_bi_IP = new TextField();
	private TextField me_vi_IP = new TextField();
	private TextField me_mu_IP = new TextField();
	private Medienserver me_bi = new Medienserver("storage-pictures.mount");
	private Medienserver me_vi = new Medienserver("storage-videos.mount");
	private Medienserver me_mu = new Medienserver("storage-music.mount");

	// Mosquitto_sub
	private Checkbox mo_sub_an_aus = new Checkbox(null, false);
	private TextField mo_sub_IP_1 = new TextField();
	private TextField mo_sub_IP_2 = new TextField();
	private TextField mo_sub_IP_3 = new TextField();
	private TextField mo_sub_IP_4 = new TextField();
	private TextField mo_sub_port = new TextField();
	private TextField mo_sub_tag = new TextField();
	private XML_Settingsdef mo_sub_xml;

	// Mosquitto_pub
	private Checkbox mo_pub_an_aus = new Checkbox(null, false);
	private TextField mo_pub_IP_1 = new TextField();
	private TextField mo_pub_IP_2 = new TextField();
	private TextField mo_pub_IP_3 = new TextField();
	private TextField mo_pub_IP_4 = new TextField();
	private TextField mo_pub_port = new TextField();
	private TextField mo_pub_tag = new TextField();
	private XML_Settingsdef mo_pub_xml;

	// LEDs
	private Button led_red = new Button("Rot");
	private Button led_blue = new Button("Blau");
	private Button led_green = new Button("Grün");
	private Button led_rainbow = new Button("Rainbow");
	private TextField led_time = new TextField();

	public GUI(String title) {
		super(title);
		addWindowListener((WindowListener) new WindowController());
		Panel p_east = new Panel();
		p_east.add(new Label("Bitte geben Sie die IP-Adresse des Smart-Mirrors ein und bestätigen Sie mit \"ok\""));
		p_east.add(ip_1);
		p_east.add(ip_2);
		p_east.add(ip_3);
		p_east.add(ip_4);
		p_east.add(bt_ip);
		bt_ip.addActionListener(this);
		this.add("East", p_east);
		setSize(650, 200);
		setVisible(true);
	}

	private void led() {
		l_main[6] = new Label("LED Test");
		p_center[6] = new Panel();
		p_center[6].add(new Label("Dauer des Effekts in ms"));
		p_center[6].add(led_time);
		p_center[6].add(led_red);
		p_center[6].add(led_green);
		p_center[6].add(led_blue);
		p_center[6].add(led_rainbow);
		led_red.addActionListener(this);
		led_green.addActionListener(this);
		led_blue.addActionListener(this);
		led_rainbow.addActionListener(this);
	}

	private void mosquitto_pub() {
		l_main[5] = new Label("Mosquitto publisher");
		p_center[5] = new Panel();
		p_center[5].setLayout(new GridLayout(6, 5));
		p_center[5].add(new Label("Mosquitto publisher anschalten?:"));
		p_center[5].add(mo_pub_an_aus);
		p_center[5].add(new Label());
		p_center[5].add(new Label());
		p_center[5].add(new Label());
		p_center[5].add(new Label("IP-Adresse des Brokers:"));
		p_center[5].add(mo_pub_IP_1);
		p_center[5].add(mo_pub_IP_2);
		p_center[5].add(mo_pub_IP_3);
		p_center[5].add(mo_pub_IP_4);
		p_center[5].add(new Label("Port des Brokers:"));
		p_center[5].add(mo_pub_port);
		p_center[5].add(new Label());
		p_center[5].add(new Label());
		p_center[5].add(new Label());
		p_center[5].add(new Label("Tag to publish:"));
		p_center[5].add(mo_pub_tag);
		p_center[5].add(new Label());
		p_center[5].add(new Label());
		p_center[5].add(new Label());

		for (int i = 0; i < 10; i++) {
			p_center[5].add(new Label(""));
		}
	}

	private void mosquitto_pub_einlesen() {
		mo_pub_an_aus.setState(states[3]);
		String ip = mo_pub_xml.ein_aus(null, "mqtt_server");
		String[] ip_a = ip.split(Pattern.quote("."));
		mo_pub_IP_1.setText(ip_a[0]);
		mo_pub_IP_2.setText(ip_a[1]);
		mo_pub_IP_3.setText(ip_a[2]);
		mo_pub_IP_4.setText(ip_a[3]);
		mo_pub_port.setText(mo_sub_xml.ein_aus(null, "mqtt_server_port"));
		mo_pub_tag.setText(mo_sub_xml.ein_aus(null, "mqtt_topic_publish"));
	}

	private void mosquitto_pub_ausgeben() {
		if (states[3] != mo_pub_an_aus.getState()) {
			if (mo_pub_an_aus.getState()) {
				mo_pub_xml.changeFW(".kodi\\addons\\script.mqtt.publisher\\resources\\settings.xml");
				File f = new File(".kodi\\addons\\script.mqtt.publisher\\resources\\_settings.xml");
				f.delete();
			} else {
				mo_pub_xml.changeFW(".kodi\\addons\\script.mqtt.publisher\\resources\\_settings.xml");
				File f = new File(".kodi\\addons\\script.mqtt.publisher\\resources\\settings.xml");
				f.delete();
			}
			states[3] = !states[3];
		}
		String ip = mo_pub_IP_1.getText() + "." + mo_pub_IP_2.getText() + "." + mo_pub_IP_3.getText() + "."
				+ mo_pub_IP_4.getText();
		mo_pub_xml.ein_aus(ip, "mqtt_server");
		mo_pub_xml.ein_aus(mo_sub_port.getText(), "mqtt_server_prt");
		mo_pub_xml.ein_aus(mo_sub_tag.getText(), "mqtt_topic_publish");
		mo_pub_xml.print();
	}

	private void mosquitto_sub() {
		l_main[4] = new Label("Mosquitto subscriber");
		p_center[4] = new Panel();
		p_center[4].setLayout(new GridLayout(6, 5));
		p_center[4].add(new Label("Mosquitto subscriber anschalten?:"));
		p_center[4].add(mo_sub_an_aus);
		p_center[4].add(new Label());
		p_center[4].add(new Label());
		p_center[4].add(new Label());
		p_center[4].add(new Label("IP-Adresse des Brokers:"));
		p_center[4].add(mo_sub_IP_1);
		p_center[4].add(mo_sub_IP_2);
		p_center[4].add(mo_sub_IP_3);
		p_center[4].add(mo_sub_IP_4);
		p_center[4].add(new Label("Port des Brokers:"));
		p_center[4].add(mo_sub_port);
		p_center[4].add(new Label());
		p_center[4].add(new Label());
		p_center[4].add(new Label());
		p_center[4].add(new Label("Tag to subscribe:"));
		p_center[4].add(mo_sub_tag);
		p_center[4].add(new Label());
		p_center[4].add(new Label());
		p_center[4].add(new Label());

		for (int i = 0; i < 10; i++) {
			p_center[4].add(new Label(""));
		}
	}

	private void mosquitto_sub_einlesen() {
		mo_sub_an_aus.setState(states[2]);
		String ip = mo_sub_xml.ein_aus(null, "mqtt_server");
		String[] ip_a = ip.split(Pattern.quote("."));
		mo_sub_IP_1.setText(ip_a[0]);
		mo_sub_IP_2.setText(ip_a[1]);
		mo_sub_IP_3.setText(ip_a[2]);
		mo_sub_IP_4.setText(ip_a[3]);
		mo_sub_port.setText(mo_sub_xml.ein_aus(null, "mqtt_server_port"));
		mo_sub_tag.setText(mo_sub_xml.ein_aus(null, "mqtt_topic_subscribe"));
	}

	private void mosquitto_sub_ausgeben() {
		if (states[2] != mo_sub_an_aus.getState()) {
			if (mo_sub_an_aus.getState()) {
				mo_sub_xml.changeFW(".kodi\\addons\\script.mqtt.subscribe\\resources\\settings.xml");
				File f = new File("src\\.kodi\\addons\\script.mqtt.subscribe\\resources\\_settings.xml");
				f.delete();
			} else {
				mo_sub_xml.changeFW(".kodi\\addons\\script.mqtt.subscribe\\resources\\_settings.xml");
				File f = new File("src\\.kodi\\addons\\script.mqtt.subscribe\\resources\\settings_xml");
				f.delete();
			}
			states[2] = !states[2];
		}
		String ip = mo_sub_IP_1.getText() + "." + mo_sub_IP_2.getText() + "." + mo_sub_IP_3.getText() + "."
				+ mo_sub_IP_4.getText();
		mo_sub_xml.ein_aus(ip, "mqtt_server");
		mo_sub_xml.ein_aus(mo_sub_port.getText(), "mqtt_server_port");
		mo_sub_xml.ein_aus(mo_sub_tag.getText(), "mqtt_topic_subscribe");
		mo_sub_xml.print();
	}

	private void medienserver() {
		l_main[3] = new Label("Medienserver");
		p_center[3] = new Panel();
		p_center[3].setLayout(new GridLayout(6, 4));

		p_center[3].add(new Label("Bildmedienserver:"));
		p_center[3].add(me_bi_an_aus);
		p_center[3].add(new Label("IP-Adresse + Pfad:"));
		p_center[3].add(me_bi_IP);

		p_center[3].add(new Label("Videomedienserver:"));
		p_center[3].add(me_vi_an_aus);
		p_center[3].add(new Label("IP-Adresse + Pfad:"));
		p_center[3].add(me_vi_IP);

		p_center[3].add(new Label("Musikmedienserver:"));
		p_center[3].add(me_mu_an_aus);
		p_center[3].add(new Label("IP-Adresse + Pfad:"));
		p_center[3].add(me_mu_IP);
		for (int i = 0; i < 12; i++) {
			p_center[3].add(new Label(""));
		}
	}

	private void medienserver_einlesen() {
		states[4] = me_bi.getState();
		states[5] = me_vi.getState();
		states[6] = me_mu.getState();
		if (states[4]) {
			me_bi_an_aus.setState(true);
			me_bi_IP.setText(me_bi.ein());
		}
		if (states[5]) {
			me_vi_an_aus.setState(true);
			me_vi_IP.setText(me_vi.ein());
		}
		if (states[6]) {
			me_mu_an_aus.setState(true);
			me_mu_IP.setText(me_mu.ein());
		}
	}

	private void medienserver_ausgeben() {
		if (states[4] != me_bi_an_aus.getState()) {
			states[4] = me_bi_an_aus.getState();
			if (states[4]) {
				me_bi.aus(me_bi_IP.getText());
				ssh.senden("systemctl enable storage-pictures.mount");
			} else {
				me_bi.leer();
				ssh.senden("systemctl disable storage-pictures.mount");
			}
		}
		if (states[5] != me_vi_an_aus.getState()) {
			states[5] = me_vi_an_aus.getState();
			if (states[5]) {
				me_vi.aus(me_vi_IP.getText());
				ssh.senden("systemctl enable storage-music.mount");
			} else {
				me_vi.leer();
				ssh.senden("systemctl disable storage-music.mount");
			}
		}
		if (states[6] != me_mu_an_aus.getState()) {
			states[6] = me_mu_an_aus.getState();
			if (states[6]) {
				me_mu.aus(me_mu_IP.getText());
				ssh.senden("systemctl enable storage-movies.mount");
			} else {
				me_mu.leer();
				ssh.senden("systemctl disable storage-movies.mount");
			}
		}
	}

	private void e_mail() {
		l_main[2] = new Label("E-Mail");
		p_center[2] = new Panel();
		p_center[2].setLayout(new GridLayout(6, 2));
		p_center[2].add(new Label("E-Mail anschalten?:"));
		p_center[2].add(em_an_aus);
		p_center[2].add(new Label("Hostname des Imap-Server:"));
		p_center[2].add(em_host);
		p_center[2].add(new Label("Anmeldenamen:"));
		p_center[2].add(em_user);
		p_center[2].add(new Label("Passwort:"));
		p_center[2].add(em_pass);
		p_center[2].add(new Label("SSL verwenden?:"));
		p_center[2].add(em_ssl);
		for (int i = 0; i < 2; i++) {
			p_center[2].add(new Label(""));
		}
	}

	private void e_mail_einlesen() {
		em_an_aus.setState(states[1]);
		em_host.setText(em_xml.ein_aus(null, "imap_host"));
		em_user.setText(em_xml.ein_aus(null, "username"));
		em_pass.setText(em_xml.ein_aus(null, "password"));
		String b_a = em_xml.ein_aus(null, "use_ssl");
		if (b_a.contentEquals("true")) {
			em_ssl.setState(true);
		}
	}

	private void e_mail_ausgeben() {
		if (states[1] != em_an_aus.getState()) {
			if (em_an_aus.getState()) {
				em_xml.changeFW(".kodi\\userdata\\addon_data\\plugin.programm.xbmcmail\\settings.xml");
				File f = new File("src\\.kodi\\userdata\\addon_data\\plugin.programm.xbmcmail\\_settings.xml");
				f.delete();
			} else {
				em_xml.changeFW(".kodi\\userdata\\addon_data\\plugin.programm.xbmcmail\\_settings.xml");
				File f = new File("src\\.kodi\\userdata\\addon_data\\plugin.programm.xbmcmail\\settings_xml");
				f.delete();
			}
			states[1] = !states[1];
		}
		em_xml.ein_aus(em_host.getText(), "imap_host");
		em_xml.ein_aus(em_user.getText(), "username");
		em_xml.ein_aus(em_pass.getText(), "password");
		em_xml.ein_aus(em_ssl.getState() ? "true" : "false", "use_ssl");
		em_xml.print();
	}

	private void wetter() {
		l_main[1] = new Label("Wetter");
		p_center[1] = new Panel();
		p_center[1].setLayout(new GridLayout(6, 2));
		p_center[1].add(new Label("Ort 1"));
		p_center[1].add(wet_or_1);
		p_center[1].add(new Label("Ort 2"));
		p_center[1].add(wet_or_2);
		p_center[1].add(new Label("Ort 3"));
		p_center[1].add(wet_or_3);
		p_center[1].add(new Label("Ort 4"));
		p_center[1].add(wet_or_4);
		p_center[1].add(new Label("Ort 5"));
		p_center[1].add(wet_or_5);
		for (int i = 0; i < 2; i++) {
			p_center[1].add(new Label(""));
		}
	}

	private void wetter_einlesen() {
		wet_or_1.setText(wet_xml.ein_aus(null, "Location1"));
		wet_or_2.setText(wet_xml.ein_aus(null, "Location2"));
		wet_or_3.setText(wet_xml.ein_aus(null, "Location3"));
		wet_or_4.setText(wet_xml.ein_aus(null, "Location4"));
		wet_or_5.setText(wet_xml.ein_aus(null, "Location5"));
	}

	private void wetter_ausgeben() {
		String[] p = new String[5];
		try {
			p[0] = URLEncoder.encode(wet_or_1.getText(), "UTF-8");
			p[1] = URLEncoder.encode(wet_or_2.getText(), "UTF-8");
			p[2] = URLEncoder.encode(wet_or_3.getText(), "UTF-8");
			p[3] = URLEncoder.encode(wet_or_4.getText(), "UTF-8");
			p[4] = URLEncoder.encode(wet_or_5.getText(), "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			new STDErrFrame("Fehler beim UTF konvertieren");
		}
		for (int i = 0; i < 5; i++) {
			try {
				URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=+" + p[i]
						+ ",DE&appid=407a2d29273d36ff306bd4dfc587de4c");
				Reader re = new InputStreamReader(url.openStream());
				BufferedReader br = new BufferedReader(re);
				StringTokenizer st = new StringTokenizer(br.readLine(), "\":{},");
				int tmp = 0;
				int id = 0;
				String name = null;
				while (st.hasMoreTokens()) {
					String s = st.nextToken();
					if (s.contentEquals("id")) {
						if (tmp != 2) {
							st.nextToken();
							tmp++;
						} else {
							id = Integer.parseInt(st.nextToken());
							System.out.println(id + "");
						}

					} else if (s.contentEquals("name")) {
						name = st.nextToken();
					}
				}
				wet_xml.ein_aus(name, "Location" + (i + 1));
				wet_xml.ein_aus("" + id, "Location" + (i + 1) + "ID");
			} catch (IOException e) {
				new STDErrFrame("Fehler beim Parsen der Orte für das Wetter");
			}

		}
		wet_xml.print();
	}

	private void rss() {
		l_main[0] = new Label("RSS-Feeds");
		p_center[0] = new Panel();
		p_center[0].setLayout(new GridLayout(6, 2));
		p_center[0].add(new Label("RSS-Feed anschalten?:"));
		p_center[0].add(rss_an_aus);
		p_center[0].add(new Label("Adresse Feed 1"));
		p_center[0].add(rss_fe_1);
		p_center[0].add(new Label("Adresse Feed 2"));
		p_center[0].add(rss_fe_2);
		p_center[0].add(new Label("Adresse Feed 3"));
		p_center[0].add(rss_fe_3);
		p_center[0].add(new Label("Adresse Feed 4"));
		p_center[0].add(rss_fe_4);
		p_center[0].add(new Label("Adresse Feed 5"));
		p_center[0].add(rss_fe_5);
	}

	private void rss_einlesen() {
		rss_an_aus.setState(states[0]);
		String[] value_alt = rss_xml.ein_aus(null);
		rss_fe_1.setText(value_alt[0]);
		rss_fe_2.setText(value_alt[1]);
		rss_fe_3.setText(value_alt[2]);
		rss_fe_4.setText(value_alt[3]);
		rss_fe_5.setText(value_alt[4]);
	}

	private void rss_ausgeben() {
		if (states[0] != rss_an_aus.getState()) {
			if (rss_an_aus.getState()) {
				rss_xml.changeFW(".kodi\\userdata\\RssFeeds.xml");
				File f = new File("src\\.kodi\\userdata\\_RssFeeds.xml");
				f.delete();
			} else {
				rss_xml.changeFW(".kodi\\userdata\\_RssFeeds.xml");
				File f = new File("src\\.kodi\\userdata\\RssFeeds.xml");
				f.delete();
			}
			states[0] = !states[0];
		}
		String[] val = new String[5];
		val[0] = rss_fe_1.getText();
		val[1] = rss_fe_2.getText();
		val[2] = rss_fe_3.getText();
		val[3] = rss_fe_4.getText();
		val[4] = rss_fe_5.getText();
		rss_xml.ein_aus(val);
		rss_xml.print();
	}

	private void InitializePanel() {
		Panel p_west = new Panel();
		p_west.setLayout(new GridLayout(l_main.length, 1, 0, 0));
		for (int i = 0; i < size_mainMenu; i++) {
			p_west.add(l_main[i]);
			l_main[i].addMouseListener(this);
			l_main[i].setBackground(null);
			this.add("Center", p_center[i]);
		}
		l_main[position_mainMenu].setBackground(Color.GRAY);
		this.add("Center", p_center[position_mainMenu]);
		this.add("West", p_west);
	}

	private void InitializeMenu() {
		MenuBar mMenuBar = new MenuBar();
		setMenuBar(mMenuBar);
		Menu mMenu;
		mMenuBar.add(mMenu = new Menu("Datei"));
		mMenu.add(m_rec = new MenuItem("Konfigurationsdaten von Mirror holen"));
		mMenu.add(m_sen = new MenuItem("Konfigurationsdaten an Mirror senden"));
		m_rec.addActionListener(this);
		m_sen.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == bt_ip) {
			this.setVisible(false);
			this.removeAll();
			String[] ip = new String[4];
			ip[0] = ip_1.getText();
			ip[1] = ip_2.getText();
			ip[2] = ip_3.getText();
			ip[3] = ip_4.getText();
			if (Integer.parseInt(ip[0]) < 0 || Integer.parseInt(ip[0]) > 255 || Integer.parseInt(ip[1]) < 0
					|| Integer.parseInt(ip[1]) > 255 || Integer.parseInt(ip[2]) < 0 || Integer.parseInt(ip[2]) > 255
					|| Integer.parseInt(ip[3]) < 0 || Integer.parseInt(ip[3]) > 255) {
				new STDErrFrame("Ungültige IP-Adresse");
			}
			ftp = new FTP(ip);
			ssh = new SSH(ip);
			InitializeMenu();
			rss();
			wetter();
			e_mail();
			medienserver();
			mosquitto_sub();
			mosquitto_pub();
			led();
			InitializePanel();
			setSize(1000, 290);
			setVisible(true);
		} else if (event.getSource() == m_rec) {
			ftp.holen();
			System.out.println("Received data");
			ueberpruefeFiles();
			rss_einlesen();
			wetter_einlesen();
			e_mail_einlesen();
			medienserver_einlesen();
			mosquitto_sub_einlesen();
			mosquitto_pub_einlesen();
		} else if (event.getSource() == m_sen) {
			rss_ausgeben();
			wetter_ausgeben();
			e_mail_ausgeben();
			medienserver_ausgeben();
			mosquitto_sub_ausgeben();
			mosquitto_pub_ausgeben();
			ftp.senden();
			System.out.println("Send data");
		} else if (event.getSource() == led_red) {
			ssh.senden("cd hyperion/bin && ./hyperion-remote -c red -d " + led_time.getText());
		} else if (event.getSource() == led_green) {
			ssh.senden("cd hyperion/bin && ./hyperion-remote -c green -d " + led_time.getText());
		} else if (event.getSource() == led_blue) {
			ssh.senden("cd hyperion/bin && ./hyperion-remote -c blue -d " + led_time.getText());
		} else if (event.getSource() == led_rainbow) {
			ssh.senden("cd hyperion/bin && ./hyperion-remote -e \"Rainbow swirl fast\" -d " + led_time.getText());
		}
	}

	private void ueberpruefeFiles() {
		wet_xml = new XML_Settings(".kodi\\userdata\\addon_data\\weather.openweathermap.extended\\settings.xml");
		File file = new File("src\\.kodi\\userdata");
		String[] f_t = file.list();
		for (String file_t : f_t) {
			if (file_t.contentEquals("RssFeeds.xml")) {
				states[0] = true;
				rss_xml = new XML_RSS(".kodi\\userdata\\RssFeeds.xml");
			} else if (file_t.contains("_RssFeeds.xml")) {
				states[0] = false;
				rss_xml = new XML_RSS(".kodi\\userdata\\_RssFeeds.xml");
			}
		}
		file = new File("src\\.kodi\\userdata\\addon_data\\plugin.programm.xbmcmail");
		f_t = file.list();
		for (String file_t : f_t) {
			if (file_t.contentEquals("settings.xml")) {
				states[1] = true;
				em_xml = new XML_Settings(".kodi\\userdata\\addon_data\\plugin.programm.xbmcmail\\settings.xml");
			} else if (file_t.contains("_settings.xml")) {
				states[1] = false;
				em_xml = new XML_Settings(".kodi\\userdata\\addon_data\\plugin.programm.xbmcmail\\_settings.xml");
			}
		}
		file = new File("src\\.kodi\\addons\\script.mqtt.subscribe\\resources");
		f_t = file.list();
		for (String file_t : f_t) {
			if (file_t.contentEquals("settings.xml")) {
				states[2] = true;
				mo_sub_xml = new XML_Settingsdef(".kodi\\addons\\script.mqtt.subscribe\\resources\\settings.xml");
			} else if (file_t.contains("_settings.xml")) {
				states[2] = false;
				mo_sub_xml = new XML_Settingsdef(".kodi\\addons\\script.mqtt.subscribe\\resources\\_settings.xml");
			}
		}
		file = new File("src\\.kodi\\addons\\service.mqtt.publisher\\resources");
		f_t = file.list();
		for (String file_t : f_t) {
			if (file_t.contentEquals("settings.xml")) {
				states[3] = true;
				mo_pub_xml = new XML_Settingsdef(".kodi\\addons\\service.mqtt.publisher\\resources\\settings.xml");
			} else if (file_t.contains("_settings.xml")) {
				states[3] = false;
				mo_pub_xml = new XML_Settingsdef(".kodi\\addons\\service.mqtt.publisher\\resources\\_settings.xml");
			}
		}
	}

	public static void main(String[] args) {
		new GUI("SmartMirror einrichten");
	}

	@Override
	public void mouseClicked(MouseEvent e) { 
		this.remove(p_center[position_mainMenu]);
		this.validate();
		for (int i = 0; i < size_mainMenu; i++) {
			l_main[i].setBackground(null);
			if (e.getSource() == l_main[i]) {
				position_mainMenu = i;
				l_main[position_mainMenu].setBackground(Color.GRAY);
				this.add("Center", p_center[position_mainMenu]);
				this.validate();
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		

	}
}

class WindowController extends WindowAdapter {
	@Override
	public void windowClosing(WindowEvent e) {
		File src = new File("src");
		löschen(src.getName());
		System.exit(0);
	}

	private void löschen(String s) {
		if (!(s.contentEquals("src\\FTP.java") || s.contentEquals("src\\GUI.java")
				|| s.contentEquals("src\\Medienserver.java") || s.contentEquals("src\\SSH.java")
				|| s.contentEquals("src\\STDErrFrame.java") || s.contentEquals("src\\XML_RSS.java")
				|| s.contentEquals("src\\XML_Settings.java") || s.contentEquals("src\\XML_Settingsdef.java"))) {
			File f = new File(s);
			if (f.isDirectory()) {
				File[] files = f.listFiles();
				for (File fi : files) {
					löschen(s + "\\" + fi.getName());
				}
				if (f.delete()) {
					System.out.println(f + " gelöscht");
				} else {
					System.out.println(f + " nicht gelöscht");
				}
			} else {
				if (f.delete()) {
					System.out.println(f + " gelöscht");
				} else {
					System.out.println(f + " nicht gelöscht");
				}
			}
		}
	}
}