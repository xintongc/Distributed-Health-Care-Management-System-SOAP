package RM_1;
import rm.StdMaps;
import rm.UDPRm;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


//Registry mtlRegistry = null;
//Registry queRegistry = null;
//Registry sheRegistry = null;
public class RemoteManager1 implements Runnable {

	ServerInterface mtlObj = null;
	ServerInterface sheObj = null;
	ServerInterface queObj = null;
   // private String patientID = null;
    
    boolean isAdmin;
//    String patientID ="";
//    String adminID = "";
    int seq=0;

	StdMaps stdMaps;
	Listening listening = new Listening();
    
    public RemoteManager1() {
//    	if(patientID.substring(3).equals("P")) {
//    		this.patientID = patientID;	
//    	}
//    	
//    	if(patientID.substring(3).equals("A")) {
//    		this.adminID = patientID;
//    		//???
//    		//checkCred(String adminID);
//    	}
//        
//        
//        System.out.println(patientID);
        
     
        	 
			mtlObj = new MtlServerObj();
			
		
 
			sheObj =  new SheServerObj();
			
		
			queObj =  new QueServerObj();
			Thread t1=new Thread(this);
			t1.setName("This is frontEnd default thread");
			t1.start();


		stdMaps = new StdMaps();
		setStdMapsFromUniqueMaps();
		listening.start();
 
		
        
    }
    public void output(HashMap<String,Integer> map) {
 		System.out.println("before loop");
     	  for (String key : map.keySet()) {
     		  System.out.println("in for loop");
            System.out.println("key= "+ key +" Value= "+map.get(key));

           }
     }
    
    public HashMap<String,Integer> showAllMaps() {
    	output(MtlServerObj.getDocMap());
    	return ( MtlServerObj.getDocMap());

    }
    
 


    public void run() {
    	revFromMulti();
    }
    
  
    
	
////////////////////////////////////////////////
    
    public void revFromMulti() {
		// TODO Auto-generated method stub
	     System.out.println("Inside send Data run by thread" + Thread.currentThread().getName());
		MulticastSocket aSocket = null;

	        HashMap<String,String> msgQue = new HashMap<>();

	        try {
	            System.out.println("Remote Manager 1 up and running");
	            aSocket = new MulticastSocket(9001);
	           // System.setProperty("java.net.preferIPv4Stack", "true");
				InetAddress group = InetAddress.getByName("224.1.2.3");
				aSocket.joinGroup(group);
	            while (true) {
	                byte[] buffer = new byte[1000];
	                byte[] outGoing = new byte[1000];
	                String inMsg = null;
	                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
	                aSocket.receive(request);
	                inMsg = new String(request.getData());
	                String[] seqNum = inMsg.split(" ", 2);
	                String build= null;
	                DatagramPacket reply =null;

	                System.out.println("RECIEVED IN RM1: "+ inMsg);

	                if(seqNum[1].startsWith("$")){
	                    seqNum[0] = String.valueOf((seq+1));
	                    System.out.println("alt");
	                    System.out.println("alt seqNum[0]: "+seqNum[0]);
	                }

	                if(seqNum[1].startsWith("%")){
	                    seqNum[0] = String.valueOf((seq-1));
	                    System.out.println("alt");
	                    System.out.println("alt seqNum[0]: "+seqNum[0]);
	                }
	                int incomeSeq = Integer.parseInt(seqNum[0]);
	                if (incomeSeq == seq) {
	                    System.out.println(" FROM SEQ: \nProcessed seq num: "+ seq + " Processed msg: " +seqNum[1] );
	                    //send to unpacking, this takes the string and runs corresponding methods, seqNum[1] is the actual request with the seq# removed
						unPack(seqNum[1]);

	                    seq++;
	                }
	                else {
	                    System.out.println("Duplicate packet" );
	                }

	                build = ""+seqNum[0]+" ";
	                outGoing = (build).getBytes();
	                reply = new DatagramPacket(outGoing, outGoing.length, request.getAddress(), request.getPort());
	                aSocket.send(reply);

	            }

	        } catch (SocketException e) {
	            System.out.println("Socket: " + e.getMessage());
	        } catch (IOException e) {
	            System.out.println("IO: " + e.getMessage());
	        } finally {
	            if (aSocket != null) aSocket.close();
	        }

}
    
    
    
    
//	needs to process msg and send back to frontend
	public void replyToFront(byte[] outgoing) {
  	  String retMsg = null;
        String temp = null;
        DatagramSocket aSocket = null;
         HashMap<String,byte[]> msgQue = new HashMap<>();
        String[] seqNum;
        int FEseq= seq;
        try {
         	aSocket = new DatagramSocket();
                aSocket.setSoTimeout(4000);

                InetAddress aHost = InetAddress.getByName("224.7.7.9");
                ///send and receive reply from Sherbrooke
                int seqPort = 5501;
                DatagramPacket request = new DatagramPacket(outgoing, outgoing.length, aHost, seqPort);
                aSocket.send(request);
                System.out.println("######## replyToFront "+FEseq+" replyToFront########");
                //put msg in que for case of resend...
                msgQue.put(String.valueOf(FEseq),outgoing);

                byte[] buffer = new byte[1000];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                Long ExitTime = System.nanoTime();

                    aSocket.receive(reply);
                    temp = (new String(reply.getData()));
                    seqNum = temp.split(" ", 2);

                int noRepCount=0;
                while(!seqNum[0].equals(String.valueOf(FEseq))){
                    System.out.println("if(!seqNum[0].equals(String.valueOf(seq)))");
                    if(msgQue.containsKey(String.valueOf(FEseq))){
                            outgoing = msgQue.get(String.valueOf(FEseq));
//                            build = build.replaceAll("\\$","");
//                            build = build.replaceAll("%","");
//                            build = build + " ret from client que";
                            //Here I need to change the message to turn the flag off
                            //m = (build).getBytes();
                            request = new DatagramPacket(outgoing, outgoing.length, aHost, seqPort);
                            System.out.println("resending: "+FEseq +" from que");
                            aSocket.send(request);
                            aSocket.receive(reply);
                            temp = (new String(reply.getData()));
                            seqNum = temp.split(" ", 2);
                        }
                        noRepCount++;
                        if(noRepCount> 5){
                        		System.out.println("if(noRepCount> 5)");
                        }
                }


                int incomeSeq = Integer.parseInt(seqNum[0]);
                if(FEseq == incomeSeq ){
                    System.out.println("return seqNum is matching!");
                    if(msgQue.containsKey(String.valueOf(FEseq))){
                        msgQue.remove(String.valueOf(FEseq));
                    }
                    FEseq++;

                }
                    retMsg = ("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\nThis is reply from server...:\n" + temp +" \n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    System.out.println(retMsg);

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null)
                aSocket.close();
        }

  }

 

  //takes a string for my code
	public byte[] prepareReturnPackage(String returnMsg){
		byte[] pkg;
		pkg = (seq +" " + returnMsg).getBytes();
		return pkg;
	}

	//takes a boolean for your code
	public byte[] prepareReturnPackage(boolean returnMsg){
		String outMsg =null;
		if(returnMsg){
			outMsg = "true";
		}else{
			outMsg = "false";
		}
		byte[] pkg;
		pkg = (seq +" " + outMsg).getBytes();
		return pkg;
	}


	public void unPack(String incomeReq){
		incomeReq = incomeReq.replaceAll("\\s+", "");
		incomeReq = incomeReq.replaceAll("\0", "");
		String[] msg = incomeReq.split(":");
		byte[] replyToFe = null;
		
		
		//Send result back to Front end
		//Here is an example using "samplemethod()", this method will be replaced with actual replica methods
		//just put all replica method calls inside "prepairReturnPackage( METHODS GO HERE!)"
		try {
		switch(msg[0]) {
			case "bookAppointment" :
				System.out.println("bookAppointment");
				replyToFe = prepareReturnPackage(bookAppointment(msg[1],msg[2],msg[3]));
				replyToFront(replyToFe);
				break;
			case "getAppointmentSchedule":
				System.out.println("getAppointmentSchedule");
				getAppointmentSchedule(msg[1]);
				replyToFe = prepareReturnPackage(getAppointmentSchedule(msg[1]));
				replyToFront(replyToFe);

				break;
			case "cancelAppointment":
				//not the same
				System.out.println("cancelAppointment");
				replyToFe = prepareReturnPackage(cancelAppointment(msg[1],msg[2],msg[3]));
				replyToFront(replyToFe);
				break;
			case "swapAppointment":
				System.out.println("swapAppointment");
				replyToFe = prepareReturnPackage(swapAppointment(msg[1],msg[2],msg[3],msg[4],msg[5]));
				replyToFront(replyToFe);
				break;
			case "addAppointment":
				System.out.println("addAppointment");
				//not the same
				replyToFe = prepareReturnPackage(addAppointment(msg[1],msg[2],Integer.valueOf(msg[3])));
				replyToFront(replyToFe);
				break;
			case "removeAppointment":
				System.out.println("removeAppointment");
				replyToFe = prepareReturnPackage(removeAppointment(msg[1],msg[2]));
				replyToFront(replyToFe);
				break;
			case "listAppointmentAvailability":
				System.out.println("listAppointmentAvailability");
				replyToFe = prepareReturnPackage(listAppointmentAvailability(msg[1]) );
				replyToFront(replyToFe);
				break;

			case "checkCred":
				System.out.println("checkCred");
				replyToFe = prepareReturnPackage(checkCred(msg[1],msg[2]));
				replyToFront(replyToFe);
				break;
				
			case "FAULT R1":
				System.out.println("FAULT R1");
				//fix but return empty string!
				replyToFe = prepareReturnPackage(resetReplica());
				replyToFront(replyToFe);
				break;
		


				//need to add a default here in case of bugs......
		}
		}catch(Exception e){
			System.err.println("Error in Replica 1");
			replyToFront(prepareReturnPackage("ERROR"));
		}


		
	}
    
	//////reset here
    public String resetReplica() {
    	System.out.println("resetting replica data");
    	return "";
    }
    
    
    
    ///////////////////////////////////// methods here
	
	  public boolean checkCred(String AdminID, String code) throws Exception {
	        if (code.equals("MTLA")) {
	          return (mtlObj.checkCred(AdminID));
	        }
	        if (code.equals("SHEA")) {
	            return (sheObj.checkCred(AdminID));

	        }
	        if (code.equals("QUEA")) {
	            return (queObj.checkCred(AdminID));

	        }
	        return false;
	    }

    private boolean swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType) throws Exception {
        System.out.println("In client swap");
       
        boolean res=false;
        if(patientID.substring(0,3).equals("MTL")){     
               res = mtlObj.swapAppointment(patientID, oldAppointmentID, oldAppointmentType,newAppointmentID, newAppointmentType);
        }
        if(patientID.substring(0,3).equals("QUE")){
            res = queObj.swapAppointment(patientID, oldAppointmentID, oldAppointmentType,newAppointmentID, newAppointmentType);	
        }
        if(patientID.substring(0,3).equals("SHE")){
            res = sheObj.swapAppointment(patientID, oldAppointmentID, oldAppointmentType,newAppointmentID, newAppointmentType);
                System.out.println("RESULT = " + res);
        }
        if(res) {
        	System.out.println("Success! Appointment " + oldAppointmentID+ "was swapped with " + newAppointmentID );
        	
        }
        else {
        	System.out.println("Failed to swap appointments!");
        	
        }
        
        return res;
    }

    public String bookAppointment(String patientID, String appointmentID, String appointmentType) throws Exception {
    	System.out.println(patientID);
    	System.out.println(appointmentID);
    	System.out.println(appointmentType);
    	
    	
    	String responce = null;
    	// THIS IS WRONG, WE NEED TO REQUESTED LOCATION, NOT THE PATIENT HOME LOC...fixed
    	String loc= appointmentID.substring(0, 3);
    	System.out.println("loc ="+loc);
        if (loc.equals("MTL")) {
           
                responce = mtlObj.bookAppointment(patientID, appointmentID, appointmentType);
                System.out.println(responce);
      
        }
        if (loc.equals("SHE")) {
           
         
                responce = sheObj.bookAppointment(patientID, appointmentID, appointmentType);
                System.out.println(responce);
               
            
        }
        if (loc.equals("QUE")) {
       
                responce = queObj.bookAppointment(patientID, appointmentID, appointmentType);
                System.out.println(responce);
               
        }
        return responce;
    }

    public String getAppointmentSchedule(String patientID) throws Exception {
        String build = "";
        if (!mtlObj.getAppointmentSchedule(patientID).startsWith("Y")) {
		    build = "You have the following appointments in Montreal:\n" + mtlObj.getAppointmentSchedule(patientID);
		}
		if (!queObj.getAppointmentSchedule(patientID).startsWith("Y")) {
		    build = build + "You have the following appointments in Quebec:\n"
		            + queObj.getAppointmentSchedule(patientID);
		}
		if (!sheObj.getAppointmentSchedule(patientID).startsWith("Y")) {
		    build = build + "You have the following appointments in Sherbrooke:\n"
		            + sheObj.getAppointmentSchedule(patientID);
		}
		if (build == "") {
		    System.out.println("You have nothing booked");
		    return "You have nothing booked";
		}
		System.out.println(build);
		
        
        return build;
    }

    public String cancelAppointment(String patientID,String appointmentID,String appointmentType) throws Exception {
        String loc = appointmentID.substring(0, 3);
        String responce = null;
//		System.out.println("Inside cancel Model");
//		System.out.println("loc " + loc);
//		System.out.println("loc == MTL " + loc == "MTL");
//		System.out.println("loc.equals(\"MTL\")" + loc.equals("MTL"));
        if (loc.equals("MTL")) {
       //System.out.println("response: " + mtlObj.cancelAppointment(patientID, appointmentID));
                responce = mtlObj.cancelAppointment(patientID, appointmentID,appointmentType);
                System.out.println("\n" + responce);
     
        }
        if (loc.equals("SHE")) {
           
                //System.out.println("response: " + sheObj.cancelAppointment(patientID, appointmentID));
                responce = sheObj.cancelAppointment(patientID, appointmentID,appointmentType);
                System.out.println("\n" + responce);
        }
            
        if (loc.equals("QUE")) {
            //System.out.println("response: " + queObj.cancelAppointment(patientID, appointmentID));
                responce = queObj.cancelAppointment(patientID, appointmentID,appointmentType);
                System.out.println("\n" + responce);
        }
        
        return responce;

    }

    public String listAppointmentAvailability(String appointmentType) throws Exception {
    	//String loc = patientID.substring(0, 3); 
    	
    	String responce = "";
            responce = mtlObj.listAppointmentAvailability(appointmentType);
			
//			responce =responce + "\n"+ sheObj.listAppointmentAvailability(appointmentType);
//			
//        	responce =responce + "\n"+queObj.listAppointmentAvailability(appointmentType);
			
		
        return responce;
    }
    
//    public String listAppointmentAvailability(String appointmentType) {
//        String response= mtlObj.listAppointmentAvailability(appointmentType);
//        return response;
//     }

    //admin
    public String addAppointment(String appointmentID, String appointmentType, int capcity)throws Exception {
    	String loc = appointmentID.substring(0, 3); 
    	String response =null;
        if (loc.equals("MTL")) {
        	response= mtlObj.addAppointment(appointmentID, appointmentType, capcity);
        }

        if (loc.equals("SHE")) {

        	response= sheObj.addAppointment(appointmentID, appointmentType, capcity);

        }
        if (loc.equals("QUE")) {

        	response= queObj.addAppointment(appointmentID, appointmentType, capcity);
        }
        
        return response;
    }

    //admin
    public String removeAppointment(String appointmentID, String appointmentType) throws Exception {
        String loc = appointmentID.substring(0, 3);
        String response =null;
        if (loc.equals("MTL")) {
        	response= mtlObj.removeAppointment(appointmentID, appointmentType);
        }
        if (loc.equals("SHE")) {
        	response=sheObj.removeAppointment(appointmentID, appointmentType);

        }
        if (loc.equals("QUE")) {
        	response= queObj.removeAppointment(appointmentID, appointmentType);
        }
        return response;
    }


	///////////////////////////////////
	public static boolean compareStdMap(Map<String,ArrayList<String>>map1, Map<String,ArrayList<String>> map2){

		if(map1.size() != map2.size()){
			return false;
		}

		for (Map.Entry mapElement : map1.entrySet()) {
			String key = (String)mapElement.getKey(); //traverse all key

			ArrayList<String> list1 = map1.get(key);
			ArrayList<String> list2 = map2.get(key);
			Collections.sort(list1);  //sort the list
			Collections.sort(list2);

			if(list1.size() != list2.size()){
				return false;
			}
			for (int i = 0; i < list1.size(); i++) {
				if(!list1.get(i).equals(list2.get(i))){  //compare the list
					return false;
				}
			}
		}
		return true;
	}


	public static boolean compareTwoReplicasMap(StdMaps r1, StdMaps r2){

//		if(compareStdMap(r1.stdPhysicianMTL,r2.stdPhysicianMTL) && compareStdMap(r1.stdSurgeonMTL, r2.stdSurgeonMTL) && compareStdMap(r1.stdDentalMTL,r2.stdDentalMTL)
//				&& compareStdMap(r1.stdPhysicianQUE,r2.stdPhysicianQUE) && compareStdMap(r1.stdSurgeonQUE, r2.stdSurgeonQUE) && compareStdMap(r1.stdDentalQUE,r2.stdDentalQUE)
//				&& compareStdMap(r1.stdPhysicianSHE,r2.stdPhysicianSHE) && compareStdMap(r1.stdSurgeonSHE, r2.stdSurgeonSHE) && compareStdMap(r1.stdDentalSHE,r2.stdDentalSHE)){
//			return true;
//		} else {
			return false;
//		}

	}

	public String failedReplica(StdMaps localMaps, StdMaps remote1, StdMaps remote2){
		if(compareTwoReplicasMap(localMaps,remote1) && compareTwoReplicasMap(localMaps, remote2) && compareTwoReplicasMap(remote1,remote2)){
			return "No Fail";
		} else if(compareTwoReplicasMap(localMaps, remote1)){
			return "Remote2 Fail";
		} else if(compareTwoReplicasMap(localMaps, remote2)){
			return "Remote1 Fail";
		} else if(compareTwoReplicasMap(remote1, remote2)){
			return "LocalMaps Fail";
		}
		return "Warning: more than 2 replicas fail";
	}

	public void detectFailAndRecover(){
		StdMaps stdRemoteMap1 = null;
		StdMaps stdRemoteMap2 = null;
		StdMaps stdLocalMap = stdMaps;

		int remotePort1 = 9898;
		int remotePort2 = 7676;

		try {
			stdRemoteMap1 = UDPRm.getRemoteStdMaps(remotePort1);
			stdRemoteMap2 = UDPRm.getRemoteStdMaps(remotePort2);
		} catch (Exception e){
			e.printStackTrace();
		}

		String failedStr = failedReplica(stdLocalMap, stdRemoteMap1, stdRemoteMap2);
		System.out.println(failedStr);
		if(failedStr.equals("Remote1 Fail")){

			try{
				UDPRm.recoverRemoteMaps(remotePort1,stdLocalMap);
			}catch (Exception e){
				e.printStackTrace();
			}
		}else if(failedStr.equals("Remote2 Fail")){
			try{
				UDPRm.recoverRemoteMaps(remotePort2,stdLocalMap);
			}catch (Exception e){
				e.printStackTrace();
			}
		}else if(failedStr.equals("No Fail")){
			//do nothing
		} else {
			System.out.println("Warning: fail to recover the replica");
		}

	}

	public void setStdPhysicianQue(){

		Map<String, ArrayList<String>> physicianQue = new HashMap<>();
		HashMap<String, Integer> uniqeDocQue = QueServerObj.getDocMap();

		HashMap<String, QueBookingDb> usersQue = QueServerObj.getUsers();

		for (Map.Entry mapElement : uniqeDocQue.entrySet()) {
			ArrayList<String> docListQue = new ArrayList<>();
			String key = (String)mapElement.getKey(); //key is appointmentID
			docListQue.add(Integer.toString(uniqeDocQue.get(key)));

			if(usersQue.size() != 0){
				for (Map.Entry patient: usersQue.entrySet()){
					HashMap<String, String> appMap = ((QueBookingDb)patient.getValue()).getAppMap();
					if(appMap.containsKey(key) && appMap.get(key).equalsIgnoreCase("Physician")){
						docListQue.add(((QueBookingDb)patient.getValue()).getPatID());
					}
				}
			}
			physicianQue.put(key,docListQue);
		}
		stdMaps.setStdPhysicianQUE(physicianQue);
	}

	public void setStdPhysicianMtl(){
		Map<String, ArrayList<String>> physicianMtl = new HashMap<>();
		HashMap<String, Integer> uniqeDocMtl = MtlServerObj.getDocMap();
		HashMap<String, MtlBookingDb> usersMtl = MtlServerObj.getUsers();

		for (Map.Entry mapElement : uniqeDocMtl.entrySet()) {
			String key = (String)mapElement.getKey();
			ArrayList<String> docListMtl = new ArrayList<>();//key is appointmentID
			docListMtl.add(Integer.toString(uniqeDocMtl.get(key)));

			if(usersMtl.size() != 0){
				for (Map.Entry patient: usersMtl.entrySet()){
					HashMap<String, String> appMap = ((MtlBookingDb)patient.getValue()).getAppMap();
					if(appMap.containsKey(key) && appMap.get(key).equalsIgnoreCase("Physician")){
						docListMtl.add(((MtlBookingDb)patient.getValue()).getPatID());
					}
				}
			}
			physicianMtl.put(key,docListMtl);
		}
		stdMaps.setStdPhysicianMTL(physicianMtl);
	}

	public void setStdPhysicianShe(){
		Map<String, ArrayList<String>> physicianShe = new HashMap<>();
		HashMap<String, Integer> uniqeDocShe = SheServerObj.getDocMap();

		HashMap<String, SheBookingDb> usersShe = SheServerObj.getUsers();

		for (Map.Entry mapElement : uniqeDocShe.entrySet()) {
			ArrayList<String> docListShe = new ArrayList<>();
			String key = (String)mapElement.getKey(); //key is appointmentID
			docListShe.add(Integer.toString(uniqeDocShe.get(key)));

			if(usersShe.size() != 0){
				for (Map.Entry patient: usersShe.entrySet()){
					HashMap<String, String> appMap = ((SheBookingDb)patient.getValue()).getAppMap();
					if(appMap.containsKey(key) && appMap.get(key).equalsIgnoreCase("Physician")){
						docListShe.add(((SheBookingDb)patient.getValue()).getPatID());
					}
				}
			}
			physicianShe.put(key,docListShe);
		}
		stdMaps.setStdPhysicianSHE(physicianShe);
	}

	public void setStdDentistQue(){
		Map<String, ArrayList<String>> dentistQue = new HashMap<>();
		HashMap<String, Integer> uniqeDentQue = QueServerObj.getDentMap();

		HashMap<String, QueBookingDb> usersDentQue = QueServerObj.getUsers();

		for (Map.Entry mapElement : uniqeDentQue.entrySet()) {
			ArrayList<String> dentListQue = new ArrayList<>();
			String key = (String)mapElement.getKey(); //key is appointmentID
			dentListQue.add(Integer.toString(uniqeDentQue.get(key)));

			if(usersDentQue.size() != 0){
				for (Map.Entry patient: usersDentQue.entrySet()){
					HashMap<String, String> appMap = ((QueBookingDb)patient.getValue()).getAppMap();
					if(appMap.containsKey(key) && appMap.get(key).equalsIgnoreCase("Dentist")){
						dentListQue.add(((QueBookingDb)patient.getValue()).getPatID());
					}
				}
			}
			dentistQue.put(key,dentListQue);
		}
		stdMaps.setStdDentalQUE(dentistQue);
	}

	public void setStdDentistMtl(){
		Map<String, ArrayList<String>> dentistMtl = new HashMap<>();
		HashMap<String, Integer> uniqeDentMtl = MtlServerObj.getDentMap();

		HashMap<String, MtlBookingDb> usersDentMtl = MtlServerObj.getUsers();

		for (Map.Entry mapElement : uniqeDentMtl.entrySet()) {
			ArrayList<String> dentListMtl = new ArrayList<>();
			String key = (String)mapElement.getKey(); //key is appointmentID
			dentListMtl.add(Integer.toString(uniqeDentMtl.get(key)));

			if(usersDentMtl.size() != 0){
				for (Map.Entry patient: usersDentMtl.entrySet()){
					HashMap<String, String> appMap = ((MtlBookingDb)patient.getValue()).getAppMap();
					if(appMap.containsKey(key) && appMap.get(key).equalsIgnoreCase("Dentist")){
						dentListMtl.add(((MtlBookingDb)patient.getValue()).getPatID());
					}
				}
			}
			dentistMtl.put(key,dentListMtl);
		}
		stdMaps.setStdDentalMTL(dentistMtl);
	}

	public void setStdDentistShe(){
		Map<String, ArrayList<String>> dentistShe = new HashMap<>();
		HashMap<String, Integer> uniqeDentShe = SheServerObj.getDentMap();

		HashMap<String, SheBookingDb> usersDentShe = SheServerObj.getUsers();

		for (Map.Entry mapElement : uniqeDentShe.entrySet()) {
			ArrayList<String> dentListShe = new ArrayList<>();
			String key = (String)mapElement.getKey(); //key is appointmentID
			dentListShe.add(Integer.toString(uniqeDentShe.get(key)));

			if(usersDentShe.size() != 0){
				for (Map.Entry patient: usersDentShe.entrySet()){
					HashMap<String, String> appMap = ((SheBookingDb)patient.getValue()).getAppMap();
					if(appMap.containsKey(key) && appMap.get(key).equalsIgnoreCase("Dentist")){
						dentListShe.add(((SheBookingDb)patient.getValue()).getPatID());
					}
				}
			}
			dentistShe.put(key,dentListShe);
		}
		stdMaps.setStdDentalSHE(dentistShe);
	}

	public void setStdSurgeonQue(){
		Map<String, ArrayList<String>> surgeonQue = new HashMap<>();
		HashMap<String, Integer> uniqeSurgeonQue = QueServerObj.getSurgMap();

		HashMap<String, QueBookingDb> usersSurgeonQue = QueServerObj.getUsers();

		for (Map.Entry mapElement : uniqeSurgeonQue.entrySet()) {
			ArrayList<String> surgeonListQue = new ArrayList<>();
			String key = (String)mapElement.getKey(); //key is appointmentID
			surgeonListQue.add(Integer.toString(uniqeSurgeonQue.get(key)));

			if(usersSurgeonQue.size() != 0){
				for (Map.Entry patient: usersSurgeonQue.entrySet()){
					HashMap<String, String> appMap = ((QueBookingDb)patient.getValue()).getAppMap();
					if(appMap.containsKey(key) && appMap.get(key).equalsIgnoreCase("Surgeon")){
						surgeonListQue.add(((QueBookingDb)patient.getValue()).getPatID());
					}
				}
			}
			surgeonQue.put(key,surgeonListQue);
		}
		stdMaps.setStdSurgeonQUE(surgeonQue);
	}
	public void setStdSurgeonMtl(){
		Map<String, ArrayList<String>> surgeonMtl = new HashMap<>();
		HashMap<String, Integer> uniqeSurgeonMtl = MtlServerObj.getSurgMap();

		HashMap<String, MtlBookingDb> usersSurgeonMtl = MtlServerObj.getUsers();

		for (Map.Entry mapElement : uniqeSurgeonMtl.entrySet()) {
			ArrayList<String> surgeonListMtl = new ArrayList<>();
			String key = (String)mapElement.getKey(); //key is appointmentID
			surgeonListMtl.add(Integer.toString(uniqeSurgeonMtl.get(key)));

			if(usersSurgeonMtl.size() != 0){
				for (Map.Entry patient: usersSurgeonMtl.entrySet()){
						HashMap<String, String> appMap = ((MtlBookingDb)patient.getValue()).getAppMap();
						if(appMap.containsKey(key) && appMap.get(key).equalsIgnoreCase("Surgeon")){
							surgeonListMtl.add(((MtlBookingDb)patient.getValue()).getPatID());
						}
				}
			}
			surgeonMtl.put(key,surgeonListMtl);
		}
		stdMaps.setStdSurgeonMTL(surgeonMtl);

	}

	public void setStdSurgeonShe(){
		Map<String, ArrayList<String>> surgeonShe = new HashMap<>();
		HashMap<String, Integer> uniqeSurgeonShe = SheServerObj.getSurgMap();

		HashMap<String, SheBookingDb> usersSurgeonShe = SheServerObj.getUsers();

		for (Map.Entry mapElement : uniqeSurgeonShe.entrySet()) {
			ArrayList<String> surgeonListShe = new ArrayList<>();
			String key = (String)mapElement.getKey(); //key is appointmentID
			surgeonListShe.add(Integer.toString(uniqeSurgeonShe.get(key)));

			if(usersSurgeonShe.size() != 0){
				for (Map.Entry patient: usersSurgeonShe.entrySet()){
					HashMap<String, String> appMap = ((SheBookingDb)patient.getValue()).getAppMap();
					if(appMap.containsKey(key) && appMap.get(key).equalsIgnoreCase("Surgeon")){
						surgeonListShe.add(((SheBookingDb)patient.getValue()).getPatID());
					}
				}
			}
			surgeonShe.put(key,surgeonListShe);
		}
		stdMaps.setStdSurgeonSHE(surgeonShe);

	}

	public void setStdMapsFromUniqueMaps(){
		setStdDentistMtl();
		setStdPhysicianMtl();
		setStdSurgeonMtl();

		setStdDentistQue();
		setStdPhysicianQue();
		setStdSurgeonQue();

		setStdDentistShe();
		setStdPhysicianShe();
		setStdSurgeonShe();
	}



	public StdMaps getStdMaps() {
		return stdMaps;
	}

	public void setStdMaps(StdMaps stdMaps) {
		this.stdMaps = stdMaps;
	}

	public void setUniueMapMtl(StdMaps map){
		Map<String, ArrayList<String>> stdPhysicianMTL = map.getStdPhysicianMTL();
		HashMap<String, Integer> mapUniquePhysicianMTL = new HashMap<String, Integer>();
		HashMap<String, MtlBookingDb> usersMTL = new HashMap<>();

		for (Map.Entry mapElement : stdPhysicianMTL.entrySet()){
			String key = (String)mapElement.getKey(); //key is appointmentID
			ArrayList<String> list = stdPhysicianMTL.get(key);
			int capacity = Integer.valueOf(list.get(0));
			mapUniquePhysicianMTL.put(key,capacity);

			for(int i = 1; i < list.size(); i++){
				if(list.get(i) != null){
					String patientID = list.get(i);
					if(usersMTL.containsKey(patientID)){
						usersMTL.get(patientID).getAppMap().put(key,"Physician");
					} else {
						MtlBookingDb bookingDb = new MtlBookingDb(patientID);
						bookingDb.patID = patientID;
						bookingDb.getAppMap().put(key,"Physician");
						usersMTL.put(patientID,bookingDb);
					}
				}
			}
		}

		Map<String, ArrayList<String>> stdDentistMTL = map.getStdDentalMTL();
		HashMap<String, Integer> mapUniqueDentistMTL = new HashMap<String, Integer>();

		for (Map.Entry mapElement : stdDentistMTL.entrySet()){
			String key = (String)mapElement.getKey(); //key is appointmentID
			ArrayList<String> list = stdDentistMTL.get(key);
			int capacity = Integer.valueOf(list.get(0));
			mapUniqueDentistMTL.put(key,capacity);

			for(int i = 1; i < list.size(); i++){
				if(list.get(i) != null){
					String patientID = list.get(i);
					if(usersMTL.containsKey(patientID)){
						usersMTL.get(patientID).getAppMap().put(key,"Dentist");
					} else {
						MtlBookingDb bookingDb = new MtlBookingDb(patientID);
						bookingDb.patID = patientID;
						bookingDb.getAppMap().put(key, "Dentist");
						usersMTL.put(patientID, bookingDb);
					}
				}
			}
		}

		Map<String, ArrayList<String>> stdSurgeonMTL = map.getStdSurgeonMTL();
		HashMap<String, Integer> mapUniqueSurgeonMTL = new HashMap<String, Integer>();

		for (Map.Entry mapElement : stdSurgeonMTL.entrySet()){
			String key = (String)mapElement.getKey(); //key is appointmentID
			ArrayList<String> list = stdSurgeonMTL.get(key);
			int capacity = Integer.valueOf(list.get(0));
			mapUniqueSurgeonMTL.put(key,capacity);

			for(int i = 1; i < list.size(); i++){
				if(list.get(i) != null){
					String patientID = list.get(i);
					if(usersMTL.containsKey(patientID)){
						usersMTL.get(patientID).getAppMap().put(key,"Surgeon");
					} else {
						MtlBookingDb bookingDb = new MtlBookingDb(patientID);
						bookingDb.patID = patientID;
						bookingDb.getAppMap().put(key, "Surgeon");
						usersMTL.put(patientID, bookingDb);
					}
				}
			}
		}

		MtlServerObj.setDentMap(mapUniqueDentistMTL);
		MtlServerObj.setSurgMap(mapUniqueSurgeonMTL);
		MtlServerObj.setDocMap(mapUniquePhysicianMTL);
		MtlServerObj.setUsers(usersMTL);

	}

	public void setUniueMapQue(StdMaps map){
		Map<String, ArrayList<String>> stdPhysicianQue = map.getStdPhysicianQUE();
		HashMap<String, Integer> mapUniquePhysicianQue = new HashMap<String, Integer>();
		HashMap<String, QueBookingDb> usersQue = new HashMap<>();

		for (Map.Entry mapElement : stdPhysicianQue.entrySet()){
			String key = (String)mapElement.getKey(); //key is appointmentID
			ArrayList<String> list = stdPhysicianQue.get(key);
			int capacity = Integer.valueOf(list.get(0));
			mapUniquePhysicianQue.put(key,capacity);

			for(int i = 1; i < list.size(); i++){
				if(list.get(i) != null){
					String patientID = list.get(i);
					if(usersQue.containsKey(patientID)){
						usersQue.get(patientID).getAppMap().put(key,"Physician");
					} else {
						QueBookingDb bookingDb = new QueBookingDb(patientID);
						bookingDb.patID = patientID;
						bookingDb.getAppMap().put(key, "Physician");
						usersQue.put(patientID, bookingDb);
					}
				}
			}
		}

		Map<String, ArrayList<String>> stdDentistQue = map.getStdDentalQUE();
		HashMap<String, Integer> mapUniqueDentistQue = new HashMap<String, Integer>();

		for (Map.Entry mapElement : stdDentistQue.entrySet()){
			String key = (String)mapElement.getKey(); //key is appointmentID
			ArrayList<String> list = stdDentistQue.get(key);
			int capacity = Integer.valueOf(list.get(0));
			mapUniqueDentistQue.put(key,capacity);

			for(int i = 1; i < list.size(); i++){
				if(list.get(i) != null){
					String patientID = list.get(i);
					if(usersQue.containsKey(patientID)){
						usersQue.get(patientID).getAppMap().put(key,"Dentist");
					} else {
						QueBookingDb bookingDb = new QueBookingDb(patientID);
						bookingDb.patID = patientID;
						bookingDb.getAppMap().put(key, "Dentist");
						usersQue.put(patientID, bookingDb);
					}
				}
			}
		}

		Map<String, ArrayList<String>> stdSurgeonQue = map.getStdSurgeonQUE();
		HashMap<String, Integer> mapUniqueSurgeonQue = new HashMap<String, Integer>();

		for (Map.Entry mapElement : stdSurgeonQue.entrySet()){
			String key = (String)mapElement.getKey(); //key is appointmentID
			ArrayList<String> list = stdSurgeonQue.get(key);
			int capacity = Integer.valueOf(list.get(0));
			mapUniqueSurgeonQue.put(key,capacity);

			for(int i = 1; i < list.size(); i++){
				if(list.get(i) != null){
					String patientID = list.get(i);
					if(usersQue.containsKey(patientID)){
						usersQue.get(patientID).getAppMap().put(key,"Surgeon");
					} else {
						QueBookingDb bookingDb = new QueBookingDb(patientID);
						bookingDb.patID = patientID;
						bookingDb.getAppMap().put(key, "Surgeon");
						usersQue.put(patientID, bookingDb);
					}
				}
			}
		}

		QueServerObj.setDentMap(mapUniqueDentistQue);
		QueServerObj.setSurgMap(mapUniqueSurgeonQue);
		QueServerObj.setDocMap(mapUniquePhysicianQue);
		QueServerObj.setUsers(usersQue);


	}

	public void setUniueMapShe(StdMaps map){
		Map<String, ArrayList<String>> stdPhysicianShe = map.getStdPhysicianSHE();
		HashMap<String, Integer> mapUniShePhysicianShe = new HashMap<String, Integer>();
		HashMap<String, SheBookingDb> usersShe = new HashMap<>();

		for (Map.Entry mapElement : stdPhysicianShe.entrySet()){
			String key = (String)mapElement.getKey(); //key is appointmentID
			ArrayList<String> list = stdPhysicianShe.get(key);
			int capacity = Integer.valueOf(list.get(0));
			mapUniShePhysicianShe.put(key,capacity);

			for(int i = 1; i < list.size(); i++){
				if(list.get(i) != null){
					String patientID = list.get(i);
					if(usersShe.containsKey(patientID)){
						usersShe.get(patientID).getAppMap().put(key,"Physician");
					} else {
						SheBookingDb bookingDb = new SheBookingDb(patientID);
						bookingDb.patID = patientID;
						bookingDb.getAppMap().put(key, "Physician");
						usersShe.put(patientID, bookingDb);
					}
				}
			}
		}

		Map<String, ArrayList<String>> stdDentistShe = map.getStdDentalSHE();
		HashMap<String, Integer> mapUniSheDentistShe = new HashMap<String, Integer>();

		for (Map.Entry mapElement : stdDentistShe.entrySet()){
			String key = (String)mapElement.getKey(); //key is appointmentID
			ArrayList<String> list = stdDentistShe.get(key);
			int capacity = Integer.valueOf(list.get(0));
			mapUniSheDentistShe.put(key,capacity);

			for(int i = 1; i < list.size(); i++){
				if(list.get(i) != null){
					String patientID = list.get(i);
					if(usersShe.containsKey(patientID)){
						usersShe.get(patientID).getAppMap().put(key,"Dentist");
					} else {
						SheBookingDb bookingDb = new SheBookingDb(patientID);
						bookingDb.patID = patientID;
						bookingDb.getAppMap().put(key, "Dentist");
						usersShe.put(patientID, bookingDb);
					}
				}
			}
		}

		Map<String, ArrayList<String>> stdSurgeonShe = map.getStdSurgeonSHE();
		HashMap<String, Integer> mapUniSheSurgeonShe = new HashMap<String, Integer>();

		for (Map.Entry mapElement : stdSurgeonShe.entrySet()){
			String key = (String)mapElement.getKey(); //key is appointmentID
			ArrayList<String> list = stdSurgeonShe.get(key);
			int capacity = Integer.valueOf(list.get(0));
			mapUniSheSurgeonShe.put(key,capacity);

			for(int i = 1; i < list.size(); i++){
				if(list.get(i) != null){
					String patientID = list.get(i);
					if(usersShe.containsKey(patientID)){
						usersShe.get(patientID).getAppMap().put(key,"Surgeon");
					} else {
						SheBookingDb bookingDb = new SheBookingDb(patientID);
						bookingDb.patID = patientID;
						bookingDb.getAppMap().put(key, "Surgeon");
						usersShe.put(patientID, bookingDb);
					}
				}
			}
		}

		SheServerObj.setDentMap(mapUniSheDentistShe);
		SheServerObj.setSurgMap(mapUniSheSurgeonShe);
		SheServerObj.setDocMap(mapUniShePhysicianShe);
		SheServerObj.setUsers(usersShe);

	}

	public void setUniqueMap(StdMaps map){
		setUniueMapMtl(map);
		setUniueMapQue(map);
		setUniueMapShe(map);
	}

	class Listening extends Thread{
		public DatagramSocket socketSer;
		public void run(){

			try {
				socketSer = new DatagramSocket(8787);

				while (true) {
					byte[] incomingData = new byte[1024];
					DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
					socketSer.receive(incomingPacket);
					byte[] data = incomingPacket.getData();
					ByteArrayInputStream in = new ByteArrayInputStream(data);
					ObjectInputStream is = new ObjectInputStream(in);
					String str="";
					try {
						StdMaps msg = (StdMaps) is.readObject();
						str=msg.getStr();

						if(str.equalsIgnoreCase("Connect for listing")) {
							StdMaps msgSend=new StdMaps(stdMaps);

							ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
							ObjectOutput os = new ObjectOutputStream(outputStream1);
							os.writeObject(msgSend);

							InetAddress IPAddress = incomingPacket.getAddress();
							int port = incomingPacket.getPort();

							byte[] dataSend = outputStream1.toByteArray();
							DatagramPacket replyPacket =new DatagramPacket(dataSend, dataSend.length, IPAddress, port);
							socketSer.send(replyPacket);
							outputStream1.close();
							os.close();
						}
						if(str.equalsIgnoreCase("Connect for modifying")) {
							StdMaps msgSend=new StdMaps(stdMaps);

							ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
							ObjectOutput os = new ObjectOutputStream(outputStream1);
							os.writeObject(msgSend);

							InetAddress IPAddress = incomingPacket.getAddress();
							int port = incomingPacket.getPort();

							byte[] dataSend = outputStream1.toByteArray();
							DatagramPacket replyPacket =new DatagramPacket(dataSend, dataSend.length, IPAddress, port);
							socketSer.send(replyPacket);

							socketSer.receive(incomingPacket);
							byte[] dataBack = incomingPacket.getData();


							StdMaps msg1=null;
							try {
								msg1 = (StdMaps) is.readObject();
							} catch (ClassNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							//return message from server.
							stdMaps=msg1;
							setUniqueMap(stdMaps);

							System.out.println("----------------");
							stdMaps.print();

							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

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

	}






}