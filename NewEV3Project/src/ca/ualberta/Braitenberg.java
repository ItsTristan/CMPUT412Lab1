package ca.ualberta;

import lejos.hardware.Button;
import lejos.hardware.motor.NXTMotor;
import lejos.hardware.sensor.NXTLightSensor;
import lejos.utility.Delay;

public class Braitenberg {
	static float loLevelA = 1.0f;	// Low light level calibration
	static float loLevelB = 1.0f;
	
	static float hiLevelA = 0.0f;	// High light level calibration
	static float hiLevelB = 0.0f;
	
	static final int MIN_POWER = 0;
	static final int MAX_POWER = 100;
	
	
	public static void main() {
		int mode;
		
		NXTMotor motorA = RobotInfo.getMotorA();
		NXTMotor motorB = RobotInfo.getMotorB();

		NXTLightSensor sensorA = RobotInfo.getSensorA();
		NXTLightSensor sensorB = RobotInfo.getSensorB();
		
		System.out.println("Select a behaviour using (up/down) and enter:");
		
		mode = selectMode();

		/*
		// Calibrate
		System.out.println("Please place the robot near the light source.");
		System.out.println("Press any button to calibrate");
		Button.waitForAnyPress();
		hiLevelA = getSensorValue(sensorA);
		hiLevelB = getSensorValue(sensorB);
		*/
		
		System.out.println("Please place the robot in a dark area.");
		System.out.println("Press any button to calibrate");
		Button.waitForAnyPress();
		loLevelA = getSensorValue(sensorA);
		loLevelB = getSensorValue(sensorB);

		System.out.println("Press any button to start");
		Button.waitForAnyPress();
		Delay.msDelay(500);

		long lastPrint = System.currentTimeMillis();
		
		while (true) {

			// Kill the program if you press any button
			if (Button.getButtons() != 0) {
				break;
			}
			
			// Get sensor readings
			float readingA = getSensorValue(sensorA);
			float readingB = getSensorValue(sensorB);

			// On-the-fly adjustments for low and high level
			loLevelA = Math.min(readingA, loLevelA);
			loLevelB = Math.min(readingB, loLevelB);
			hiLevelA = Math.max(readingA, hiLevelA);
			hiLevelB = Math.max(readingB, hiLevelB);

			if (System.currentTimeMillis() - lastPrint > 1500) {
				System.out.format("A = %.2f\nB = %.2f\n\n", readingA, readingB);
				lastPrint = System.currentTimeMillis();
			}
			
			// Behaviour
			switch (mode) {
			case 0:
				cowardMode(motorA, motorB, readingA, readingB);
				break;
			case 1:
				aggressiveMode(motorA, motorB, readingA, readingB);
				break;
			case 2:
				loveMode(motorA, motorB, readingA, readingB);
				break;
			case 3:
				exploreMode(motorA, motorB, readingA, readingB);
				break;
			}
		}

		motorA.stop();
		motorB.stop();
	}

	public static float getSensorValue(NXTLightSensor sensor) {
		float[] sample = new float[sensor.sampleSize()];
		sensor.getAmbientMode().fetchSample(sample, 0);
		return mean(sample);
	}
	
	/**
	 * Computes the mean of the given data.
	 * @param data
	 * @return
	 */
	public static float mean(float[] data) {
		float result = 0f;
		float n = 0;
		for (float f : data) {
			result += (f-result) / (++n);
		}
		return result;
	}
	
	/**
	 * Chases the light but moves so that it sees less of it.
	 * @param leftMotor
	 * @param rightMotor
	 * @param leftSignal
	 * @param rightSignal
	 */
	public static void cowardMode(NXTMotor leftMotor, NXTMotor rightMotor, float leftSignal, float rightSignal) {
		int valueA = (int)(((leftSignal - loLevelA) / (hiLevelA - loLevelA))*(MAX_POWER-MIN_POWER)+MIN_POWER);
		int valueB = (int)(((rightSignal - loLevelB) / (hiLevelB - loLevelB))*(MAX_POWER-MIN_POWER)+MIN_POWER);
		
		leftMotor.setPower(valueA);
		rightMotor.setPower(valueB);
	}
	
	/**
	 * Chases the light and turns to see more of it
	 * @param leftMotor
	 * @param rightMotor
	 * @param leftSignal
	 * @param rightSignal
	 */
	public static void aggressiveMode(NXTMotor leftMotor, NXTMotor rightMotor, float leftSignal, float rightSignal) {
		int valueA = (int)(((leftSignal - loLevelA) / (hiLevelA - loLevelA))*(MAX_POWER-MIN_POWER)+MIN_POWER);
		int valueB = (int)(((rightSignal - loLevelB) / (hiLevelB - loLevelB))*(MAX_POWER-MIN_POWER)+MIN_POWER);
		
		leftMotor.setPower(valueB);
		rightMotor.setPower(valueA);
	}
	
	/**
	 * Moves towards the light but slows down the closer to the light source
	 * it gets.
	 * @param leftMotor
	 * @param rightMotor
	 * @param leftSignal
	 * @param rightSignal
	 */
	public static void loveMode(NXTMotor leftMotor, NXTMotor rightMotor, float leftSignal, float rightSignal) {
		int valueA = (int)(((leftSignal - loLevelA) / (hiLevelA - loLevelA))*(MAX_POWER-MIN_POWER)+MIN_POWER);
		int valueB = (int)(((rightSignal - loLevelB) / (hiLevelB - loLevelB))*(MAX_POWER-MIN_POWER)+MIN_POWER);
		
		leftMotor.setPower(MAX_POWER - valueA);
		rightMotor.setPower(MAX_POWER - valueB);
		
	}
	
	public static void exploreMode(NXTMotor leftMotor, NXTMotor rightMotor, float leftSignal, float rightSignal) {
		int valueA = (int)(((leftSignal - loLevelA) / (hiLevelA - loLevelA))*(MAX_POWER-MIN_POWER)+MIN_POWER);
		int valueB = (int)(((rightSignal - loLevelB) / (hiLevelB - loLevelB))*(MAX_POWER-MIN_POWER)+MIN_POWER);
		leftMotor.setPower(MAX_POWER - valueB);
		rightMotor.setPower(MAX_POWER - valueA);
	}
	
	public static int selectMode() {
		int choice = 0;
		while (true) {
			switch (choice) {
			case 0:
				System.out.println("Coward");
				break;
			case 1:
				System.out.println("Aggressive");
				break;
			case 2:
				System.out.println("Love");
				break;
			case 3:
				System.out.println("Explore");
				break;
			}
			int btn = Button.waitForAnyPress();
			if (Main.check_fields(btn, Button.ID_DOWN)){
				choice = (choice + 1) % 4;
			}
			else if (Main.check_fields(btn, Button.ID_UP)){
				choice = (choice + 3) % 4;
			}
			else if (Main.check_fields(btn, Button.ID_ENTER)) {
				return choice;
			}
		}
	}
}
