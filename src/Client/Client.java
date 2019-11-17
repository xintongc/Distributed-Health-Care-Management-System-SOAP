package Client;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import Server.ServerInterface;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

public class Client extends Thread implements Runnable{
	static String clientID;
	static String patientID;
	static String oldAppointmentID;
	static String oldAppointmentType;
	static String newAppointmentID;
	static String newAppointmentType;

	ServerInterface MTLobj;
	ServerInterface QUEobj;
	ServerInterface SHEobj;


	static boolean repeat=true;
	public Client() {

		try{
			URL urlMtl = new URL("http://localhost:9999/hw?wsdl");
			QName mtlQName = new QName("http://Server/","MTLServerService");
			Service mtlService = Service.create(urlMtl, mtlQName);
			MTLobj = mtlService.getPort(ServerInterface.class);

			URL urlQue = new URL("http://localhost:8888/hw?wsdl");
			QName queQName = new QName("http://Server/","QUEServerService");
			Service queService = Service.create(urlQue, queQName);
			QUEobj = queService.getPort(ServerInterface.class);

			URL urlShe = new URL("http://localhost:7777/hw?wsdl");
			QName sheQName = new QName("http://Server/","SHEServerService");
			Service sheService = Service.create(urlShe, sheQName);
			SHEobj = sheService.getPort(ServerInterface.class);

		} catch (Exception e){
			e.printStackTrace();
		}

	}

	public static void main(String args[]) throws Exception
	{

		AdminClient admin=new AdminClient();
		PatientClient patient=new PatientClient();
		System.out.println("Admin ClientID:");
		admin.outputClientInfo();
		System.out.println("\nPatient ClientID:");
		patient.outputClientInfo();
		System.out.println("Please login with client ID");
		

		String clientID="";
		while(repeat) {
			
			System.out.println("Please login with clientID:");
			Scanner keyboard4=new Scanner(System.in);
			clientID=keyboard4.nextLine();
			char type=clientID.charAt(3);		
			if(type=='A') {
				admin.adminStart(clientID);
				System.out.println("Continue using DHMS? yes/no");
				Scanner keyboard5=new Scanner(System.in);
				String continue5=keyboard5.nextLine();
				if(continue5.equalsIgnoreCase("yes"))
					repeat=true;
				else {
					System.out.println("Thanks for using DHMS.");
					repeat=false;
				}
			}					
			else {
				patient.patientStart(clientID);
				System.out.println("Continue using DHMS? yes/no");
				Scanner keyboard5=new Scanner(System.in);
				String continue5=keyboard5.nextLine();
				if(continue5.equalsIgnoreCase("yes"))
					repeat=true;
				else {
					System.out.println("Thanks for using DHMS.");
					repeat=false;
				}
			}			
		}
	}
	public static String setAppointmentID()
	{		
		System.out.println("Please enter the appointmentID:");
		String appointmentID="";
		Scanner keyboard=new Scanner(System.in);
		appointmentID=keyboard.nextLine();
		return appointmentID;
	}
	public static String setAppointmentType()
	{
		System.out.println("Please enter the appointmentType:\n1. Physician;\n2. Surgeon;\n3. Dental");
		String appointmentType="";
		int input=0;
		Scanner keyboard=new Scanner(System.in);
		input=keyboard.nextInt();
		switch(input) {
			case 1: appointmentType="Physician";break;
			case 2: appointmentType="Surgeon";break;
			case 3: appointmentType="Dental";break;
		}
		return appointmentType;
	}
	public int setAppointmentWeek()
	{
		System.out.println("Please enter the appointment week");
		int input=0;
		Scanner keyboard=new Scanner(System.in);
		input=keyboard.nextInt();
		return input;
	}
	public int setCapacity()
	{
		System.out.println("Please enter the capacity you want(should be less than the maximum capacity:3)");
		int input=0;
		boolean doLoop=false;
		Scanner keyboard=new Scanner(System.in);
		do {			
			input=keyboard.nextInt();
			if(input>3) {
				System.out.println("Should be less than the maximum capacity:3. Please enter again.");
				doLoop=true;
			}else {
				doLoop=false;
			}
		}while(doLoop);
		
		return input;
	}
	public static String setPatientID()
	{
		System.out.println("Please enter the patientID:");
		String patientID="";
		Scanner keyboard=new Scanner(System.in);
		patientID=keyboard.nextLine();
		return patientID;
	}
	public static String setClientID()
	{
		System.out.println("Please enter the ClientID:");
		String clientID="";
		Scanner keyboard=new Scanner(System.in);
		clientID=keyboard.nextLine();
		return clientID;
	}
	
	public void printAppointmentByType(Map<String, Map<String,ArrayList<String>>> map, String type) {
		for(Map.Entry<String, Map<String,ArrayList<String>>> mtl:map.entrySet())
		{
			String appointmentType=mtl.getKey();
			if(appointmentType.equalsIgnoreCase(type)) {
//				System.out.print(appointmentType+"  ");
				for(Map.Entry<String, ArrayList<String>> nestedMap:mtl.getValue().entrySet())
				{
					System.out.print(appointmentType+ "    "+nestedMap.getKey()+" ");
					for(String op:nestedMap.getValue()) {
						System.out.print(op+"  ");
					}
					System.out.println("");
				}
				System.out.println("");
			}
			
		}
	}
	
	public void printAppointmentBySchedule(Map<String, Map<String,ArrayList<String>>> map,String clientID){
		for(Map.Entry<String, Map<String,ArrayList<String>>> mtl:map.entrySet())
		{
			String appointmentType=mtl.getKey();			
			for(Map.Entry<String, ArrayList<String>> nestedMap:mtl.getValue().entrySet())
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

	@Override
	public void run() {
		try {
			MTLobj.swapAppointment(clientID, patientID, oldAppointmentID, oldAppointmentType, newAppointmentID, newAppointmentType);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
