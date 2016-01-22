package ca.ualberta;

import lejos.hardware.Button;
import lejos.hardware.sensor.NXTLightSensor;

public class Braitenberg {
	
	public static void main() {
		SaferMotor motorA = RobotInfo.getMotorA();
		SaferMotor motorB = RobotInfo.getMotorB();

		NXTLightSensor sensorA = RobotInfo.getSensorA();
		NXTLightSensor sensorB = RobotInfo.getSensorB();
		
		System.out.println("Press any button to calibrate");
		Button.waitForAnyPress();
		
		// Calibrate
		
		while (true) {
			// Read the sensor values
			
			// Behave according to which sensor is larger
			
		}
		
	}
}
