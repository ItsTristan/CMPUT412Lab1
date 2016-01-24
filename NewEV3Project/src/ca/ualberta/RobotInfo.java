package ca.ualberta;

import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.NXTLightSensor;

public class RobotInfo {
	private static SaferMotor motorA;
	private static SaferMotor motorB;
	
	private static NXTLightSensor sensorA;
	private static NXTLightSensor sensorB;

	/**
	 * Returns a singleton value for motor A.
	 * Prevents errors when initializing the motors.
	 * @return
	 */
	public static SaferMotor getMotorA() {
		if (motorA == null) {
			motorA = new SaferMotor(MotorPort.A);
		}
		return motorA;
	}

	/**
	 * Returns a singleton value for motor B
	 * Prevents errors when initializing the motors.
	 * @return
	 */
	public static SaferMotor getMotorB() {
		if (motorB == null) {
			motorB = new SaferMotor(MotorPort.B);
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
