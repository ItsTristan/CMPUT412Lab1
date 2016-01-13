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
	
	public static final int motorspeed = 50;

	public static void main(String[] args) {

		EncoderMotor motorA =new NXTMotor (MotorPort.A);
		EncoderMotor motorB =new NXTMotor (MotorPort.B);
		
		motorA.resetTachoCount();
		System.out.println("Motor A tachometer:" + motorA.getTachoCount());

		moveForward(motorA,motorB, 2);
		System.out.println("Move forward 2");
		System.out.flush();
		turnClockwise(motorA,motorB);
		System.out.println("Rotate 90 deg");
		System.out.flush();
		
		moveForward(motorA,motorB, 3);
		System.out.println("Move forward 3");
		System.out.flush();
		turnClockwise(motorA,motorB);
		System.out.println("Rotate 90 deg");
		System.out.flush();
		
		moveForward(motorA,motorB, 2);
		System.out.println("Move forward 2");
		System.out.flush();
		turnClockwise(motorA,motorB);
		System.out.println("Rotate 90 deg");
		System.out.flush();
		
		moveForward(motorA,motorB, 3);
		System.out.println("Move forward 3");
		System.out.flush();
		turnClockwise(motorA,motorB);
		System.out.println("Rotate 90 deg");
		System.out.flush();
		
		System.out.println("Motor A t:" + motorA.getTachoCount());

		motorA.resetTachoCount();
		//System.out.println(motorA.getTachoCount()-motorA.getTachoCount());
		Delay.msDelay(2000);
		Button.waitForAnyPress();
		
	}
	
	public static void moveForward(EncoderMotor motorA, EncoderMotor motorB, float rotations) {
		motorA.setPower(motorspeed);
		motorB.setPower(motorspeed);
		motorA.forward();
		motorB.forward();
		while (motorA.getTachoCount() < 360*rotations)
			Delay.msDelay(10);
		motorA.stop();
		motorB.stop();
	}
	
	public static void turnCounterClockwise(EncoderMotor motorA, EncoderMotor motorB) {
		turnClockwise(motorB, motorA, 90);
	}
	public static void turnCounterClockwise(EncoderMotor motorA, EncoderMotor motorB, int degrees) {
		turnClockwise(motorB, motorA, degrees);
	}
	public static void turnClockwise(EncoderMotor motorA, EncoderMotor motorB) {
		turnClockwise(motorA, motorB, 90);
	}
	public static void turnClockwise(EncoderMotor motorA, EncoderMotor motorB, int degree) {
		motorA.setPower(motorspeed);
		motorB.setPower(motorspeed);
		motorA.forward();
		motorB.backward();
		while (motorA.getTachoCount() < 2*degree)
			Delay.msDelay(10);
		motorA.stop();
		motorB.stop();
	}

}