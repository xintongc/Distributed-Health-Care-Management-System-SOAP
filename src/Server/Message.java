package Server;

import rm.StdMaps;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Message implements Serializable {
	Map<String, Map<String,ArrayList<String>>> map = new HashMap<String, Map<String,ArrayList<String>>>();
	private String msg="";
	
	public Message(String msg) {
		this.msg=msg;
	}
	public Message(Map<String, Map<String,ArrayList<String>>> map) {
		this.map =map;
	}
	public Map<String, Map<String,ArrayList<String>>> getMap(){
		return map;
	}
	public void setMap(Map<String, Map<String,ArrayList<String>>> map){
		this.map =map;
	}
	public void setMessage(Map<String, Map<String,ArrayList<String>>> map) {
		this.map =map;
	}
	public String getMsg() {
		return msg;
	}

}
