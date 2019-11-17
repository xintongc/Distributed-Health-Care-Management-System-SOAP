package Client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class AdminClient extends Client implements Runnable{
	Map<Integer, String> adminMap = new HashMap<>();
	
	public AdminClient()
	{
		adminMap.put(1,"MTLA0001");adminMap.put(2,"QUEA0001");adminMap.put(3,"SHEA0001");
	}

	public void adminStart(String clientID) throws Exception
	{		
		operations(clientID);

	}
	public void operations(String clientID) throws Exception
	{
		System.out.println();
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
			+ "\n1. Exit\n2. Book appointment;\n3. Get appointment Schedule;\n4. Cancel appointment;\n5. Swap appointment;"
			+"\n6. Add appointment;\n7. Remove appointment;\n8. List appointment availability;");
		
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
				if(clientType=='A')
				{
					String patientID=super.setPatientID();
					String appointmentID=super.setAppointmentID();
					String appointmentType=super.setAppointmentType();	
					if(clientID.contains("MTL"))
					{
						 result=MTLobj.bookAppointment(clientID,patientID, appointmentID, appointmentType);
						String resultStr=(result==true)?"Success":"Failed";
						MTLobj.writeTxtClient(clientID,"book Appointment", resultStr);
						MTLobj.writeTxtServerMTL(clientID,patientID,appointmentType,appointmentID,"book Appointment", resultStr);
					}
					else if(clientID.contains("QUE"))
					{
						 result=QUEobj.bookAppointment(clientID,patientID, appointmentID, appointmentType);
						String resultStr=(result==true)?"Success":"Failed";
						QUEobj.writeTxtClient(clientID,"book Appointment", resultStr);
						QUEobj.writeTxtServerQUE(clientID,patientID,appointmentType,appointmentID,"book Appointment", resultStr);
					}
					else if(clientID.contains("SHE"))
					{
						 result=SHEobj.bookAppointment(clientID,patientID, appointmentID, appointmentType);
						String resultStr=(result==true)?"Success":"Failed";
						SHEobj.writeTxtClient(clientID,"book Appointment", resultStr);
						SHEobj.writeTxtServerSHE(clientID,patientID,appointmentType,appointmentID,"book Appointment", resultStr);
					}
					
					if(result){
						System.out.println("Booking successfully for: " + appointmentType + " " + appointmentID);
					}else {
						System.out.println("Booking failed for: " + appointmentType + " " + appointmentID);
					}
					
					operations(clientID);
				}
				
			}break;
			case 3:
			{	
				boolean result = false;
				if(clientType=='A') {
					String patientID=setPatientID();
					if(patientID.contains("MTL"))
					{
						 result=MTLobj.getAppointmentSchedule(patientID);
						 System.out.println("Your booked appointment");
						printAppointmentBySchedule(UDPClient.getAppointmentSchedule(1111, "").getMap(), patientID);
						String resultStr=(result==true)?"Success":"Failed";
						MTLobj.writeTxtClient(clientID,"get Appointment Schedule", resultStr);
						MTLobj.writeTxtServerMTL(clientID,patientID,"-","-","get Appointment Schedule", resultStr);
					}
					else if(patientID.contains("QUE"))
					{
						 result=QUEobj.getAppointmentSchedule(patientID);
						 System.out.println("Your booked appointment");
						printAppointmentBySchedule(UDPClient.getAppointmentSchedule(3333, "").getMap(), patientID);
						String resultStr=(result==true)?"Success":"Failed";
						QUEobj.writeTxtClient(clientID,"get Appointment Schedule", resultStr);
						QUEobj.writeTxtServerQUE(clientID,patientID,"-","-","get Appointment Schedule", resultStr);
					}
					else if(patientID.contains("SHE"))
					{
						result=SHEobj.getAppointmentSchedule(patientID);
						System.out.println("Your booked appointment");
						printAppointmentBySchedule(UDPClient.getAppointmentSchedule(2222, "").getMap(), patientID);
						String resultStr=(result==true)?"Success":"Failed";
						SHEobj.writeTxtClient(clientID,"get Appointment Schedule", resultStr);
						SHEobj.writeTxtServerSHE(clientID,patientID,"-","-","get Appointment Schedule", resultStr);
					}
					
					operations(clientID);
				}
								
			}break;
			case 4:
			{
				boolean result = false;
				if(clientType=='A') {
					String patientID=setPatientID();
					String appointmentID=setAppointmentID();
					String appointmentType=setAppointmentType();
					if(clientID.contains("MTL"))
					{
						 result=MTLobj.cancelAppointment(clientID, patientID,appointmentID, appointmentType);
						String resultStr=(result==true)?"Success":"Failed";
						MTLobj.writeTxtClient(clientID,"cancel Appointment", resultStr);
						MTLobj.writeTxtServerMTL(clientID,patientID,"-","-","cancel Appointment", resultStr);
					}
					else if(clientID.contains("QUE"))
					{
						 result=QUEobj.cancelAppointment(clientID, patientID,appointmentID, appointmentType);
						String resultStr=(result==true)?"Success":"Failed";
						QUEobj.writeTxtClient(clientID,"cancel Appointment", resultStr);
						QUEobj.writeTxtServerQUE(clientID,patientID,"-","-","cancel Appointment", resultStr);
					}
					else if(clientID.contains("SHE"))
					{
						 result=SHEobj.cancelAppointment(clientID, patientID,appointmentID, appointmentType);
						String resultStr=(result==true)?"Success":"Failed";
						SHEobj.writeTxtClient(clientID,"cancel Appointment", resultStr);
						SHEobj.writeTxtServerSHE(clientID,patientID,"-","-","cancel Appointment", resultStr);
					}
					
					if(result){
						System.out.println("Cancel successfully for: " + appointmentType + " " + appointmentID);
					}else {
						System.out.println("Cancel failed for: " + appointmentType + " " + appointmentID);
					}
					
					operations(clientID);
				}				
			}break;
			case 5:
			{
				boolean resultSwap = false;
				if(clientType=='A') {
					String patientID=setPatientID();
					System.out.println("Please type the old appointment ID and type:");
					String oldAppointmentID=setAppointmentID();
					String oldAppointmentType=setAppointmentType();
					System.out.println("Please type the new appointment ID and type:");
					String newAppointmentID=setAppointmentID();
					String newAppointmentType=setAppointmentType();
					if(clientID.contains("MTL"))
					{
						 resultSwap=MTLobj.swapAppointment(clientID, patientID,oldAppointmentID, oldAppointmentType,newAppointmentID, newAppointmentType);
						String resultStr=(resultSwap==true)?"Success":"Failed";
						MTLobj.writeTxtClient(clientID,"swap Appointment", resultStr);
						MTLobj.writeTxtServerMTL(clientID,patientID,"-","-","swap Appointment", resultStr);
					}
					else if(clientID.contains("QUE"))
					{
						 resultSwap=QUEobj.swapAppointment(clientID, patientID,oldAppointmentID, oldAppointmentType,newAppointmentID, newAppointmentType);
						String resultStr=(resultSwap==true)?"Success":"Failed";
						QUEobj.writeTxtClient(clientID,"swap Appointment", resultStr);
						QUEobj.writeTxtServerQUE(clientID,patientID,"-","-","swap Appointment", resultStr);
					}
					else if(clientID.contains("SHE"))
					{
						 resultSwap=SHEobj.swapAppointment(clientID, patientID,oldAppointmentID, oldAppointmentType,newAppointmentID, newAppointmentType);
						String resultStr=(resultSwap==true)?"Success":"Failed";
						SHEobj.writeTxtClient(clientID,"swap Appointment", resultStr);
						SHEobj.writeTxtServerSHE(clientID,patientID,"-","-","swap Appointment", resultStr);
					}
					
					if(resultSwap){
						System.out.println("Swap successfully for: " + oldAppointmentType + " " + oldAppointmentID + " to " + newAppointmentType + " " + newAppointmentID);
					}else {
						System.out.println("Swap failed for: " + oldAppointmentType + " " + oldAppointmentID + " to " + newAppointmentType + " " + newAppointmentID);
					}
					
					operations(clientID);					
					
				}
			}break;
			case 6:
			{
				boolean result = false;
				System.out.println("You are trying to add an appointment.");
				boolean doLoop=false;
				String appointmentID="";
				String appointmentType="";
				int appointmentWeekInt=0;
				String appointmentWeekStr="";
				int capacity=0;
				capacity=setCapacity();
				String capacityStr=capacity+"";
				
				if(clientID.contains("MTL"))
				{
					do {
						appointmentID=setAppointmentID();
						appointmentType=setAppointmentType();
						appointmentWeekInt=setAppointmentWeek();
						appointmentWeekStr=appointmentWeekInt+"";
						if(MTLobj.checkAppointmentExisted(appointmentID,appointmentType)) {
							System.out.println("The appointment you entered exists in MTL Database. Please enter another one.");
							doLoop=true;
						}else {
							doLoop=false;
						}
					}while(doLoop);
					 result=MTLobj.addAppointment(appointmentID,appointmentType,capacityStr,appointmentWeekStr);
					String resultStr=(MTLobj.checkAppointmentExisted(appointmentID,appointmentType)==true)?"Success":"Failed";
					MTLobj.writeTxtClient(clientID,"add Appointment", resultStr);
					MTLobj.writeTxtServerMTL(clientID,clientID,"-","-","add Appointment", resultStr);
				}
				else if(clientID.contains("QUE"))
				{
					do {
						appointmentID=setAppointmentID();
						appointmentType=setAppointmentType();
						appointmentWeekInt=setAppointmentWeek();
						appointmentWeekStr=appointmentWeekInt+"";
						if(QUEobj.checkAppointmentExisted(appointmentID,appointmentType)) {
							System.out.println("The appointment you entered exists in MTL Database. Please enter another one.");
							doLoop=true;
						}else {
							doLoop=false;
						}
					}while(doLoop);
					 result=QUEobj.addAppointment(appointmentID,appointmentType,capacityStr,appointmentWeekStr);
					String resultStr=(QUEobj.checkAppointmentExisted(appointmentID,appointmentType)==true)?"Success":"Failed";
					QUEobj.writeTxtClient(clientID,"add Appointment", resultStr);
					QUEobj.writeTxtServerQUE(clientID,clientID,"-","-","add Appointment", resultStr);
				}
				else if(clientID.contains("SHE"))
				{
					do {
						appointmentID=setAppointmentID();
						appointmentType=setAppointmentType();
						appointmentWeekInt=setAppointmentWeek();
						appointmentWeekStr=appointmentWeekInt+"";
						if(SHEobj.checkAppointmentExisted(appointmentID,appointmentType)) {
							System.out.println("The appointment you entered exists in MTL Database. Please enter another one.");
							doLoop=true;
						}else {
							doLoop=false;
						}
					}while(doLoop);
					 result=SHEobj.addAppointment(appointmentID,appointmentType,capacityStr,appointmentWeekStr);
					String resultStr=(SHEobj.checkAppointmentExisted(appointmentID,appointmentType)==true)?"Success":"Failed";
					SHEobj.writeTxtClient(clientID,"add Appointment", resultStr);
					SHEobj.writeTxtServerSHE(clientID,clientID,"-","-","add Appointment", resultStr);
				}
				
				if(result){
					System.out.println("Add Appointment successfully for: " + appointmentType + " " + appointmentID);
				}else {
					System.out.println("Add Appointment failed for: " + appointmentType + " " + appointmentID);
				}
				
				operations(clientID);
			}break;
			case 7:
			{
				boolean result = false;
				System.out.println("You are trying to remove an appointment.");
				boolean doLoop=false;
				String appointmentID="";
				String appointmentType="";
				
				if(clientID.contains("MTL"))
				{
					do {
						appointmentID=setAppointmentID();
						appointmentType=setAppointmentType();
						if(!MTLobj.checkAppointmentExisted(appointmentID,appointmentType)) {
							System.out.println("The appointment you entered does not exist in MTL Database. Please enter another one.");
							MTLobj.writeTxtServerMTL(clientID,clientID,"-","-","remove Appointment", "Failed");
							doLoop=true;
						}else {
							doLoop=false;
						}
					}while(doLoop);
					 result=MTLobj.removeAppointment(appointmentID,appointmentType);
					String resultStr=(result==true)?"Success":"Failed";
					MTLobj.writeTxtClient(clientID,"remove Appointment", resultStr);
					MTLobj.writeTxtServerMTL(clientID,clientID,"-","-","remove Appointment", resultStr);
				}
				else if(clientID.contains("QUE"))
				{
					do {
						appointmentID=setAppointmentID();
						appointmentType=setAppointmentType();
						if(!QUEobj.checkAppointmentExisted(appointmentID,appointmentType)) {
							System.out.println("The appointment you entered does not exist in MTL Database. Please enter another one.");
							QUEobj.writeTxtServerQUE(clientID,clientID,"-","-","remove Appointment", "Failed");
							doLoop=true;
						}else {
							doLoop=false;
						}
					}while(doLoop);
					 result=QUEobj.removeAppointment(appointmentID,appointmentType);
					String resultStr=(result==true)?"Success":"Failed";
					QUEobj.writeTxtClient(clientID,"remove Appointment", resultStr);
					QUEobj.writeTxtServerMTL(clientID,clientID,"-","-","remove Appointment", resultStr);
				}
				else if(clientID.contains("SHE"))
				{
					do {
						appointmentID=setAppointmentID();
						appointmentType=setAppointmentType();
						if(!SHEobj.checkAppointmentExisted(appointmentID,appointmentType)) {
							System.out.println("The appointment you entered does not exist in MTL Database. Please enter another one.");
							SHEobj.writeTxtServerSHE(clientID,clientID,"-","-","remove Appointment", "Failed");
							doLoop=true;
						}else {
							doLoop=false;
						}
					}while(doLoop);
					 result=SHEobj.removeAppointment(appointmentID,appointmentType);
					String resultStr=(result==true)?"Success":"Failed";
					SHEobj.writeTxtClient(clientID,"remove Appointment", resultStr);
					SHEobj.writeTxtServerMTL(clientID,clientID,"-","-","remove Appointment", resultStr);
				}
				if(result){
					System.out.println("Removing appointment successfully for: " + appointmentType + " " + appointmentID);
				}else {
					System.out.println("Removing appointment failed for: " + appointmentType + " " + appointmentID);
				}
				
				operations(clientID);
			}break;
			case 8:
			{
				String appointmentType=setAppointmentType();
				if(clientID.contains("MTL"))
				{
					boolean result=MTLobj.listAppointmentAvailability(appointmentType);
					String resultStr=(result==true)?"Success":"Failed";
					MTLobj.writeTxtClient(clientID,"list Appointment Availability", resultStr);
					MTLobj.writeTxtServerMTL(clientID,clientID,"-","-","list Appointment Availability", resultStr);
				}
				else if(clientID.contains("QUE"))
				{
					boolean result=QUEobj.listAppointmentAvailability(appointmentType);
					String resultStr=(result==true)?"Success":"Failed";
					QUEobj.writeTxtClient(clientID,"list Appointment Availability", resultStr);
					QUEobj.writeTxtServerQUE(clientID,clientID,"-","-","list Appointment Availability", resultStr);
				}
				else if(clientID.contains("SHE"))
				{
					boolean result=SHEobj.listAppointmentAvailability(appointmentType);
					String resultStr=(result==true)?"Success":"Failed";
					SHEobj.writeTxtClient(clientID,"list Appointment Availability", resultStr);
					SHEobj.writeTxtServerSHE(clientID,clientID,"-","-","list Appointment Availability", resultStr);
				}
				
				printAppointmentByType(UDPClient.listAppointmentAvailability(1111, "").getMap(),appointmentType);
				printAppointmentByType(UDPClient.listAppointmentAvailability(2222, "").getMap(),appointmentType);
				printAppointmentByType(UDPClient.listAppointmentAvailability(3333, "").getMap(),appointmentType);
	
				operations(clientID);
			}break;			
			
		}
			
	}
	
	public void outputClientInfo()
	{
		adminMap.entrySet().forEach(entry->{System.out.print(" "+entry.getValue());});
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
