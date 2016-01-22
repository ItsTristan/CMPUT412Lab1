package ca.ualberta;

import lejos.hardware.port.MotorPort;
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
		SaferMotor motorA =new SaferMotor (MotorPort.A);
		SaferMotor motorB =new SaferMotor (MotorPort.B);
		
		motorA.resetTachoCount();
		motorB.resetTachoCount();
		
		for (int i = 0; i<3; i++){
			motorA.setPower(command[i][0]);
			motorB.setPower(command[i][1]);
			motorA.forward();
			motorB.forward();
			
			long now = System.currentTimeMillis();
			while (System.currentTimeMillis()-now < command[i][2]*1000){
				//heading 
				Delay.msDelay(10);
			}	
		}
		
		motorA.close();
		motorB.close();
	}
}
