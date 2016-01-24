package ca.ualberta;

import lejos.hardware.Button;
import lejos.hardware.motor.NXTMotor;
import lejos.utility.Delay;

public class deadReckoning {
	public static final double wheeldiameter = 56;
	public static final double wheelwidth = 28;
	public static final double wheeltowheeldiameter = 128.5;
	public static double heading = 0;
	public static double x_loc = 0;
	public static double y_loc = 0;
	
	static int[][] command = {
		      { 80, 60, 2},
		      { 60, 60, 1}, 
		      {-50, 80, 2}
		    };
	
//	public static void main(String[] args) {		// Uncomment this to make this a compile target
		public static void main() {
		NXTMotor motorA = RobotInfo.getMotorA();
		NXTMotor motorB = RobotInfo.getMotorB();

		double distancePerTick = wheeldiameter*Math.PI/360;	// = 2pi*r / 360 = arcdistance
		double ticksPerRot = Math.PI*wheeltowheeldiameter/distancePerTick;
		double radiansPerTick = (2*Math.PI)/ticksPerRot;
		
		
		for (int i = 0; i<3; i++){
			motorA.setPower(command[i][0]);
			motorB.setPower(command[i][1]);

			double prevA = motorA.getTachoCount();
			double prevB = motorB.getTachoCount();
			long now = System.currentTimeMillis();
			
			motorA.forward();
			motorB.forward();
			
			while (System.currentTimeMillis()-now < command[i][2]*1000){
				//Delay.msDelay(20);
				long t = System.currentTimeMillis();
				
				// distance per tick: wheeldiameter*pi/360 (in degrees)
				// deltaHeading = (right ticks -  left ticks)* (wheeldiameter/(2*wheeltowheeldiameter))
				// deltaDistance = (left ticks + right ticks)/2* (wheeldiameter*pi/360)
				// heading = heading + deltaHeading
				// deltaX = deltaDistance* cos(heading)
				// deltaY = deltaDistance* sin(heading)
				double currentA = motorA.getTachoCount();
				double currentB = motorB.getTachoCount();
				
				double speedA = currentA - prevA;
				double speedB = currentB - prevB;
				
				double d_distance = (speedA+speedB)/2 
						* distancePerTick;
				heading += (speedB-speedA)*(radiansPerTick/2);
				
				//double d_distance = (motorA.getTachoDiff()+motorB.getTachoDiff())/2 
						//* distancePerTick;
				
				//heading += (motorA.getTachoDiff() - motorB.getTachoDiff())
									//*(wheeldiameter/(2*wheeltowheeldiameter));
				
				x_loc += d_distance*Math.cos(heading);
				y_loc += d_distance*Math.sin(heading);
				
				prevA = currentA;
				prevB = currentB;
				while(System.currentTimeMillis()-t<20){Delay.msDelay(1);};
			}	
			System.out.format("X = %.2f cm\nY = %.2f cm\n Heading = %.2f deg\n\n", 
					x_loc/10, y_loc/10, Math.toDegrees(heading) % 360);
		}
		motorA.stop();
		motorB.stop();
		motorA.close();
		motorB.close();
		
		Button.waitForAnyPress();
	}
}
