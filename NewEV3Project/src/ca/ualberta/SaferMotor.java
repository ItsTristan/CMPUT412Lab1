package ca.ualberta;

import lejos.hardware.motor.NXTMotor;
import lejos.hardware.port.Port;

public class SaferMotor extends NXTMotor {
	private int tachocount = 0;
	private int prevtacho = 0;
	
	public SaferMotor(Port port) {
		super(port);
	}
	
//	@Override
	public void resetTachoCount() {
		tachocount = this.getTachoCount();
		prevtacho = tachocount;
	}
	
//	@Override
	public int getTachoCount() {
		return super.getTachoCount() - tachocount;
	}
	
	public int getTachoDifferential() {
		int result = this.getTachoCount() - prevtacho;
		prevtacho = this.getTachoCount();
		return result;
	}
}
