package ca.ualberta;

import lejos.utility.Delay;

public class deadReckoning {
	public static final double wheeldiameter = 56;
	public static final double wheelwidth = 28;
	public static final double wheeltowheeldiameter = 126.5;
	public static double heading = 0;
	public static float x_loc = 0;
	public static float y_loc = 0;
	
	static int[][] command = {
		      { 80, 60, 2},
		      { 60, 60, 1},
		      {-50, 80, 2}
		    };
	
	public static void main(String[] args) {
		SaferMotor motorA = RobotInfo.getMotorA();
		SaferMotor motorB = RobotInfo.getMotorB();
		
		motorA.resetTachoCount();
		motorB.resetTachoCount();
		
		for (int i = 0; i<3; i++){
			motorA.setPower(command[i][0]);
			motorB.setPower(command[i][1]);
			motorA.forward();
			motorB.forward();
			
			motorA.resetTachoCount();
			motorB.resetTachoCount();
			long now = System.currentTimeMillis();
			while (System.currentTimeMillis()-now < command[i][2]*1000){
				// distance per tick: wheeldiameter/2 (in degrees)
				// deltaHeading = (right ticks -  left ticks)* (wheeldiameter/(2*wheeltowheeldiameter))
				// deltaDistance = (left ticks + right ticks/2)* (wheeldiameter*pi/360)
				// heading = heading + deltaHeading
				// deltaX = deltaDistance* cos(heading)
				// deltaY = deltaDistance* sin(heading)
				heading += (motorA.getTachoDifferential() - motorB.getTachoDifferential())
									*(wheeldiameter/(2*wheeltowheeldiameter)); 
				double d_distance = (motorA.getTachoDifferential() + motorB.getTachoDifferential()/2) 
									* (wheeldiameter*Math.PI/360); 
				x_loc += d_distance*Math.cos(Math.toRadians(heading));
				y_loc += d_distance*Math.sin(Math.toRadians(heading));		
				Delay.msDelay(10);
			}	
			System.out.format("X = %.2f \nY = %.2f \n Heading = %.2f degrees", x_loc, y_loc, heading);
		}
		motorA.stop();
		motorB.stop();
		motorA.close();
		motorB.close();
	}
}
