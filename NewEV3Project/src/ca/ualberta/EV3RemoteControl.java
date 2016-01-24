package ca.ualberta;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RemoteEV3;

public class EV3RemoteControl extends JFrame implements KeyListener {

	private static final long serialVersionUID = -2575431873208821198L;

	public static RemoteEV3 ev3;

	public static String MACaddress = "00:16:53:44:97:4F";
	public static String address = "10.0.1.1";

	public static RMIRegulatedMotor motorA;
	public static RMIRegulatedMotor motorB;
	
	private JLabel motorSpeedLabel;

	public EV3RemoteControl() {
		this.addKeyListener(this);
		
		// Add a listener so we dispose of the motors correctly
		final JFrame frame = this;
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				if (JOptionPane.showConfirmDialog(frame, "Are you sure to close this window?", "Really Closing?",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
					disconnect();
					System.exit(0);
				}
			}
		});
		
		// Try to connect
		try {
			connect();
		} catch (RemoteException | MalformedURLException | NotBoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Error occured while connecting");
			e.printStackTrace();
			disconnect();
		}
		
		frame.setSize(400, 400);
		
		GridLayout layout = new GridLayout(2, 1);
		frame.setLayout(layout);
		JLabel info = new JLabel("<html>Press WASDQE to move,<br/>Pg Up/Pg Down to change speed,<br/>Space to stop</html>");
		motorSpeedLabel = new JLabel("Motor Speed = " + forwardPower);
		frame.add(info);
		frame.add(motorSpeedLabel);
	}

	public static void main(String args[]) {
		EV3RemoteControl rc = new EV3RemoteControl();
		rc.setVisible(true);
		rc.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void connect() throws RemoteException, MalformedURLException, NotBoundException {
		System.out.println("Establishing link...");
		ev3 = new RemoteEV3(address);
		motorA = ev3.createRegulatedMotor("A", 'L');
		motorB = ev3.createRegulatedMotor("B", 'L');
		System.out.println("EV3 is Connected");
	}

	public void disconnect() {
		// Close motorA
		try {
			if (motorA != null) motorA.close();
			System.out.println("Port A is disconnected\n");
		} catch (Exception e) {
			System.err.println("Could not close motor A");
			e.printStackTrace();
		}
		
		// Close motorB
		try {
			if (motorB != null) motorB.close();
			System.out.println("Port B is disconnected\n");
		} catch (Exception e) {
			System.err.println("Could not close motor B");
			e.printStackTrace();
		}
		
		System.out.println("Disconnected Ports\n");
		System.exit(0);
	}

	/// REGION: Key Events
	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("Key Pressed: " + e);
		try {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_W:
				forward(true);
				break;
			case KeyEvent.VK_A:
				turnLeft(true);
				break;
			case KeyEvent.VK_S:
				backward(true);
				break;
			case KeyEvent.VK_D:
				turnRight(true);
				break;
			case KeyEvent.VK_Q:
				pivotLeft(true);
				break;
			case KeyEvent.VK_E:
				pivotRight(true);
				break;
			}
		} catch (Exception err) {
			System.err.println("Error sending message: ");
			err.printStackTrace();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		System.out.println("Key Released: " + e);
		try {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_W:
			case KeyEvent.VK_A:
			case KeyEvent.VK_S:
			case KeyEvent.VK_D:
				
			case KeyEvent.VK_Q:
			case KeyEvent.VK_E:
				
			case KeyEvent.VK_SPACE:
				stop();
				break;
			case KeyEvent.VK_ESCAPE:
				disconnect();
				break;
			case KeyEvent.VK_PAGE_UP:
				speedUp();
				break;
			case KeyEvent.VK_PAGE_DOWN:
				speedDown();
				break;
			}
		} catch (Exception err) {
			System.err.println("Error sending message: ");
			err.printStackTrace();
		}
	}

	private static final int MIN_SPEED = 0;
	private static final int MAX_SPEED = 740;
	
	private int motorAPower = 0;
	private int motorBPower = 0;

	private static int forwardPower = 240;

	private void forward(boolean isPressed) throws RemoteException {
		
		motorAPower = forwardPower;
		motorBPower = forwardPower;
		
		moveMotors();
	}

	private void backward(boolean isPressed) throws RemoteException {
		motorAPower = -forwardPower;
		motorBPower = -forwardPower;

		moveMotors();
	}

	private void turnLeft(boolean isPressed) throws RemoteException {
		motorBPower = forwardPower;

		moveMotors();
	}

	private void turnRight(boolean isPressed) throws RemoteException {
		motorAPower = forwardPower;

		moveMotors();
	}

	private void pivotLeft(boolean isPressed) throws RemoteException {
		motorAPower = -forwardPower;
		motorBPower = forwardPower;

		moveMotors();
	}

	private void pivotRight(boolean isPressed) throws RemoteException {
		motorAPower = forwardPower;
		motorBPower = -forwardPower;

		moveMotors();
	}

	private void stop() throws RemoteException {
		motorA.stop(true);
		motorB.stop(true);

		motorAPower = 0;
		motorBPower = 0;

		updateLabels();
	}

	private void speedUp() throws RemoteException {
		int diff = clamp(forwardPower+10, MIN_SPEED, MAX_SPEED) - forwardPower;
		forwardPower += diff;
		
		if (motorAPower != 0) 
			motorAPower = (motorAPower < 0 ? -1 : 1) * diff + motorAPower;
		if (motorBPower != 0) 
			motorBPower = (motorBPower < 0 ? -1 : 1) * diff + motorBPower;
		
		moveMotors();
	}

	private void speedDown() throws RemoteException {
		int diff = clamp(forwardPower-10, MIN_SPEED, MAX_SPEED) - forwardPower;
		forwardPower += diff;
		
		if (motorAPower != 0) 
			motorAPower = (motorAPower < 0 ? -1 : 1) * diff + motorAPower;
		if (motorBPower != 0) 
			motorBPower = (motorBPower < 0 ? -1 : 1) * diff + motorBPower;

		moveMotors();
	}
	
	private void moveMotors() throws RemoteException {
		motorA.setSpeed(motorAPower * (motorAPower < 0 ? -1 : 1));
		motorB.setSpeed(motorBPower * (motorBPower < 0 ? -1 : 1));
		System.out.format("Set motor speeds to A = %d, B = %d\n", motorAPower, motorBPower);

		if (motorAPower > 0) motorA.forward();
		else if (motorAPower < 0) motorA.backward();
		else motorA.stop(false);
		
		if (motorBPower > 0) motorB.forward();
		else if (motorBPower < 0) motorB.backward();
		else motorB.stop(false);
		
		updateLabels();
	}
	
	private int clamp(int value, int lo, int hi) {
		if (value < lo) return lo;
		if (value > hi) return hi;
		return value;
	}
	
	private void updateLabels() {
		motorSpeedLabel.setText("Motor Speed = " + forwardPower);
	}
}
