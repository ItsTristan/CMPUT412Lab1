package ca.ualberta;

import lejos.hardware.motor.NXTMotor;
import lejos.hardware.port.Port;

public class SaferMotor extends NXTMotor {
	private int tachocount = 0;
	private int prevtacho = 0;
	
	public SaferMotor(Port port) {
		super(port);
		super.resetTachoCount();
	}
	
//	@Override
	public void resetTachoCount() {
		tachocount = super.getTachoCount();
		prevtacho = tachocount;
	}
	
//	@Override
	public int getTachoCount() {
		return super.getTachoCount() - tachocount;
	}
	
	public int getTachoDiff() {
		int result = this.getTachoCount() - prevtacho;
		prevtacho += result;
		return result;
	}
	
	public int getRealTachoCount() {
		return super.getTachoCount();
	}
}
