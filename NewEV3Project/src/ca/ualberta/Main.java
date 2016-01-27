package ca.ualberta;

/*
*Version 0.8.1-beta
*/

import lejos.hardware.Button;
import lejos.hardware.motor.NXTMotor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.EncoderMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class Main {

	public static final int motorspeed = 80;
	public static final int finespeed = 10;

	public static final int max_loops = 100;

	public static final float wheeldiameter = 56;
	public static final float wheelwidth = 28;
	public static final float meanwheeldistance = 128f;
	public static final float minwheeldistance = 100f;
	public static final float maxwheeldistance = 156f;
	public static final float wheeltowheeldistance = 106.30f;	// Adjusted
	
	public static final int MAX_SPEED = motorspeed+5;
	
	public static final int correction_threshold = 1;
	
	/**
	 * Mode:
	 *  0 = Manual Control
	 *  1 = Dead Reckoning
	 *  2 = Braitenberg
	 *  
	 * 	(4 = Tests)
	 */
	public static int num_modes = 3;

	public static void main(String[] args) {
		selectMode();
	}
	
	public static void selectMode() {
		System.out.println("Select Mode:");
		int choice = 0;
		while (true) {
			switch (choice) {
			case 0:
				System.out.println("Manual Control");
				break;
			case 1:
				System.out.println("Dead Reckoning");
				break;
			case 2:
				System.out.println("Braitenberg");
				break;
			}
			
			int btn = Button.waitForAnyPress();
			if (check_fields(btn, Button.ID_DOWN)){
				choice = (choice + 1) % num_modes;
			}
			else if (check_fields(btn, Button.ID_UP)){
				choice = (choice + num_modes-1) % num_modes;
			}
			else if (check_fields(btn, Button.ID_ENTER)) {
				switch(choice) {
				case 0:
					manual_control();
					break;
				case 1:
					deadReckoning.main();
					break;
				case 2:
					Braitenberg.main();
					break;
				}
				return;
			} else if (check_fields(btn, Button.ID_ESCAPE)){
				return;
			}
		}
	}
	
	/**
	 * Allows the user to manually control the robot.
	 * 
	 * Escape exits the program
	 * Up to move forward,
	 * Left/Right to rotate 90 degrees
	 * Enter opens the menu
	 * usSensorC.get
	 * @param motorA
	 * @param motorB
	 * @return
	 */
		
	public static void manual_control() {
		NXTMotor motorA = RobotInfo.getMotorA();
		NXTMotor motorB = RobotInfo.getMotorB();
		
		while (true) {
			System.out.println("Waiting for input...");
			int btn = Button.waitForAnyPress();
			
			if (check_fields(btn, Button.ID_LEFT))
				turnCounterClockwise(motorA, motorB);
			else if (check_fields(btn, Button.ID_RIGHT))
				turnClockwise(motorA, motorB);
			else if (check_fields(btn, Button.ID_UP))
				moveForward(motorA, motorB, 1);
			else if (check_fields(btn, Button.ID_ESCAPE))
				break;
			else if (check_fields(btn, Button.ID_ENTER))
				driveMenuOption(motorA, motorB);
			else
				System.out.println("Invalid input.");
		}

		motorA.close();
		motorB.close();
	}
	
	/***
	 * Displays the current selection. Use up and down to choose
	 * menu items and enter to select.
	 * @param motorA
	 * @param motorB
	 */
	public static void driveMenuOption(EncoderMotor motorA, EncoderMotor motorB) {
		int choice = 0;
		while (true) {
			switch (choice) {
			case 0:
				System.out.println("Drive Square?");
				break;
			case 1:
				System.out.println("Drive Circle?");
				break;
			case 2:
				System.out.println("Drive Figure 8?");
				break;
			case 3:
				System.out.println("Drive Straight?");
				break;
			}
			int btn = Button.waitForAnyPress();
			if (check_fields(btn, Button.ID_DOWN)){
				choice = (choice + 1) % 4;
			}
			else if (check_fields(btn, Button.ID_UP)){
				choice = (choice + 3) % 4;
			}
			else if (check_fields(btn, Button.ID_ENTER)) {
				System.out.println("Starting...");
				Delay.msDelay(200);
				switch (choice) {
				case 0:
					driveSquare(motorA, motorB);
					break;
				case 1:
					driveCircle(motorA, motorB);
					break;
				case 2:
					driveFigureEight(motorA, motorB);
					break;
				case 3:
					driveStraightLine(motorA, motorB);
					break;
				}
				return;
			} else if (check_fields(btn, Button.ID_ESCAPE)){
				return;
			}
		}
	}
	/***
	 * Drives the robot in a straight line
	 * @param motorA
	 * @param motorB
	 */
	public static void driveStraightLine(EncoderMotor motorA, EncoderMotor motorB) {
		moveForward(motorA,motorB, 3);
	}
	
	/***
	 * Drives the robot in a square pattern
	 * @param motorA
	 * @param motorB
	 */
	public static void driveSquare(EncoderMotor motorA, EncoderMotor motorB) {
		moveForward(motorA,motorB, 2f);
		turnClockwise(motorA,motorB);
		
		moveForward(motorA,motorB, 2f);
		turnClockwise(motorA,motorB);
		
		moveForward(motorA,motorB, 2f);
		turnClockwise(motorA,motorB);
		
		moveForward(motorA,motorB, 2f);
		turnClockwise(motorA,motorB);
	}
	
	/***
	 * Drives the robot in a circle
	 * @param motorA
	 * @param motorB
	 * @param rotations
	 */
	
	public static void driveCircle(EncoderMotor motorA, EncoderMotor motorB) {
		motorA.setPower(motorspeed);
		motorB.setPower(motorspeed);
		
		float prevA = motorA.getTachoCount();
		float prevB = motorB.getTachoCount();
		
		motorA.forward();
		motorB.flt();
		
		float rot_amt = 0f;
		
		while (rot_amt < 360f) {
			float currentA = motorA.getTachoCount();
			float currentB = motorB.getTachoCount();
			
			float speedA = currentA - prevA;
			float speedB = currentB - prevB;
			// convert wheel diameter to radius.
			// meanwheeldistance becomes the turn radius
			rot_amt += (speedA + speedB) * wheeldiameter / (2*meanwheeldistance);
			
			
			System.out.println("Distance = " + rot_amt);

			prevA = currentA;
			prevB = currentB;
		}
		
		motorA.stop();
		motorB.stop();
	}
	
	/***
	 * Drives the robot in a figure 8 shape (two circles)
	 * @param motorA
	 * @param motorB
	 * @param rotations
	 */
	public static void driveFigureEight(EncoderMotor motorA, EncoderMotor motorB) {
		driveCircle(motorA, motorB);
		driveCircle(motorB, motorA);
	}
	
	public static void moveForward(EncoderMotor motorA, EncoderMotor motorB, float rotations) {
		motorA.setPower(motorspeed);
		motorB.setPower(motorspeed);
		
		int startA = motorA.getTachoCount();
		int startB = motorB.getTachoCount();
		int prevAt = startA;
		int prevBt = motorB.getTachoCount();
		
		//US sensor used for error accumulation measure
		EV3UltrasonicSensor usSensorC = RobotInfo.getSensorC();
		SampleProvider distanceMode = usSensorC.getDistanceMode();
		float[] sample = new float[distanceMode.sampleSize()]; 
		distanceMode.fetchSample(sample, 0);
		float sensorReadingStart = sample[0];
		
		motorA.forward();
		motorB.forward();
		if (usSensorC.isEnabled() == false){ usSensorC.enable();}
		while (motorA.getTachoCount()- startA < 360*rotations){
			int tA = motorA.getTachoCount();
			int tB = motorB.getTachoCount();
			
			correctSpeed(motorA, motorB, tA-prevAt, tB-prevBt);
			Delay.msDelay(5);
			prevAt = tA;
			prevBt = tB;
		}
		motorA.stop();
		motorB.stop();
		Delay.msDelay(200);
		
		// for the error accumulation measure, report calculated distance moved
		distanceMode.fetchSample(sample, 0);
		float sensorNewReading = sample[0];
		float distSensor = Math.abs(sensorNewReading - sensorReadingStart);
		double distanceA = (motorA.getTachoCount()-startA)*Math.PI*wheeldiameter/360;
		double distanceB = (motorB.getTachoCount()-startB)*Math.PI*wheeldiameter/360;
		System.out.format("DistA = %.2fcm\nDistB = %.2fcm\n", distanceA/10, distanceB/10);
		System.out.format("Senstart= %.2fcm\nSendist: %.2fcm\n", sensorReadingStart*100, distSensor*100);
	}
	
	public static void turnCounterClockwise(EncoderMotor motorA, EncoderMotor motorB) {
//		turnCounterClockwise(motorA, motorB, 90);
		turnClockwise(motorB, motorA, 90);
	}
	
	public static void turnClockwise(EncoderMotor motorA, EncoderMotor motorB) {
		turnClockwise(motorA, motorB, 90);
	}
	
	/**
	 * Rotates by keeping track of how far around the circle it has
	 * rotated.
	 * @param motorA
	 * @param motorB
	 * @param degrees
	 */
	public static void turnClockwise(EncoderMotor motorA, EncoderMotor motorB, float degrees) {
		motorA.setPower(motorspeed);
		motorB.setPower(motorspeed);
		
		int prevA = motorA.getTachoCount();
		int prevB = motorB.getTachoCount();
		float travel = 0f;
		
		motorA.forward();
		motorB.backward();

		double distancePerTick = wheeldiameter*Math.PI/360;	// = 2pi*r / 360 = arcdistance
		double ticksPerRot = Math.PI*meanwheeldistance/distancePerTick;
		double radiansPerTick = (2*Math.PI)/ticksPerRot;
		
		while (Math.abs(travel - degrees) > 1.5f && travel < degrees) {
			int tA = motorA.getTachoCount();
			int tB = motorB.getTachoCount();
			
			float speedA = tA-prevA;
			float speedB = tB-prevB;

			travel += Math.abs(((speedB-speedA)/2)*(wheeldiameter/wheeltowheeldistance));
			
			System.out.println("Travel = " + travel%360);

			prevA = tA;
			prevB = tB;
		}
		motorA.stop();
		motorB.stop();
	}
	
	/**
	 * Adjusts the motor power so they are closer to rotating
	 * at the same speed.
	 * @param motorA
	 * @param motorB
	 * @param diffA The differential of the motor 
	 * @param diffB
	 */
	public static void correctSpeed(EncoderMotor motorA, EncoderMotor motorB, int diffA, int diffB){
		int difference = Math.abs(diffA)-Math.abs(diffB);
		int bSpeed = motorB.getPower();
		int aSpeed = motorA.getPower();
		
		if (difference > correction_threshold){
			if (bSpeed <= MAX_SPEED){
				motorB.setPower(bSpeed+1);
			}
			else{
				motorA.setPower(aSpeed-1);
			}
		}
		if (difference < -correction_threshold){
			if (aSpeed <= MAX_SPEED){
				motorA.setPower(aSpeed+1);
			}
			else{
				motorB.setPower(bSpeed-1);
			}
		}
		
		motorA.forward();
		motorB.forward();
	}
		
	public static boolean check_fields(int source, int flag) {
		return (source & flag) != 0;
	}
	
}