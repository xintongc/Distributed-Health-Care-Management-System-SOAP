package Server;

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

	public void printAppointment()
	{
		for(Map.Entry<String, Map<String,ArrayList<String>>> mapEntry:map.entrySet())
		{
			String appointmentType=mapEntry.getKey();
			System.out.println(appointmentType);
			for(Map.Entry<String, ArrayList<String>> nestedMap:mapEntry.getValue().entrySet())
			{
				System.out.print("    "+nestedMap.getKey()+" ");
				for(String op:nestedMap.getValue()) {
					System.out.print(op+"  ");
				}
				System.out.println("");
			}
		}
	}

	public void printAppointmentByType(String type) {
		for(Map.Entry<String, Map<String,ArrayList<String>>> mapEntry:map.entrySet())
		{
			String appointmentType=mapEntry.getKey();
			if(appointmentType.equalsIgnoreCase(type)) {
				System.out.println(appointmentType+"  ");
				for(Map.Entry<String, ArrayList<String>> nestedMap:mapEntry.getValue().entrySet())
				{
					System.out.print("    "+nestedMap.getKey()+" ");
					for(String op:nestedMap.getValue()) {
						System.out.print(op+"  ");
					}
					System.out.println("");
				}
				System.out.println("");
			}

		}
	}

	public void printAppointmentBySchedule(String clientID){
		for(Map.Entry<String, Map<String,ArrayList<String>>> mapEntry:map.entrySet())
		{
			String appointmentType=mapEntry.getKey();
			for(Map.Entry<String, ArrayList<String>> nestedMap:mapEntry.getValue().entrySet())
			{
				for(int i=0;i<nestedMap.getValue().size();i++)
				{
					if(nestedMap.getValue().get(i).equalsIgnoreCase(clientID))
						System.out.println(appointmentType+" "+nestedMap.getKey()+"  "+nestedMap.getValue().get(i));
				}
			}
			System.out.println("");
		}
	}

}
