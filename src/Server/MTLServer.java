package Server;

import javax.jws.WebService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import Client.BytesUtil;
import Client.UDPClient;

@WebService(endpointInterface = "Server.ServerInterface")
public class MTLServer implements ServerInterface{

	static Map<String, Map<String,ArrayList<String>>> MTLMap = new HashMap<String, Map<String,ArrayList<String>>>();
	Map<String, Map<String,ArrayList<String>>> otherMap1=null;
	Map<String, Map<String,ArrayList<String>>> otherMap2=null;
	private final int maxCapacity=3;
	PrintWriter outputTxtClient = null;
	PrintWriter outputTxtServer = null;
	Listening listening = new Listening();

	public MTLServer(){
		super();
		ArrayList<String> temp1=new ArrayList<String>();
		ArrayList<String> temp2=new ArrayList<String>();
		ArrayList<String> temp3=new ArrayList<String>();
		temp1.add("3");temp1.add("2");
		temp2.add("3");temp2.add("2");
		temp3.add("3");temp3.add("2");
		Map<String,ArrayList<String>> t1=new HashMap<String,ArrayList<String>>();
		t1.put("MTLE111111",temp1);
		MTLMap.put("Physician",t1);
		Map<String,ArrayList<String>> t2=new HashMap<String,ArrayList<String>>();
		t2.put("MTLA111111",temp2);
		MTLMap.put("Surgeon",t2);
		Map<String,ArrayList<String>> t3=new HashMap<String,ArrayList<String>>();
		t3.put("MTLA111111",temp3);
		MTLMap.put("Dental",t3);
		listening.start();
	}

	public static void main(String args[]) throws Exception
	{

		try {
			System.out.println("MTL Server ready and waiting ...");
		}

		catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

		System.out.println("MTLServer Exiting ...");
		System.out.println("Server is Up & Running");
	}
	
	public synchronized boolean addAppointment(String appointmentID, String appointmentType, String strCapacity,String appointmentWeekStr)
	{
		ArrayList<String> subValue=new ArrayList<String>();
		//String strCapacity=String.valueOf(capacity);
		subValue.add(strCapacity);subValue.add(appointmentWeekStr);
		String cityName=appointmentID.substring(0, 3);
//		Map<String,ArrayList<String>> subMap = new HashMap<String,ArrayList<String>>();
//		subMap.put(appointmentID, subValue);
		if(cityName.equalsIgnoreCase("MTL")) {
			MTLMap.get(appointmentType).put(appointmentID, subValue);
//			MTLMap.put(appointmentType, subMap);
			System.out.println("The following appointment was added in the MTL Hospital System:");
			System.out.println(appointmentType+"  "+appointmentID+"  "+strCapacity);
			printAppointment(MTLMap);
			return true;
		}			
		else {
			System.out.println("You cannot add appointment for other cities.");
			return false;
		}

	}
	public boolean checkAppointmentExisted(String appointmentID, String appointmentType)
	{
		boolean key1=MTLMap.containsKey(appointmentType);
		boolean key2=MTLMap.get(appointmentType).containsKey(appointmentID);
		if(key1&&key2) {
			return true;
		}
		return false;
	}
	public synchronized boolean removeAppointment(String appointmentID, String appointmentType)
	{
		Map<String,ArrayList<String>> subMap = MTLMap.get(appointmentType);
		if(subMap.get(appointmentID) != null){
			subMap.remove(appointmentID);
			return true;
		}
		return false;
	}
	
	public boolean listAppointmentAvailability(String appointmentType)
	{

		Message msg1 = null;
		try {
			msg1 = UDPClient.listAppointmentAvailability(2222, appointmentType);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		Message msg2 = null;
		try {
			msg2 = UDPClient.listAppointmentAvailability(3333, appointmentType);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		otherMap1 = msg1.getMap();
		otherMap2 = msg2.getMap();

		printAppointmentByType(otherMap1,appointmentType);
		printAppointmentByType(otherMap2,appointmentType);
		printAppointmentByType(MTLMap, appointmentType);

		return true;
	}
	
	private void bookInMap(Map<String, Map<String,ArrayList<String>>> map, String patientID, String appointmentID, String appointmentType) {
		map.get(appointmentType).get(appointmentID).add(patientID);
		String changeCapacityStr=map.get(appointmentType).get(appointmentID).get(0);
		int changeCapacityInt=Integer.parseInt(changeCapacityStr);
		int changed=changeCapacityInt-1;
		String changedStr=changed+"";
		map.get(appointmentType).get(appointmentID).set(0, changedStr);		
	}
	
	public synchronized boolean bookAppointment(String patientID, String appointmentID, String appointmentType) throws ClassNotFoundException, IOException
	{
		
		String city=appointmentID.substring(0, 3);
		
		switch(city) {
			case "MTL": {
				bookInMap(MTLMap, patientID, appointmentID, appointmentType);	
			}break;
			case "QUE":{
				UDPClient.book(3333, patientID, appointmentID, appointmentType);
			}break;
			case "SHE":{
				UDPClient.book(2222, patientID, appointmentID, appointmentType);
			}
		}
		
		System.out.println("Appointment booked");
		
		
		Message msg1 = null;
		try {
			msg1 = UDPClient.listAppointmentAvailability(2222, appointmentType);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		Message msg2 = null;
		try {
			msg2 = UDPClient.listAppointmentAvailability(3333, appointmentType);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		otherMap1 = msg1.getMap();
		otherMap2 = msg2.getMap();
		
		printAppointment(MTLMap);
		printAppointment(otherMap1);
		printAppointment(otherMap2);
	
		return true;
	}
	
	
	public boolean getAppointmentSchedule(String patientID)
	{
		Message msg1 = null;
		try {
			msg1 = UDPClient.getAppointmentSchedule(2222, patientID);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		Message msg2 = null;
		try {
			msg2 = UDPClient.getAppointmentSchedule(3333, patientID);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		otherMap1 = msg1.getMap();
		otherMap2 = msg2.getMap();
		printAppointmentBySchedule(otherMap1,patientID);
		printAppointmentBySchedule(otherMap2,patientID);
		printAppointmentBySchedule(MTLMap, patientID);
		
		return true;
	}
	
	private void cancelInMap(Map<String, Map<String,ArrayList<String>>> map, String patientID, String appointmentID, String appointmentType) {
		map.get(appointmentType).get(appointmentID).remove(patientID);
		int remainingCapacity=maxCapacity-map.get(appointmentType).get(appointmentID).size()+2;
		String remainingCapacityStr=remainingCapacity+"";
		map.get(appointmentType).get(appointmentID).set(0, remainingCapacityStr);
	}
	
	
	public synchronized boolean cancelAppointment(String patientID, String appointmentID,String appointmentType) throws ClassNotFoundException, IOException
	{
	String city=appointmentID.substring(0, 3);
		
		switch(city) {
			case "MTL": {
				cancelInMap(MTLMap, patientID, appointmentID, appointmentType);	
			}break;
			case "QUE":{
				UDPClient.cancel(3333, patientID, appointmentID, appointmentType);
			}break;
			case "SHE":{
				UDPClient.cancel(2222, patientID, appointmentID, appointmentType);
			}
		}
		
		System.out.println("Appointment canceled");
		
		
		Message msg1 = null;
		try {
			msg1 = UDPClient.listAppointmentAvailability(2222, appointmentType);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		Message msg2 = null;
		try {
			msg2 = UDPClient.listAppointmentAvailability(3333, appointmentType);
		} catch (ClassNotFoundException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		otherMap1 = msg1.getMap();
		otherMap2 = msg2.getMap();
		
		printAppointment(MTLMap);
		printAppointment(otherMap1);
		printAppointment(otherMap2);
	
		return true;
	}
	
	
	
	public synchronized boolean swapAppointment(String patientID,String oldAppointmentID, String oldAppointmentType,String newAppointmentID, String newAppointmentType) throws ClassNotFoundException, IOException {

		  Message msg1 = null;
		  try {
		    msg1 = UDPClient.listAppointmentAvailability(3333, oldAppointmentType);
		  } catch (ClassNotFoundException | IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    return false;
		  }
		  Message msg2 = null;
		  try {
		    msg2 = UDPClient.listAppointmentAvailability(2222, oldAppointmentType);
		  } catch (ClassNotFoundException | IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    return false;
		  }

		  otherMap1 = msg1.getMap();
		  otherMap2 = msg2.getMap();


		  String cityOld= oldAppointmentID.substring(0, 3);
		  String cityNew = newAppointmentID.substring(0, 3);

		  switch(cityNew) {
		  case "MTL":
		  {
		    bookInMap(MTLMap, patientID, newAppointmentID, newAppointmentType);
		  }break;
		  case "QUE":
		  {
		    UDPClient.book(3333, patientID, newAppointmentID, newAppointmentType);

		  }break;
		  case "SHE":
		  {
		    UDPClient.book(2222, patientID, newAppointmentID, newAppointmentType);

		  }break;
		}
		switch(cityOld) {

		  case "MTL":
		  {
		    cancelInMap(MTLMap, patientID, oldAppointmentID, oldAppointmentType);
		  }break;
		  case "QUE":
		  {
		    UDPClient.cancel(3333, patientID, oldAppointmentID, oldAppointmentType);
		  }break;
		  case "SHE":
		  {
		    UDPClient.cancel(2222, patientID, oldAppointmentID, oldAppointmentType);
		  }break;
		}

		  return true;
	}
	
	
	
	private void printAppointment(Map<String, Map<String,ArrayList<String>>> map)
	{
		for(Map.Entry<String, Map<String,ArrayList<String>>> mtl:map.entrySet())
		{
			String appointmentType=mtl.getKey();
			System.out.println(appointmentType);
			for(Map.Entry<String, ArrayList<String>> nestedMap:mtl.getValue().entrySet())
			{
				System.out.print("    "+nestedMap.getKey()+" ");
				for(String op:nestedMap.getValue()) {
					System.out.print(op+"  ");
				}
				System.out.println("");
			}
		}
	}
	private void printAppointmentByType(Map<String, Map<String,ArrayList<String>>> map, String type) {
		for(Map.Entry<String, Map<String,ArrayList<String>>> mtl:map.entrySet())
		{
			String appointmentType=mtl.getKey();
			if(appointmentType.equalsIgnoreCase(type)) {
				System.out.println(appointmentType+"  ");
				for(Map.Entry<String, ArrayList<String>> nestedMap:mtl.getValue().entrySet())
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
	private void printAppointmentBySchedule(Map<String, Map<String,ArrayList<String>>> map,String clientID){
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

	class Listening extends Thread{
//		public Thread t;
		public DatagramSocket socketSer;

//		public Listening(DatagramSocket socketSer){
//			this.socketSer = socketSer;
//		}

		public void run(){

			try {
				socketSer = new DatagramSocket(1111);

				while (true) {
					byte[] incomingData = new byte[1024];
					DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
					socketSer.receive(incomingPacket);
					byte[] data = incomingPacket.getData();
					ByteArrayInputStream in = new ByteArrayInputStream(data);
					ObjectInputStream is = new ObjectInputStream(in);
					String str="";
					try {
						Message msg = (Message) is.readObject();
						str=msg.getMsg();
						if(str.equalsIgnoreCase("Connect for listing")) {
							Message msgSend=new Message(MTLMap);

							ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
							ObjectOutput os = new ObjectOutputStream(outputStream1);
							os.writeObject(msgSend);

							InetAddress IPAddress = incomingPacket.getAddress();
							int port = incomingPacket.getPort();

							byte[] dataSend = outputStream1.toByteArray();
							DatagramPacket replyPacket =new DatagramPacket(dataSend, dataSend.length, IPAddress, port);
							socketSer.send(replyPacket);
//					writeTxtServerMTL("-","-","-","-","Send DB", "Success");
							outputStream1.close();
							os.close();
						}
						if(str.equalsIgnoreCase("Connect for modifying")) {
							Message msgSend=new Message(MTLMap);

							ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
							ObjectOutput os = new ObjectOutputStream(outputStream1);
							os.writeObject(msgSend);

							InetAddress IPAddress = incomingPacket.getAddress();
							int port = incomingPacket.getPort();

							byte[] dataSend = outputStream1.toByteArray();
							DatagramPacket replyPacket =new DatagramPacket(dataSend, dataSend.length, IPAddress, port);
							socketSer.send(replyPacket);
//					writeTxtServerMTL("-","-","-","-","Send DB", "Success");

							socketSer.receive(incomingPacket);
//					writeTxtServerMTL("-","-","-","-","Received DB", "Success");
							byte[] dataBack = incomingPacket.getData();


							Message msg1=null;
							try {
								msg1 = (Message) is.readObject();
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							//return message from server.
							MTLMap=msg1.getMap();
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

//					Message message = (Message) BytesUtil.toObject(dataBack);
//					MTLMap = message.getMap();

						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}

			} catch (SocketException e) {
				e.printStackTrace();
			} catch (IOException i) {
				i.printStackTrace();
			}


		}

//		public void start(){
//			if(t == null){
//				t = new Thread(this);
//				t.start();
//			}
//		}
	}


	
	private boolean validation(String appointmentType,String patientID,String task, String appointmentID,String clientID,
			Map<String, Map<String,ArrayList<String>>> otherMapA,Map<String, Map<String,ArrayList<String>>> otherMapB,
			Map<String, Map<String,ArrayList<String>>> otherMapC) {
		//a patient cannot book more than one appointment with the same appointment id and same appointment type
		String capacityStr1=otherMapA.get(appointmentType).get(appointmentID).get(0);
		int capacityInt1=Integer.parseInt(capacityStr1);
		boolean capacityOk1=capacityInt1>0?true:false;
		boolean contain1=otherMapA.get(appointmentType).get(appointmentID).contains(patientID);
		boolean success1=capacityOk1&&!contain1;
		if(contain1) {
			System.out.println("You cannot book more than one appointment with the same appointment id and same appointment type.");
			return false;
		}else if(!capacityOk1) {
			System.out.println("You cannot book this appointment because it is full.");
			return false;
		}
		
		//a patient cannot have more than one booking of same appointment type in a day.
		boolean contain2=false;
		String checkDate=appointmentID.substring(4);
		String orderedCheckDate=checkDate.substring(4)+checkDate.substring(2, 4)+checkDate.substring(0, 2);
		int convertOrderedCheckDate=Integer.parseInt(orderedCheckDate);
		String date="";
		String orderedDate="";
		int convertOrderedDate=0;
		String capacityStr2="";
		int capacityInt2=0;
		for(Map.Entry<String, ArrayList<String>> nestedMap:otherMapA.get(appointmentType).entrySet()) {
			String capacityStrTemp=otherMapA.get(appointmentType).get(appointmentID).get(0);
			capacityInt2=capacityInt2+Integer.parseInt(capacityStrTemp);			
			date=nestedMap.getKey().substring(4);
			orderedDate=date.substring(4)+date.substring(2, 4)+date.substring(0, 2);
			convertOrderedDate=Integer.parseInt(orderedDate);
			if(convertOrderedDate==convertOrderedCheckDate) {
				contain2=nestedMap.getValue().contains(patientID);
				if(contain2==true)
					break;
			}
		}
		boolean capacityOk2=capacityInt2>0?true:false;		
		boolean success2=capacityOk2&&!contain2;
		if(contain2) {
			System.out.println("cannot have more than one booking of same appointment type in a day.");
			return false;
		}else if(!capacityOk2) {
			System.out.println("You cannot book this appointment because it is full.");
			return false;
		}
		
		int count1=0;
		String dateToCheck1="";
		String weekCheck1="0";
		for(Map.Entry<String, Map<String,ArrayList<String>>> map:otherMapB.entrySet())
		{
			for(Map.Entry<String, ArrayList<String>> nestedMap:map.getValue().entrySet())
			{
				if(nestedMap.getValue().contains(patientID))
				{
					if(count1==0) {
						dateToCheck1=nestedMap.getKey();
						weekCheck1=nestedMap.getValue().get(1);
					    count1++;
					}else {
						String dateToCheck11=nestedMap.getKey();
						String subdateToCheck1=dateToCheck1.substring(6);
						String subdateToCheck11=dateToCheck11.substring(6);
						if(weekCheck1.equalsIgnoreCase(nestedMap.getValue().get(1))&&subdateToCheck1.equalsIgnoreCase(subdateToCheck11))
							count1++;
					}					   
				}					
			}
		}
		int count2=0;
		String dateToCheck2="";
		String weekCheck2="0";
		for(Map.Entry<String, Map<String,ArrayList<String>>> map:otherMapC.entrySet())
		{
			for(Map.Entry<String, ArrayList<String>> nestedMap:map.getValue().entrySet())
			{
				if(nestedMap.getValue().contains(patientID))
				{
					if(count2==0) {
						dateToCheck2=nestedMap.getKey();
						weekCheck2=nestedMap.getValue().get(1);
					    count2++;
					}else {
						String dateToCheck22=nestedMap.getKey();
						String subdateToCheck2=dateToCheck2.substring(6);
						String subdateToCheck22=dateToCheck22.substring(6);
						if(weekCheck1.equalsIgnoreCase(nestedMap.getValue().get(1))&&subdateToCheck2.equalsIgnoreCase(subdateToCheck22))
							count2++;
					}					   
				}					
			}
		}
		int count=count1+count2;
		boolean success3=count<3?true:false;
		if(!success3) {
			System.out.println("You cannot book more than 3 appointments from other cities.");
			return false;
			}
		if(success1&&success2&&success3)
			return true;
		else
			return false;
	}


    public void writeTxtClient(String clientID,String task, String result) {
        try
        {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            outputTxtClient =new PrintWriter(new FileOutputStream("client.txt",true));
            outputTxtClient.flush();
            outputTxtClient.printf("%-15s%-35s%-15s%-60s%n",clientID,task,result,timestamp);
            outputTxtClient.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Error opening the file client.txt.");
            System.exit(0);
        }
    }
    public void writeTxtServerMTL(String clientID,String patientID,String appointmentType,String appointmentID,String task, String result) {
        try
        {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            outputTxtServer =new PrintWriter(new FileOutputStream("MTLServer.txt",true));
            outputTxtServer.flush();
            outputTxtServer.printf("%-15s%-15s%-20s%-20s%-35s%-15s%-60s%n",clientID,patientID,appointmentType,appointmentID,task,result,timestamp);
            outputTxtServer.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Error opening the file MTLServer.txt.");
            System.exit(0);
        }
    }
    public void writeTxtServerQUE(String clientID,String patientID,String appointmentType,String appointmentID,String task, String result) {
        try
        {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            outputTxtServer =new PrintWriter(new FileOutputStream("QUEServer.txt",true));
            outputTxtServer.flush();
            outputTxtServer.printf("%-15s%-15s%-20s%-20s%-35s%-15s%-60s%n",clientID,patientID,appointmentType,appointmentID,task,result,timestamp);
            outputTxtServer.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Error opening the file MTLServer.txt.");
            System.exit(0);
        }
    }
    public void writeTxtServerSHE(String clientID,String patientID,String appointmentType,String appointmentID,String task, String result) {
        try
        {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            outputTxtServer =new PrintWriter(new FileOutputStream("SHEServer.txt",true));
            outputTxtServer.flush();
            outputTxtServer.printf("%-15s%-15s%-20s%-20s%-35s%-15s%-60s%n",clientID,patientID,appointmentType,appointmentID,task,result,timestamp);
            outputTxtServer.close();
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Error opening the file MTLServer.txt.");
            System.exit(0);
        }
    }
	

}
