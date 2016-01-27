package ca.ualberta;

import lejos.hardware.Button;
import lejos.hardware.motor.NXTMotor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.SampleProvider;
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
	//use these for turn tests
	// fast for long time left turn
	static int[][] command2 = { { 80, 0, 3} };
	// fast for long time right turn
	static int[][] command3 = { { 80, 0, 3} };
	// slow right turn
	static int[][] command4 = { { 40, 0, 4} };
	// slow left turn
	static int[][] command5 = { { 0, 40, 4} };	
	
//	public static void main(String[] args) {		// Uncomment this to make this a compile target
		public static void main() {
		//command = command6; // change command for testing cases
		NXTMotor motorA = RobotInfo.getMotorA();
		NXTMotor motorB = RobotInfo.getMotorB();

		double distancePerTick = wheeldiameter*Math.PI/360;	// = 2pi*r / 360 = arcdistance
		double ticksPerRot = Math.PI*wheeltowheeldiameter/distancePerTick;
		double radiansPerTick = (2*Math.PI)/ticksPerRot;
		
		double prevA = motorA.getTachoCount();
		double prevB = motorB.getTachoCount();
		
		EV3GyroSensor gySensD = RobotInfo.getSensorD();
		gySensD.reset();
		SampleProvider angleMode = gySensD.getAngleMode();
		float[] sample = new float[angleMode.sampleSize()]; 
		angleMode.fetchSample(sample, 0);
		float sensorAngleStart = sample[0];
		float sensorAngleEnd = sensorAngleStart;
		
		for (int i = 0; i<command.length; i++){
			motorA.setPower(command[i][0]);
			motorB.setPower(command[i][1]);

			long now = System.currentTimeMillis();
			
			motorA.forward();
			motorB.forward();
			
			while (System.currentTimeMillis()-now < command[i][2]*1000){
				//Delay.msDelay(20);
				long t = System.currentTimeMillis();
				// MATH
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
				
				x_loc += d_distance*Math.cos(heading);
				y_loc += d_distance*Math.sin(heading);
				
				prevA = currentA;
				prevB = currentB;
				while(System.currentTimeMillis()-t<20){Delay.msDelay(1);};
			}	
			//Extra iteration to correct for lost loop for 
			//delay when changing instructions
			double currentA = motorA.getTachoCount();
			double currentB = motorB.getTachoCount();
			
			double speedA = currentA - prevA;
			double speedB = currentB - prevB;
			
			double d_distance = (speedA+speedB)/2 
					* distancePerTick;
			heading += (speedB-speedA)*(radiansPerTick/2);
			
			x_loc += d_distance*Math.cos(heading);
			y_loc += d_distance*Math.sin(heading);
			
			prevA = currentA;
			prevB = currentB;
			angleMode.fetchSample(sample, 0);
			sensorAngleEnd = sample[0];
		}
		motorA.stop();
		motorB.stop();
		
		System.out.format("X = %.2fcm\nY = %.2fcm\n Heading = %.2fdeg\n Gstart: %.2fdeg\n Gend: %.2fdeg\n ", 
					x_loc/10, y_loc/10, Math.toDegrees(heading), sensorAngleStart, sensorAngleEnd);
		
		motorA.close();
		motorB.close();
		
		Button.waitForAnyPress();
	}
}
