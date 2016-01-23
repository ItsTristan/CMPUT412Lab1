package ca.ualberta;

import lejos.hardware.Button;

public class deadReckoning {
	public static final double wheeldiameter = 56;
	public static final double wheelwidth = 28;
	public static final double wheeltowheeldiameter = 126.5;
	public static double heading = 0;
	public static double x_loc = 0;
	public static double y_loc = 0;
	
	static int[][] command = {
		      { 80, 40, 1},
		      { 80, 80, 1},
		      {80, 80, 1}
		    };
	
//	public static void main(String[] args) {		// Uncomment this to make this a compile target
		public static void main() {
		SaferMotor motorA = RobotInfo.getMotorA();
		SaferMotor motorB = RobotInfo.getMotorB();
		
		motorA.resetTachoCount();
		motorB.resetTachoCount();

		double distancePerTick = wheeldiameter*Math.PI/360;	// = 2pi*r / 360 = arcdistance
		
		for (int i = 0; i<3; i++){
			motorA.setPower(command[i][0]);
			motorB.setPower(command[i][1]);
			motorA.forward();
			motorB.forward();

			
			
			double prevA = motorA.getRealTachoCount();
			double prevB = motorB.getRealTachoCount();
			
		
			long now = System.currentTimeMillis();
			while (System.currentTimeMillis()-now < command[i][2]*1500){
				// distance per tick: wheeldiameter*pi/360 (in degrees)
				// deltaHeading = (right ticks -  left ticks)* (wheeldiameter/(2*wheeltowheeldiameter))
				// deltaDistance = (left ticks + right ticks)/2* (wheeldiameter*pi/360)
				// heading = heading + deltaHeading
				// deltaX = deltaDistance* cos(heading)
				// deltaY = deltaDistance* sin(heading)
				double currentA = motorA.getRealTachoCount();
				double currentB = motorB.getRealTachoCount();
				
				double speedA = currentA - prevA;
				double speedB = currentB - prevB;
				
				double d_distance = (speedA+speedB)/2 
						* distancePerTick;
				heading += (speedB-speedA)*(wheeldiameter/(2*wheeltowheeldiameter));
				
				//double d_distance = (motorA.getTachoDiff()+motorB.getTachoDiff())/2 
						//* distancePerTick;
				
				//heading += (motorA.getTachoDiff() - motorB.getTachoDiff())
									//*(wheeldiameter/(2*wheeltowheeldiameter));
				
				x_loc += d_distance*Math.cos(Math.toRadians(heading));
				y_loc += d_distance*Math.sin(Math.toRadians(heading));
				
				prevA = currentA;
				prevB = currentB;
			}	
			System.out.format("X = %.2f cm\nY = %.2f cm\n Heading = %.2f degrees\n\n", x_loc/10, y_loc/10, heading);
		}
		motorA.stop();
		motorB.stop();
		motorA.close();
		motorB.close();
		
		Button.waitForAnyPress();
	}
}
