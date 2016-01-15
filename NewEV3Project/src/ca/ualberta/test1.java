package ca.ualberta;

/*
*Version 0.8.1-beta
*/

import lejos.hardware.Button;
import lejos.hardware.motor.NXTMotor;
import lejos.hardware.port.MotorPort;
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

	public static void main(String[] args) {

		EncoderMotor motorA =new NXTMotor (MotorPort.A);
		EncoderMotor motorB =new NXTMotor (MotorPort.B);
		
		motorA.resetTachoCount();
		motorB.resetTachoCount();
		
		if (manual_control(motorA, motorB)) {
			return;
		}

		driveSquare(motorA, motorB);
		
		//motorA.resetTachoCount();
		//System.out.println("Motor A tachometer:" + motorA.getTachoCount());
		
		System.out.println("Motor A t:" + motorA.getTachoCount());

		motorA.resetTachoCount();
		System.out.println("Motor A t: " + motorA.getTachoCount());
		Delay.msDelay(2000);
		Button.waitForAnyPress();
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
	public static boolean manual_control(EncoderMotor motorA, EncoderMotor motorB) {
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
				return true;
			else if (check_fields(btn, Button.ID_ENTER))
				driveMenuOption(motorA, motorB);
			else
				System.out.println("Invalid input.");
		}
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
		
		float startA = motorA.getTachoCount();
		float startB = motorB.getTachoCount();
		
		motorA.forward();
		motorB.flt();
		
		float rot_amt = 0f;
		
		while (rot_amt < 360f) {
			float tA = motorA.getTachoCount();
			float tB = motorB.getTachoCount();
			
			// convert wheel diameter to radius.
			// meanwheeldistance becomes the turn radius
			rot_amt += ((tA - startA) + (tB - startB)) * wheeldiameter / (2*meanwheeldistance);
			
			System.out.println("Distance = " + rot_amt);

			startA = tA;
			startB = tB;
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
		int current_tach = motorA.getTachoCount();
		motorA.forward();
		motorB.forward();
		while (motorA.getTachoCount()- current_tach < 360*rotations)
			Delay.msDelay(5);
		motorA.stop();
		motorB.stop();
	}
	
	public static void turnCounterClockwise(EncoderMotor motorA, EncoderMotor motorB) {
		turnCounterClockwise(motorA, motorB, 90);
	}
	
	public static void turnCounterClockwise(EncoderMotor motorA, EncoderMotor motorB, int degrees) {
		// CounterClockwise and Clockwise can be factored together more nicely,
		// but pay attention to the signs on the heading counter.
		motorA.setPower(motorspeed);
		motorB.setPower(motorspeed);
		int startA = motorA.getTachoCount();
		int startB = motorB.getTachoCount();
		float travel = 0f;
		
		motorA.backward();
		motorB.forward();
		
		while (travel < degrees) {
			int tA = motorA.getTachoCount();
			int tB = motorB.getTachoCount();

			heading -= ((tB - startB) - (tA - startA)) / 4;
			System.out.println("Heading = " + heading);

			travel += (-(tA - startA) + (tB - startB)) / 4;

			startA = tA;
			startB = tB;
		}
		motorA.stop();
		motorB.stop();
	}
	
	public static void turnClockwise(EncoderMotor motorA, EncoderMotor motorB) {
		turnClockwise(motorA, motorB, 90);
	}
	
	public static void turnClockwise(EncoderMotor motorA, EncoderMotor motorB, float degrees) {
		// Rotates by keeping track of how far around the circle it has
		// rotated. To get how much each wheel needs to rotate,
		// just multiply the desired rotation amount by the ratio
		// between the turning radius and the wheel radius.
		motorA.setPower(motorspeed);
		motorB.setPower(motorspeed);
		int startA = motorA.getTachoCount();
		int startB = motorB.getTachoCount();
		float travel = 0f;
		
		motorA.forward();
		motorB.backward();
		
		while (travel < degrees) {
			int tA = motorA.getTachoCount();
			int tB = motorB.getTachoCount();

			heading += ((tA - startA) - (tB - startB)) / 4;
			System.out.println("Heading = " + heading);

			travel += ((tA - startA) - (tB - startB)) / 4;

			startA = tA;
			startB = tB;
		}
		motorA.stop();
		motorB.stop();
	}
	
		
	public static boolean check_fields(int source, int flag) {
		return (source & flag) != 0;
	}

}