package iw.ur.thymio.Thymio;

import iw.ur.thymio.Connection.ThymioConnection;

import java.util.ArrayList;
import java.util.List;





/**
 * <h1>Thymio Class for Course Informationssysteme (SS 2016) Universitaet Regensburg</h1>
 * 
 * <div>Dozent: Prof. Dr. Bernd Ludwig</div>
 * 
 * <div>Mitarbeiter: Aigner Johannes, Thiemo Kersting</div>
 * 
 * <div>For more Information contact one of these. </div>
 * 
 * @version 1.0
 * @author Aigner Johannes: johannes.aigner@stud.uni-regensburg.de
 */

public class Thymio {
	
	private final static int MAX_SPEED = 500;
	private final static double BASE_WIDTH = 95;     
	private final static double SPEED_COEF = 2.93;	
	private final static short SPEED_ROTATION = 50;
	private final static short SPEED_AHEAD = 100;
	
	private final static double DEFAULT_WHITE_FIELD = 900;
	private final static double DEFAULT_BLACK_FIELD = 250;
	private final static double FIELD_BIAS = 100;
	private final static long DRIVE_BIAS = 4000;
	private final static int MOVE_FIELD_BIAS = 350;
	
	private final static int WHITE_FIELD = 1;
	private final static int BLACK_FIELD = -1;
	private final static int CORRECT_TIME = 3;
	
	private short vleft;
	private short vright;
	
	private double whiteField = DEFAULT_WHITE_FIELD;
	private double blackField = DEFAULT_BLACK_FIELD;
	private double bias = FIELD_BIAS;
	private long driveBias = DRIVE_BIAS;

	private int currentField = 1;
	
	private double orientation;
	
	/**
	 * Set up Thymio Connection
	 */
	private ThymioConnection connection;
	
	
	/**
	 * Generates "new" Thymio with given IP 
	 * Inits Thymio Connection 
	 * 
	 * Throws Exception if your IP isn't available or your Raspberry isn't running!
	 * You have to init Thymio first of all before running any commands!
	 *
	 * @param ip: The IP of your Raspberry
	 */
	public Thymio(String ip){
		connection = new ThymioConnection(ip);
		orientation = 0;
	}
	
	/**
	 * This Method sets the Orientation. The Start value is zero degree. 
	 * You can change the Start Orientation by setting it right on the start.
	 * @param degree: Thymio Orientation
	 */
	public void setOrientation(double degree){
		orientation = degree;
	}
	
	
	/**
	 * This Method returns the current Orientation.
	 * 
	 * @return: Orientation in degree (double)
	 */
	public double getOrientation(){
		return orientation;
	}
	
	
	/**
	 * Method to change the Parameters for White or Black Field (depends on your Thymio)
	 * Default params: White = 900; Black = 250; Bias = 100
	 * 
	 * @param whiteField: Set the parameter to your value for a white field
	 * @param blackField: Set the parameter to your value for a black field
	 * @param bias: Set the parameter to your value for the bias
	 */
	public void setFieldSensitivity(double whiteField, double blackField, double bias){
		this.whiteField = whiteField;
		this.blackField = blackField;
		this.bias = bias;
	}
	
	/**
	 * Method to change the Bias for driving. If your Thymio doesn't reach the middle of the next field (Method move()) you have to increase this value,
	 * or decrease it otherwise. (Default value 3500)
	 * 
	 * @param driveBias: Default Value 3500. (Change the value if needed to 4000 or 4500 or to 3000 or 2500)
	 */
	public void setMoveSensitivity(long moveBias){
		this.driveBias = moveBias;
	}
	
	/**
	 * Method to set the Start Field: 1 for white and -1 for black
	 * 
	 * @param field: Default Value white!
	 */
	public void setStartField(int field){
		currentField = field;
	}
	
	/**
	 * Method for driving Thymio with given Speed (Left and Right)
	 * 
	 * @param motorLeft: Speed for Motor Left (-500 - +500)
	 * @param motorRight: Speed for Motor Right (-500 - + 500)
	 */
	public void drive(int motorLeft, int motorRight){
		List<Short> driveList = new ArrayList<Short>();
		driveList.add((short) motorLeft);
		driveList.add((short) motorRight);
		
		connection.setSpeed(driveList);
	}
	
	/**
	 * Method for driving to next Field 
	 * Thymio will drive one field and will probably correct itself. 
	 * 
	 * This Method tries to move exactly one field (straight!)
	 * 
	 */
	public void move(){
		
		boolean arrived = false;
		boolean hasCorrected = false;
		
		int startLeftGround = getGroundReflected()[0];
		int startRightGround = getGroundReflected()[1];
		int leftGround = 0;
		int rightGround = 0;
		int startField = onField();
		int correctCount = 0;

		
		while(!arrived){
			while(!hasCorrected){
				leftGround = getGroundReflected()[0];
				rightGround = getGroundReflected()[1];
				drive(50,50);
				if(startField != onField()){
					if(leftGround > startLeftGround + MOVE_FIELD_BIAS || leftGround < startLeftGround - MOVE_FIELD_BIAS
							|| rightGround > startRightGround + MOVE_FIELD_BIAS || rightGround < startRightGround - MOVE_FIELD_BIAS){
						correct(startField, leftGround, rightGround);
						correctCount++;
						System.out.println("Corrected: " + correctCount + "mal!");
						if(correctCount == CORRECT_TIME){
							hasCorrected = true;
						}
					}
					System.out.println("in !hascorrected");
				}
			}
			
			drive(100,100);
			
			try {
				Thread.sleep(driveBias);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stop();
			arrived = true;
		}
		
	}

	private void correct(int field, int left, int right){
		if(field == WHITE_FIELD){
			if(left > right + MOVE_FIELD_BIAS){
				int currLeft = getGroundReflected()[0];
				int currRight = getGroundReflected()[1];
				while(currLeft > currRight + MOVE_FIELD_BIAS){
					drive(0,-100);
					currLeft = getGroundReflected()[0];
					currRight = getGroundReflected()[1];
				}
				stop();
			}
			else if(right > left + MOVE_FIELD_BIAS){
				int currLeft = getGroundReflected()[0];
				int currRight = getGroundReflected()[1];
				while(currRight > currLeft + MOVE_FIELD_BIAS){
					drive(-100,0);
					currLeft = getGroundReflected()[0];
					currRight = getGroundReflected()[1];
				}
				stop();
			}else{
				stop();
			}
		}
		if(field == BLACK_FIELD){
			if(left > right + MOVE_FIELD_BIAS){
				int currLeft = getGroundReflected()[0];
				int currRight = getGroundReflected()[1];
				while(currLeft > currRight + MOVE_FIELD_BIAS){
					drive(-100,0);
					currLeft = getGroundReflected()[0];
					currRight = getGroundReflected()[1];
				}
				stop();
			}
			else if(right > left + MOVE_FIELD_BIAS){
				int currLeft = getGroundReflected()[0];
				int currRight = getGroundReflected()[1];
				while(currRight > currLeft + MOVE_FIELD_BIAS){
					drive(0,-100);
					currLeft = getGroundReflected()[0];
					currRight = getGroundReflected()[1];
				}
				stop();
			}else{
				stop();
			}
		}
	}
	
	/**
	 * Method for rotating Thymio with given degrees
	 * 
	 * @param degree: Degrees for Rotation (-360 - +360)
	 */
	public void rotate(double degree){
		double rad = degree * Math.PI/180 * -1;
		double dt;
		long milli;
		ArrayList<Short> dataleft = new ArrayList<Short>();
		ArrayList<Short> dataright = new ArrayList<Short>();

		if (rad < 0) {
			vleft = (short)SPEED_ROTATION;
			vright = (short)(-SPEED_ROTATION);
			
			dataleft.add(vleft);
			dataright.add(vright);
		}
		else {
			vleft = (short)(-SPEED_ROTATION);
			vright = (short)SPEED_ROTATION;
			
			dataleft.add(vleft);
			dataright.add(vright);			
		}
		
		dt = (BASE_WIDTH*SPEED_COEF)/(2*SPEED_ROTATION)*Math.abs(rad);
		milli = (long)(1000*dt);
		

		
		connection.setVariable("motor.left.target", dataleft);
		connection.setVariable("motor.right.target", dataright);
		
		try {
			Thread.sleep(milli);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		stop();
		drive(-75,-75);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stop();
		//t.start();
	}
	
	
	
	/**
	 * Method for stopping Thymio.
	 * 
	 * This Method stops the Thymio by setting speed to zero!
	 */
	public void stop(){
		ArrayList<Short> go = new ArrayList<Short>();

		vleft = vright = (short)0;
		go.add(vleft);
		
		connection.setVariable("motor.left.target", go);
		connection.setVariable("motor.right.target", go);
	}

	
	/**
	 * Method for getting the current field Color from Thymio
	 * 
	 * @return: Returns 1 for white Field and -1 for Black Field or 0 for none of these.
	 */
	public int onField(){
		int[] ground = getGroundReflected();
		if(ground[0] > (whiteField-bias) && ground[0] < (whiteField+bias) && ground[1] > (whiteField-bias) && ground[1] < (whiteField+bias)){
			return 1;
		}
		if(ground[0] > (blackField-bias) && ground[0] < (blackField+bias) && ground[1] > (blackField-bias) && ground[1] < (blackField+bias)){
			return -1;
		}
		return 0;
	}
	
	
	/**
	 * Method for getting the Sensor-Data from the Ground Sensors
	 * 
	 * @return: Returns Array with 2 values for both sensors  
	 */
	public int[] getGroundReflected(){
		List<Short> sensors = connection.getVariable("prox.ground.reflected");
		int[] ground = new int[2];
		for(int i = 0; i < 2; i++){
			ground[i] = sensors.get(i);
		}
		return ground;
	}
	
	
	/**
	 *  Method for getting the Sensor-Data from the Horizontal Sensors
	 *  
	 * @return: Returns Array with 7 values: 0 (left) - 4 (right) // 5 (back left) , 6 (back right) 
	 */
	public int[] getProxHorizontal(){
		List<Short> sensors = connection.getVariable("prox.horizontal");
		int[] horizontal = new int[7];
		for(int i = 0; i < 7; i++){
			horizontal[i] = sensors.get(i);
		}
		return horizontal;
	}

}
