import java.awt.*;
import java.awt.event.*;

public class STDErrFrame extends Frame implements ActionListener {
	private static final long serialVersionUID = -8056417519273074298L;
	private Button bt = new Button("OK");

	public STDErrFrame(String title) {
		super(title);
		addWindowListener((WindowListener) new WindowController());
		this.add(new Label("Programm unerwartet beendet"));
		this.add(bt);
		bt.addActionListener(this);
	}

	class WindowController extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent e) {
			System.exit(1);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.exit(1);
	}
}
