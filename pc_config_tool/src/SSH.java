import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSH {
	private Session session;

	public SSH(String[] ip_a) {
		String user = "root";
		String password = "openelec";
		try {
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			JSch jsch = new JSch();
			String ip = ip_a[0] + "." + ip_a[1] + "." + ip_a[2] + "." + ip_a[3];
			session = jsch.getSession(user, ip, 22);
			session.setPassword(password);
			session.setConfig(config);
			session.connect();
		} catch (JSchException e) {
			new STDErrFrame("Fehler beim SSH verbinden");
		}
	}

	public void senden(String command) {
		try {
			Channel channel = session.openChannel("exec");
			((ChannelExec) channel).setCommand(command);
			channel.setInputStream(null);
			((ChannelExec) channel).setErrStream(System.err);
			channel.connect();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ee) {
			}
			channel.disconnect();
		} catch (JSchException e) {
			new STDErrFrame("Fehler bei SSH senden");
		}
	}

	public void abmelden() {
		session.disconnect();
	}
}
