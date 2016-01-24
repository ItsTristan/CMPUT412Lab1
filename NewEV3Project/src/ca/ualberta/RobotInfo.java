package ca.ualberta;

import lejos.hardware.motor.NXTMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.NXTLightSensor;

public class RobotInfo {
	private static NXTMotor motorA;
	private static NXTMotor motorB;
	
	private static NXTLightSensor sensorA;
	private static NXTLightSensor sensorB;

	public static String MACaddress = "00:16:53:44:97:4F";
	public static String address = "10.0.1.1";
	
	/**
	 * Returns a singleton value for motor A.
	 * Prevents errors when initializing the motors.
	 * @return
	 */
	public static NXTMotor getMotorA() {
		if (motorA == null) {
			motorA = new NXTMotor(MotorPort.A);
		}
		return motorA;
	}

	/**
	 * Returns a singleton value for motor B
	 * Prevents errors when initializing the motors.
	 * @return
	 */
	public static NXTMotor getMotorB() {
		if (motorB == null) {
			motorB = new NXTMotor(MotorPort.B);
		}
		return motorB;
	}

	/**
	 * Returns a singleton value for light sensor corresponding to A 
	 * Prevents errors when initializing the motors.
	 * @return
	 */
	public static NXTLightSensor getSensorA() {
		if (sensorA == null) {
			sensorA = new NXTLightSensor(SensorPort.S1);
		}
		return sensorA;
	}

	/**
	 * Returns a singleton value for light sensor corresponding to B
	 * Prevents errors when initializing the motors.
	 * @return
	 */
	public static NXTLightSensor getSensorB() {
		if (sensorB == null) {
			sensorB = new NXTLightSensor(SensorPort.S2);
		}
		return sensorB;
	}
	
}
