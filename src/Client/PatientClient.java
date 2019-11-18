package Client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class PatientClient extends Client implements Runnable{
	Map<Integer, String> patientMap = new HashMap<>();
	public PatientClient()
	{
		patientMap.put(1,"MTLP1");
		patientMap.put(3,"QUEP1");
		patientMap.put(5,"SHEP1");

	}

	public void patientStart(String clientID) throws Exception
	{		
		operations(clientID);
	}
	public void operations(String clientID) throws Exception
	{	

		System.out.println("~~~~~ Appointment Slots In Hospitals ~~~~~");
		printAppointmentByType(UDPClient.listAppointmentAvailability(1111, "").getMap(),"Physician");
		printAppointmentByType(UDPClient.listAppointmentAvailability(2222, "").getMap(),"Physician");
		printAppointmentByType(UDPClient.listAppointmentAvailability(3333, "").getMap(),"Physician");

		printAppointmentByType(UDPClient.listAppointmentAvailability(1111, "").getMap(),"Surgeon");
		printAppointmentByType(UDPClient.listAppointmentAvailability(2222, "").getMap(),"Surgeon");
		printAppointmentByType(UDPClient.listAppointmentAvailability(3333, "").getMap(),"Surgeon");

		printAppointmentByType(UDPClient.listAppointmentAvailability(1111, "").getMap(),"Dental");
		printAppointmentByType(UDPClient.listAppointmentAvailability(2222, "").getMap(),"Dental");
		printAppointmentByType(UDPClient.listAppointmentAvailability(3333, "").getMap(),"Dental");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println();
		System.out.println("Choose one of the following options:"
					+ "\n1. Exit\n2. Book appointment;\n3. Get appointment Schedule;\n4. Cancel appointment;\n5. Swap appointment;");
				
		int option=0;
		Scanner keyboard=new Scanner(System.in);
		option=keyboard.nextInt();
		try {
			try {
				selectOperations(option,clientID);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void selectOperations(int option, String clientID) throws Exception
	{
		char clientType=clientID.charAt(3);
		
		switch(option) 
		{
			case 1:
				return;
			case 2:
			{
				boolean result = false;
				String appointmentID=setAppointmentID();
				String appointmentType=setAppointmentType();	
				if(clientID.contains("MTL"))
				{
				    result=MTLobj.bookAppointment(clientID, appointmentID, appointmentType);
					
					String resultStr=(result==true)?"Success":"Failed";
					MTLobj.writeTxtClient(clientID,"book Appointment", resultStr);
					MTLobj.writeTxtServerMTL(clientID,clientID,appointmentType,appointmentID,"book Appointment", resultStr);
				}
				else if(clientID.contains("QUE"))
				{
					 result=QUEobj.bookAppointment(clientID, appointmentID, appointmentType);
					
					String resultStr=(result==true)?"Success":"Failed";
					QUEobj.writeTxtClient(clientID,"book Appointment", resultStr);
					QUEobj.writeTxtServerQUE(clientID,clientID,appointmentType,appointmentID,"book Appointment", resultStr);
				}
				else if(clientID.contains("SHE"))
				{
					 result=SHEobj.bookAppointment(clientID, appointmentID, appointmentType);

					String resultStr=(result==true)?"Success":"Failed";
					SHEobj.writeTxtClient(clientID,"book Appointment", resultStr);
					SHEobj.writeTxtServerSHE(clientID,clientID,appointmentType,appointmentID,"book Appointment", resultStr);
				}
				
				if(result){
					System.out.println("Booking successfully for: " + appointmentType + " " + appointmentID);
				}else {
					System.out.println("Booking failed for: " + appointmentType + " " + appointmentID);
				}
				
				operations(clientID);
				
				
			}break;
			case 3:
			{
				boolean result = false;
				if(clientID.contains("MTL"))
				{
					 result=MTLobj.getAppointmentSchedule(clientID);
					System.out.println("~~~~~~~~Your booked appointment~~~~~~~~");
					String resultStr=(result==true)?"Success":"Failed";
					MTLobj.writeTxtClient(clientID,"get Appointment Schedule", resultStr);
					MTLobj.writeTxtServerMTL(clientID,clientID,"-","-","get Appointment Schedule", resultStr);
				}
				else if(clientID.contains("QUE"))
				{
					 result=QUEobj.getAppointmentSchedule(clientID);
					System.out.println("Your booked appointment");

					String resultStr=(result==true)?"Success":"Failed";
					QUEobj.writeTxtClient(clientID,"get Appointment Schedule", resultStr);
					QUEobj.writeTxtServerQUE(clientID,clientID,"-","-","get Appointment Schedule", resultStr);
				}
				else if(clientID.contains("SHE"))
				{
					 result=SHEobj.getAppointmentSchedule(clientID);
					System.out.println("Your booked appointment");

					String resultStr=(result==true)?"Success":"Failed";
					SHEobj.writeTxtClient(clientID,"get Appointment Schedule", resultStr);
					SHEobj.writeTxtServerSHE(clientID,clientID,"-","-","get Appointment Schedule", resultStr);
				}
				printAppointmentBySchedule(UDPClient.getAppointmentSchedule(1111, "").getMap(), clientID);
				printAppointmentBySchedule(UDPClient.getAppointmentSchedule(3333, "").getMap(), clientID);
				printAppointmentBySchedule(UDPClient.getAppointmentSchedule(2222, "").getMap(), clientID);
				System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				operations(clientID);
								
			}break;
			case 4:
			{
					boolean result = false;
					String appointmentID=setAppointmentID();
					String appointmentType=setAppointmentType();
					if(clientID.contains("MTL"))
					{
						 result=MTLobj.cancelAppointment( clientID,appointmentID, appointmentType);
					
						String resultStr=(result==true)?"Success":"Failed";
						MTLobj.writeTxtClient(clientID,"cancel Appointment", resultStr);
						MTLobj.writeTxtServerMTL(clientID,clientID,"-","-","cancel Appointment", resultStr);
					}
					else if(clientID.contains("QUE"))
					{
						 result=QUEobj.cancelAppointment( clientID,appointmentID, appointmentType);
						
						String resultStr=(result==true)?"Success":"Failed";
						QUEobj.writeTxtClient(clientID,"cancel Appointment", resultStr);
						QUEobj.writeTxtServerQUE(clientID,clientID,"-","-","cancel Appointment", resultStr);
					}
					else if(clientID.contains("SHE"))
					{
						 result=SHEobj.cancelAppointment( clientID,appointmentID, appointmentType);
						
						String resultStr=(result==true)?"Success":"Failed";
						SHEobj.writeTxtClient(clientID,"cancel Appointment", resultStr);
						SHEobj.writeTxtServerSHE(clientID,clientID,"-","-","cancel Appointment", resultStr);
					}
					
					if(result){
						System.out.println("Cancel successfully for: " + appointmentType + " " + appointmentID);
					}else {
						System.out.println("Cancel failed for: " + appointmentType + " " + appointmentID);
					}
					
					operations(clientID);
								
			}break;
			case 5:
			{	
				boolean resultSwap = false;
				System.out.println("Please type the old appointment ID and type:");
				String oldAppointmentID=setAppointmentID();
				String oldAppointmentType=setAppointmentType();
				System.out.println("Please type the new appointment ID and type:");
				String newAppointmentID=setAppointmentID();
				String newAppointmentType=setAppointmentType();
				if(clientID.contains("MTL"))
				{
					 resultSwap=MTLobj.swapAppointment(clientID, clientID,oldAppointmentID, oldAppointmentType,newAppointmentID, newAppointmentType);
					String resultStr=(resultSwap==true)?"Success":"Failed";
					MTLobj.writeTxtClient(clientID,"swap Appointment", resultStr);
					MTLobj.writeTxtServerMTL(clientID,clientID,"-","-","swap Appointment", resultStr);
				}
				else if(clientID.contains("QUE"))
				{
					 resultSwap=QUEobj.swapAppointment(clientID, clientID,oldAppointmentID, oldAppointmentType,newAppointmentID, newAppointmentType);
					String resultStr=(resultSwap==true)?"Success":"Failed";
					QUEobj.writeTxtClient(clientID,"swap Appointment", resultStr);
					QUEobj.writeTxtServerQUE(clientID,clientID,"-","-","swap Appointment", resultStr);
				}
				else if(clientID.contains("SHE"))
				{
					 resultSwap=SHEobj.swapAppointment(clientID, clientID,oldAppointmentID, oldAppointmentType,newAppointmentID, newAppointmentType);
					String resultStr=(resultSwap==true)?"Success":"Failed";
					SHEobj.writeTxtClient(clientID,"swap Appointment", resultStr);
					SHEobj.writeTxtServerSHE(clientID,clientID,"-","-","swap Appointment", resultStr);
				}
				
				if(resultSwap){
					System.out.println("Swap successfully for: " + oldAppointmentType + " " + oldAppointmentID + " to " + newAppointmentType + " " + newAppointmentID);
				}else {
					System.out.println("Swap failed for: " + oldAppointmentType + " " + oldAppointmentID + " to " + newAppointmentType + " " + newAppointmentID);
				}
				
				operations(clientID);	
				
			}break;			
		}
	}
	public void outputClientInfo()
	{
		patientMap.entrySet().forEach(entry->{System.out.print(" "+entry.getValue());});
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	

}
