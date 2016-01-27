package ca.ualberta;

import lejos.hardware.motor.NXTMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.NXTLightSensor;

public class RobotInfo {
	private static NXTMotor motorA;
	private static NXTMotor motorB;
	
	private static NXTLightSensor lightSensorA;
	private static NXTLightSensor lightSensorB;
	private static EV3UltrasonicSensor usSensorC;
	private static EV3GyroSensor gySensorD;

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
		if (lightSensorA == null) {
			lightSensorA = new NXTLightSensor(SensorPort.S1);
		}
		return lightSensorA;
	}

	/**
	 * Returns a singleton value for light sensor corresponding to B
	 * Prevents errors when initializing the motors.
	 * @return
	 */
	public static NXTLightSensor getSensorB() {
		if (lightSensorB == null) {
			lightSensorB = new NXTLightSensor(SensorPort.S2);
		}
		return lightSensorB;
	}
	
	/**
	 * Returns a singleton value for ultrasonic sensor corresponding to C
	 * Prevents errors when initializing the motors.
	 * @return
	 */
	public static EV3UltrasonicSensor getSensorC() {
		if (usSensorC == null) {
			usSensorC = new EV3UltrasonicSensor(SensorPort.S3);
		}
		return usSensorC;
	}
	
	public static EV3GyroSensor getSensorD() {
		if (gySensorD == null) {
			gySensorD = new EV3GyroSensor(SensorPort.S4);
		}
		return gySensorD;
	}
}
