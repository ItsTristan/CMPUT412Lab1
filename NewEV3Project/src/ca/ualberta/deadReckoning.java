package ca.ualberta;

import lejos.hardware.motor.NXTMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.EncoderMotor;
import lejos.utility.Delay;

public class deadReckoning {
	public static float heading = 0;
	public static float x_loc = 0;
	public static float y_loc = 0;
	
	static int[][] command = {
		      { 80, 60, 2},
		      { 60, 60, 1},
		      {-50, 80, 2}
		    };
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EncoderMotor motorA =new NXTMotor (MotorPort.A);
		EncoderMotor motorB =new NXTMotor (MotorPort.B);
		
		motorA.resetTachoCount();
		motorB.resetTachoCount();
		
		for (int i = 0; i<3; i++){
			motorA.setPower(command[i][0]);
			motorB.setPower(command[i][1]);
			motorA.forward();
			motorB.forward();
			
			long now = System.currentTimeMillis();
			while (System.currentTimeMillis()-now < command[i][2]*1000){
				heading 
			}
			
		}
	}
}
