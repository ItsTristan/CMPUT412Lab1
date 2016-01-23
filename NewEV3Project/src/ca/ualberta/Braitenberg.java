package ca.ualberta;

import lejos.hardware.Button;
import lejos.hardware.sensor.NXTLightSensor;

public class Braitenberg {
	
	public static void main() {
		SaferMotor motorA = RobotInfo.getMotorA();
		SaferMotor motorB = RobotInfo.getMotorB();

		NXTLightSensor sensorA = RobotInfo.getSensorA();
		NXTLightSensor sensorB = RobotInfo.getSensorB();
		
		float[] sampleA = new float[sensorA.sampleSize()];
		float[] sampleB = new float[sensorB.sampleSize()];
		
		// If sample sizes are different... ????
		assert sensorA.sampleSize() == sensorB.sampleSize();
		
		System.out.println("Press any button to calibrate");
		Button.waitForAnyPress();
		
		// Calibrate
		
		while (true) {
			// Read the sensor values
			sensorA.getAmbientMode().fetchSample(sampleA, 0);
			sensorB.getAmbientMode().fetchSample(sampleB, 0);
			
			// Behave according to which sensor is larger
			for (int i=0; i<sensorA.sampleSize(); i++) {
				System.out.println("SensorA[" + i + "] = " + sampleA[i]);
				System.out.println("SensorB[" + i + "] = " + sampleB[i]);
			}
			
			break;
		}
		
	}
}
