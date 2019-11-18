package Client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Map;

import Server.MTLServer;
import Server.Message;

public class UDPClient {
	private final static int maxCapacity=3;
	
    public synchronized static Message listAppointmentAvailability(int port, String appointmentType) throws ClassNotFoundException, IOException{
    	DatagramSocket SocketCli=null;
    	InetAddress IPAddress = null;
    	byte[] bytes = null;
    	byte[] data = null;
    	DatagramPacket sendPacket = null;
    	DatagramPacket incomingPacket = null;	
    	byte[] dataBack = null;

        Message msg = null;

        try{
        	SocketCli = new DatagramSocket();
			IPAddress = InetAddress.getByName("localhost");				
			bytes = new byte[1024];
        } catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}
        msg = new Message("Connect for listing");

		data = BytesUtil.toByteArray(msg);
		sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
		SocketCli.send(sendPacket);
		incomingPacket = new DatagramPacket(bytes, bytes.length);
		SocketCli.receive(incomingPacket);
		
		dataBack = incomingPacket.getData();
		Message message = (Message) BytesUtil.toObject(dataBack);
		
        return message;
    }
    
    public synchronized static Message getAppointmentSchedule(int port, String patientID) throws ClassNotFoundException, IOException{

    	DatagramSocket SocketCli=null;
    	InetAddress IPAddress = null;
    	byte[] incomingData = null;

    	byte[] data = null;
    	DatagramPacket sendPacket = null;
    	DatagramPacket incomingPacket = null;	
    	byte[] dataBack = null;

        Message msg = null;

        try{
        	SocketCli = new DatagramSocket();
			IPAddress = InetAddress.getByName("localhost");				
			incomingData = new byte[1024];
        } catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
		}
        msg = new Message("Connect for listing");
		data = BytesUtil.toByteArray(msg);
		sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
		SocketCli.send(sendPacket);
		incomingPacket = new DatagramPacket(incomingData, incomingData.length);
		SocketCli.receive(incomingPacket);
		
		dataBack = incomingPacket.getData();

		Message message = (Message) BytesUtil.toObject(dataBack);
		
        return message;
    }
    
    public synchronized static boolean book(int port, String clientID,String patientID, String appointmentID, String appointmentType) throws ClassNotFoundException, IOException{
    	DatagramSocket SocketCli=null;
    	InetAddress IPAddress = null;
    	byte[] incomingData = null;
    	ByteArrayOutputStream outputStream = null;
    	ObjectOutputStream os = null;
    	byte[] data = null;
    	DatagramPacket sendPacket = null;
    	DatagramPacket incomingPacket = null;	
    	byte[] dataBack = null;
    	ByteArrayInputStream in = null;
    	ObjectInputStream is = null;
    	Map<String, Map<String,ArrayList<String>>> otherMap;
    	Message msg=null;
    	
    	try {
			SocketCli = new DatagramSocket();
			IPAddress = InetAddress.getByName("localhost");				
			incomingData = new byte[1024];
			msg = new Message("Connect for modifying");
    	}catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	outputStream = new ByteArrayOutputStream();
		os = new ObjectOutputStream(outputStream);
		os.writeObject(msg);
		data = outputStream.toByteArray();
		sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
		SocketCli.send(sendPacket);

		incomingPacket = new DatagramPacket(incomingData, incomingData.length);
		SocketCli.receive(incomingPacket);
		
		dataBack = incomingPacket.getData();
		in = new ByteArrayInputStream(dataBack);
		is = new ObjectInputStream(in);
		Message msg1 = (Message) is.readObject();
		otherMap=msg1.getMap();

		
		otherMap.get(appointmentType).get(appointmentID).add(patientID);
		String changeCapacityStr=otherMap.get(appointmentType).get(appointmentID).get(0);
		int changeCapacityInt=Integer.parseInt(changeCapacityStr);
		int changed=changeCapacityInt-1;
		String changedStr=changed+"";
		otherMap.get(appointmentType).get(appointmentID).set(0, changedStr);

		
		Message msg2 = new Message(otherMap);
		os.writeObject(msg2);
		data = outputStream.toByteArray();
		sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
		SocketCli.send(sendPacket);					

    	return true;
    }
    
    public synchronized static boolean cancel(int port, String clientID, String patientID, String appointmentID, String appointmentType) throws ClassNotFoundException, IOException{
    	DatagramSocket SocketCli=null;
    	InetAddress IPAddress = null;
    	byte[] incomingData = null;
    	ByteArrayOutputStream outputStream = null;
    	ObjectOutputStream os = null;
    	byte[] data = null;
    	DatagramPacket sendPacket = null;
    	DatagramPacket incomingPacket = null;	
    	byte[] dataBack = null;
    	ByteArrayInputStream in = null;
    	ObjectInputStream is = null;
    	Map<String, Map<String,ArrayList<String>>> otherMap;
    	Message msg=null;
    	
    	try {
			SocketCli = new DatagramSocket();
			IPAddress = InetAddress.getByName("localhost");				
			incomingData = new byte[1024];
			msg = new Message("Connect for modifying");
    	}catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	outputStream = new ByteArrayOutputStream();
		os = new ObjectOutputStream(outputStream);
		os.writeObject(msg);
		data = outputStream.toByteArray();
		sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
		SocketCli.send(sendPacket);
		incomingPacket = new DatagramPacket(incomingData, incomingData.length);
		SocketCli.receive(incomingPacket);
		
		dataBack = incomingPacket.getData();
		in = new ByteArrayInputStream(dataBack);
		is = new ObjectInputStream(in);
		Message msg1 = (Message) is.readObject();
		otherMap=msg1.getMap();
		
		otherMap.get(appointmentType).get(appointmentID).remove(patientID);
		int remainingCapacity = maxCapacity -otherMap.get(appointmentType).get(appointmentID).size()+2;
		String remainingCapacityStr=remainingCapacity+"";
		otherMap.get(appointmentType).get(appointmentID).set(0, remainingCapacityStr);
    	
		
		Message msg2 = new Message(otherMap);
		os.writeObject(msg2);
		data = outputStream.toByteArray();
		sendPacket = new DatagramPacket(data, data.length, IPAddress, port);
		SocketCli.send(sendPacket);		
		
    	return true;
    }
    



}
