package ca.ualberta;

/*
*Version 0.8.1-beta
*/

import lejos.hardware.Button;
import lejos.robotics.EncoderMotor;
import lejos.utility.Delay;

public class test1 {

	public static final int motorspeed = 80;
	public static final int finespeed = 10;

	public static final int max_loops = 100;

	public static final float wheeldiameter = 56;
	public static final float wheelwidth = 28;
	public static final float meanwheeldistance = 128f;
	public static final float minwheeldistance = 100f;
	public static final float maxwheeldistance = 156f;
	
	public static float heading = 0;
	
	/**
	 * Mode:
	 * -1 = Tests
	 *  0 = Manual Control
	 *  1 = Dead Reckoning
	 *  2 = Braitenberg
	 *  3 = Teleoperation
	 */
	public static int mode = 3; // change this sometime

	public static void main(String[] args) {
		
		switch(mode) {
		case -1:
			runAssertions();
			break;
		case 0:
			manual_control();
			break;
		case 1:
			deadReckoning.main();
			break;
		case 2:
			Braitenberg.main();
			break;
		case 3:
//			EV3tr.main();
			break;
		}
		
	}
	
	/**
	 * Allows the user to manually control the robot.
	 * 
	 * Escape exits the program
	 * Up to move forward,
	 * Left/Right to rotate 90 degrees
	 * Enter opens the menu
	 * 
	 * @param motorA
	 * @param motorB
	 * @return
	 */
	public static void manual_control() {
		SaferMotor motorA = RobotInfo.getMotorA();
		SaferMotor motorB = RobotInfo.getMotorB();
		
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
		moveForward(motorA,motorB, 2);
		turnClockwise(motorA,motorB);
		
		moveForward(motorA,motorB, 3);
		turnClockwise(motorA,motorB);
		
		moveForward(motorA,motorB, 2);
		turnClockwise(motorA,motorB);
		
		moveForward(motorA,motorB, 3);
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
	
	/// section: motor controls
	
	public static void moveForward(EncoderMotor motorA, EncoderMotor motorB, float rotations) {
		motorA.setPower(motorspeed);
		motorB.setPower(motorspeed);
		
		int startA = motorA.getTachoCount();
		
		int prevAt = startA;
		int prevBt = motorB.getTachoCount();
		
		motorA.forward();
		motorB.forward();
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
	}
	
	public static void turnCounterClockwise(EncoderMotor motorA, EncoderMotor motorB) {
//		turnCounterClockwise(motorA, motorB, 90);
		turnClockwise(motorB, motorA, 90);
	}
	
//	public static void turnCounterClockwise(EncoderMotor motorA, EncoderMotor motorB, int degrees) {
//		// CounterClockwise and Clockwise can be factored together more nicely,
//		// but pay attention to the signs on the heading counter.
//		motorA.setPower(motorspeed);
//		motorB.setPower(motorspeed);
//		
//		int prevA = motorA.getTachoCount();
//		int prevB = motorB.getTachoCount();
//		
//		float travel = 0f;
//		
//		motorA.backward();
//		motorB.forward();
//		
//		while (travel < degrees) {
//			int tA = motorA.getTachoCount();
//			int tB = motorB.getTachoCount();
//
//			heading += ((tA - prevA) - (tB - prevB) ) / 4;
//
//			travel -= ((tA - prevA) - (tB - prevB)) / 4;
//
//			prevA = tA;
//			prevB = tB;
//		}
//		
//		System.out.println("Heading = " + heading);
//		
//		motorA.stop();
//		motorB.stop();
//	}
	
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
		
		while (travel < degrees) {
			int tA = motorA.getTachoCount();
			int tB = motorB.getTachoCount();
			
			heading += ((tA - prevA) - (tB - prevB)) / 4;
			System.out.println("Heading = " + heading);

			travel += ((tA - prevA) - (tB - prevB)) / 4;

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
		
		if (difference > 5){
			if (bSpeed <= 95){
				motorB.setPower(bSpeed+1);
			}
			else{
				motorA.setPower(aSpeed-1);
			}
		}
		if (difference < -5){
			if (aSpeed <= 95){
				motorA.setPower(aSpeed+1);
			}
			else{
				motorB.setPower(bSpeed-1);
			}
		}
	}
		
	public static boolean check_fields(int source, int flag) {
		return (source & flag) != 0;
	}
	
	
	public static void runAssertions() {
		System.out.println("Running tests...");
		
		SaferMotor motorA = RobotInfo.getMotorA();
		SaferMotor motorB = RobotInfo.getMotorB(); 
		
		motorA.resetTachoCount();
		motorB.resetTachoCount();

		int prevA = motorA.getRealTachoCount();
		int prevB = motorB.getRealTachoCount();
		
		motorA.setPower(80);
		motorB.setPower(60);
		
		motorA.forward();
		motorB.forward();

		System.out.println("Start tacho A = " + prevA);
		System.out.println("Start tacho B = " + prevB);

		Delay.msDelay(1000);
		
		System.out.println("Mid tacho A = " + motorA.getRealTachoCount());
		System.out.println("Mid tacho B = " + motorB.getRealTachoCount());
		
		System.out.println("Speed A = " + (motorA.getRealTachoCount() - prevA));
		System.out.println("Speed B = " + (motorB.getRealTachoCount() - prevB));

		System.out.println("AutoSpeed A = " + motorA.getTachoDiff());
		System.out.println("AutoSpeed B = " + motorB.getTachoDiff());

		prevA = motorA.getRealTachoCount();
		prevB = motorB.getRealTachoCount();
		
		Delay.msDelay(1000);
		
		motorA.stop();
		motorB.stop();
		
		System.out.println("End tacho A = " + motorA.getRealTachoCount());
		System.out.println("End tacho B = " + motorB.getRealTachoCount());
		
		System.out.println("Speed A = " + (motorA.getRealTachoCount() - prevA));
		System.out.println("Speed B = " + (motorB.getRealTachoCount() - prevB));

		System.out.println("AutoSpeed A = " + motorA.getTachoDiff());
		System.out.println("AutoSpeed B = " + motorB.getTachoDiff());
		
	}
	
}