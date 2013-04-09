package cn.seu.dkpure;

/**
 * Route direction constants Defines
 * @author dkpure
 *
 */
public class RouteDirectionConstants {
	public final static int ROUTE_DIR_UNKNOWN = -1;
	
	public final static int ROUTE_DIR_GO_STRAIGHT = 0;
	
	public final static int ROUTE_DIR_TURN_LEFT0 = 1; //左前转
	public final static int ROUTE_DIR_TURN_LEFT1 = 2; //左转(90度转)
	public final static int ROUTE_DIR_TURN_LEFT2 = 3; //左后转
	public final static int ROUTE_DIR_TURN_LEFT3 = 4; //向左掉头
	
	public final static int ROUTE_DIR_TURN_RIGHT0 = 5; //右前转
	public final static int ROUTE_DIR_TURN_RIGHT1 = 6; //右转(90度转)
	public final static int ROUTE_DIR_TURN_RIGHT2 = 7; //右后转
	public final static int ROUTE_DIR_TURN_RIGHT3 = 8; //向右掉头
}
