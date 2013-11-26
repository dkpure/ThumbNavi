package cn.seu.dkpure;

import android.util.Log;

public class RouteParser {
	
	static String getDestination(String cline) {
		String retstr = null;
		
		if (cline.contains("进入") || cline.contains("上匝道") || cline.contains("到达")) {
			if (cline.contains("进入")) {
				int index0 = cline.lastIndexOf("进入");
//				int index1 = cline.lastIndexOf("-");
				
//				if (index1 > 0) {
//					retstr = cline.substring(index0 + 2, index1).trim();
//				} else {
//					retstr = cline.substring(index0 + 2);
//				}
				retstr = cline.substring(index0 + 2);
			} else if (cline.contains("上匝道")) {
				retstr = "匝道";
			} else if (cline.contains("到达")) {
				int lastindex = cline.lastIndexOf("到达");
				retstr = cline.substring(lastindex + 2);
			} else {
				Log.v("RouteDirectionParser", "warning: can not parse destination!");
			}
		} else {
			if (cline.contains("靠左") || cline.contains("靠右") )
				retstr = cline;
			Log.v("RouteDirectionParser", "warning: can not parse destination!");
		}
		
		return retstr;
	}
	
	static String getDirection(String cline) {
		String retstr = null;
		
		if (cline.contains("进入") || (cline.contains("从") && cline.contains("向")) || 
				cline.contains("上匝道")) {
			if (cline.contains("进入")) {
				int index0 = cline.lastIndexOf("进入");
				if (index0 != 0) {
					retstr = cline.substring(0, index0);
				}
			} else if (cline.contains("从") && cline.contains("向")) {
				int index0 = cline.indexOf("向");
				int index1 = cline.lastIndexOf("出发");
				
				if (index1 > 0) {
					retstr = cline.substring(index0 + 1, index1);
				} else {
					retstr = cline.substring(index0 + 1);
				}
			} else if (cline.contains("上匝道")) {
				int index0 = cline.lastIndexOf("上");
				retstr = cline.substring(0, index0);
			} else {
				Log.v("RouteDirectionParser", "warning: can not parse destination!");
			}
		} else {
			if (!cline.contains("到达")) {
				int index0 = cline.lastIndexOf("-");
				
				if (index0 > 0) {
					retstr = cline.substring(0, index0).trim();
				} else {
					retstr = cline;
				}
			}
		}
		
		return retstr;
	}
	
	static int parseDirectionIndex(String dir) {
		if (null == dir || dir.equals("")) {
			return RouteDirectionConstants.ROUTE_DIR_UNKNOWN;
		}
		
		int ret = RouteDirectionConstants.ROUTE_DIR_UNKNOWN;;
		dir = dir.trim();//trim dir string
		
		if (dir.equalsIgnoreCase("直行")) {
			ret = RouteDirectionConstants.ROUTE_DIR_GO_STRAIGHT;
		} else if (dir.equalsIgnoreCase("左前方转弯")
				|| dir.equalsIgnoreCase("稍向左转")
				|| dir.equalsIgnoreCase("靠左")
				) {
			ret = RouteDirectionConstants.ROUTE_DIR_TURN_LEFT0;
		} else if (dir.equalsIgnoreCase("右前方转弯")
				|| dir.equalsIgnoreCase("稍向右转")
				|| dir.equalsIgnoreCase("靠右")
				) {
			ret = RouteDirectionConstants.ROUTE_DIR_TURN_RIGHT0;
		} else if (dir.equalsIgnoreCase("左转")) {
			ret = RouteDirectionConstants.ROUTE_DIR_TURN_LEFT1;
		} else if (dir.equalsIgnoreCase("右转")) {
			ret = RouteDirectionConstants.ROUTE_DIR_TURN_RIGHT1;
		} else if (dir.equalsIgnoreCase("稍向左后转弯")) {
			ret = RouteDirectionConstants.ROUTE_DIR_TURN_LEFT2;
		} else if (dir.equalsIgnoreCase("稍向右后转弯")) {
			ret = RouteDirectionConstants.ROUTE_DIR_TURN_RIGHT2;
		} else if (dir.equalsIgnoreCase("调头")) {
			ret = RouteDirectionConstants.ROUTE_DIR_TURN_LEFT3;
		} else {
			ret = RouteDirectionConstants.ROUTE_DIR_UNKNOWN;
			Log.v("RouteDirectionParser", "warning: can not parse destination!");
		}
		
		return ret;		
	}
	
	static int getDirIndexFromContent(String cline) {
		
		String dirstr = getDirection(cline);
		
		if (null == dirstr || dirstr.equals("")) {
//			return RouteDirectionConstants.ROUTE_DIR_UNKNOWN;
			return RouteDirectionConstants.ROUTE_DIR_GO_STRAIGHT;
		} else {
			return parseDirectionIndex(dirstr);
		}
	}
	
	static int getDirIndexFromDir(String dir) {
		if (null == dir || dir.equals("")) {
//			return RouteDirectionConstants.ROUTE_DIR_UNKNOWN;
			return RouteDirectionConstants.ROUTE_DIR_GO_STRAIGHT;
		} else {
			return parseDirectionIndex(dir);
		}
	}
	
	static String getDistance(String cline) {
		String retstr = null;
		
		if (cline.contains("-")) {
			int lastindex = cline.lastIndexOf("-"); //look for "-" symbol
			retstr = cline.substring(lastindex + 2).trim();
			
			//get clean distance, for 20米右转, we just keep 20米
			if (retstr.contains("米")) {				
				int index = retstr.lastIndexOf("米");
				retstr = retstr.substring(0, index + 1).trim();
			} else if (retstr.contains("公里")) {
				int index = retstr.lastIndexOf("公里");
				retstr = retstr.substring(0, index + 2).trim();
			} else if (retstr.contains("千米")) {
				int index = retstr.lastIndexOf("千米");
				retstr = retstr.substring(0, index + 2).trim();
			}
		}
		
		return retstr;
	}
	
	static int getPureDistance(String cline) {
		int ret = -1;
		float tmp;
		
		if (cline.contains("-")) {
			int lastindex = cline.lastIndexOf("-"); //look for "-" symbol
			String retstr = cline.substring(lastindex + 2).trim();
			
			//get clean distance, for 20米右转, we just keep 20米
			if (retstr.contains("米")) {				
				int index = retstr.lastIndexOf("米");
				retstr = retstr.substring(0, index).trim();
				tmp = Float.parseFloat(retstr);
				ret = (int) tmp;
			} else if (retstr.contains("公里")) {
				int index = retstr.lastIndexOf("公里");
				retstr = retstr.substring(0, index).trim();
				tmp = Float.parseFloat(retstr);
				ret = (int) (tmp * 1000.0f);
			} else if (retstr.contains("千米")) {
				int index = retstr.lastIndexOf("千米");
				retstr = retstr.substring(0, index).trim();
				tmp = Float.parseFloat(retstr);
				ret = (int) (tmp * 1000.0f);
			}
		}
		
		return ret;
	}
	
	static String getRouteIndicationForTts(String cline) {
		String ret = "";
		
		if (cline != null && !cline.equals(""))
//			ret = cline.replace('-', ' ').trim();
			ret = cline.trim().replace("-", "").replace(" ", "");
		
		return ret;
	}
}