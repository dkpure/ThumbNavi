package cn.seu.dkpure;

import android.util.Log;

public class RouteParser {
	
	static String getDestination(String cline) {
		String retstr = null;
		
		if (cline.contains("����") || cline.contains("���ѵ�") || cline.contains("����")) {
			if (cline.contains("����")) {
				int index0 = cline.lastIndexOf("����");
//				int index1 = cline.lastIndexOf("-");
				
//				if (index1 > 0) {
//					retstr = cline.substring(index0 + 2, index1).trim();
//				} else {
//					retstr = cline.substring(index0 + 2);
//				}
				retstr = cline.substring(index0 + 2);
			} else if (cline.contains("���ѵ�")) {
				retstr = "�ѵ�";
			} else if (cline.contains("����")) {
				int lastindex = cline.lastIndexOf("����");
				retstr = cline.substring(lastindex + 2);
			} else {
				Log.v("RouteDirectionParser", "warning: can not parse destination!");
			}
		} else {
			if (cline.contains("����") || cline.contains("����") )
				retstr = cline;
			Log.v("RouteDirectionParser", "warning: can not parse destination!");
		}
		
		return retstr;
	}
	
	static String getDirection(String cline) {
		String retstr = null;
		
		if (cline.contains("����") || (cline.contains("��") && cline.contains("��")) || 
				cline.contains("���ѵ�")) {
			if (cline.contains("����")) {
				int index0 = cline.lastIndexOf("����");
				if (index0 != 0) {
					retstr = cline.substring(0, index0);
				}
			} else if (cline.contains("��") && cline.contains("��")) {
				int index0 = cline.indexOf("��");
				int index1 = cline.lastIndexOf("����");
				
				if (index1 > 0) {
					retstr = cline.substring(index0 + 1, index1);
				} else {
					retstr = cline.substring(index0 + 1);
				}
			} else if (cline.contains("���ѵ�")) {
				int index0 = cline.lastIndexOf("��");
				retstr = cline.substring(0, index0);
			} else {
				Log.v("RouteDirectionParser", "warning: can not parse destination!");
			}
		} else {
			if (!cline.contains("����")) {
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
		
		if (dir.equalsIgnoreCase("ֱ��")) {
			ret = RouteDirectionConstants.ROUTE_DIR_GO_STRAIGHT;
		} else if (dir.equalsIgnoreCase("��ǰ��ת��")
				|| dir.equalsIgnoreCase("������ת")
				|| dir.equalsIgnoreCase("����")
				) {
			ret = RouteDirectionConstants.ROUTE_DIR_TURN_LEFT0;
		} else if (dir.equalsIgnoreCase("��ǰ��ת��")
				|| dir.equalsIgnoreCase("������ת")
				|| dir.equalsIgnoreCase("����")
				) {
			ret = RouteDirectionConstants.ROUTE_DIR_TURN_RIGHT0;
		} else if (dir.equalsIgnoreCase("��ת")) {
			ret = RouteDirectionConstants.ROUTE_DIR_TURN_LEFT1;
		} else if (dir.equalsIgnoreCase("��ת")) {
			ret = RouteDirectionConstants.ROUTE_DIR_TURN_RIGHT1;
		} else if (dir.equalsIgnoreCase("�������ת��")) {
			ret = RouteDirectionConstants.ROUTE_DIR_TURN_LEFT2;
		} else if (dir.equalsIgnoreCase("�����Һ�ת��")) {
			ret = RouteDirectionConstants.ROUTE_DIR_TURN_RIGHT2;
		} else if (dir.equalsIgnoreCase("��ͷ")) {
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
			
			//get clean distance, for 20����ת, we just keep 20��
			if (retstr.contains("��")) {				
				int index = retstr.lastIndexOf("��");
				retstr = retstr.substring(0, index + 1).trim();
			} else if (retstr.contains("����")) {
				int index = retstr.lastIndexOf("����");
				retstr = retstr.substring(0, index + 2).trim();
			} else if (retstr.contains("ǧ��")) {
				int index = retstr.lastIndexOf("ǧ��");
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
			
			//get clean distance, for 20����ת, we just keep 20��
			if (retstr.contains("��")) {				
				int index = retstr.lastIndexOf("��");
				retstr = retstr.substring(0, index).trim();
				tmp = Float.parseFloat(retstr);
				ret = (int) tmp;
			} else if (retstr.contains("����")) {
				int index = retstr.lastIndexOf("����");
				retstr = retstr.substring(0, index).trim();
				tmp = Float.parseFloat(retstr);
				ret = (int) (tmp * 1000.0f);
			} else if (retstr.contains("ǧ��")) {
				int index = retstr.lastIndexOf("ǧ��");
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