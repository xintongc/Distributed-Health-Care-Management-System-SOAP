package rm;

import Server.MTLServer;
import Server.QUEServer;
import Server.SHEServer;
import Server.ServerInterface;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ReplicaManager{
    StdMaps stdMaps;
    static int seq=0;
    boolean isAdmin;

    ServerInterface mtlObj = null;
    ServerInterface sheObj = null;
    ServerInterface queObj = null;


    Listening listening = new Listening();

    public ReplicaManager(){

        stdMaps = new StdMaps();

        try {

            URL urlMtl = new URL("http://localhost:9999/hw?wsdl");
            QName mtlQName = new QName("http://Server/","MTLServerService");
            Service mtlService = Service.create(urlMtl, mtlQName);
            mtlObj = mtlService.getPort(ServerInterface.class);

            URL urlQue = new URL("http://localhost:8888/hw?wsdl");
            QName queQName = new QName("http://Server/","QUEServerService");
            Service queService = Service.create(urlQue, queQName);
            queObj = queService.getPort(ServerInterface.class);

            URL urlShe = new URL("http://localhost:7777/hw?wsdl");
            QName sheQName = new QName("http://Server/","SHEServerService");
            Service sheService = Service.create(urlShe, sheQName);
            sheObj = sheService.getPort(ServerInterface.class);

//			System.out.println(mtlObj.test());
//			String name = "prasad";
            //System.out.println(server.sayHello(name));

            setStdMapsFromUniqueMaps();
            listening.start();

            Thread t1=new Thread();
//            Thread t1=new Thread(this); //can not have this
            t1.setName("This is frontEnd default thread");
            t1.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println("Replica Manager Running");
        ReplicaManager replicaManager = new ReplicaManager();
//        replicaManager.stdMaps.setTestMap();
    }

    public void run() {
        revFromMulti();
    }

    public void revFromMulti() {
        // TODO Auto-generated method stub
        System.out.println("Inside send Data run by thread" + Thread.currentThread().getName());
        MulticastSocket aSocket = null;

        HashMap<String,String> msgQue = new HashMap<>();

        try {
            System.out.println("Remote Manager 1 up and running");
            aSocket = new MulticastSocket(9001);
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

            InetAddress aHost = InetAddress.getByName("localhost");
            ///send and receive reply from Sherbrooke
            int seqPort = 5500;
            DatagramPacket request = new DatagramPacket(outgoing, outgoing.length, aHost, seqPort);
            aSocket.send(request);
            System.out.println("######## replyToFront"+FEseq+" replyToFront########");
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
                replyToFe = prepareReturnPackage(addAppointment(msg[1],msg[2],msg[3]));
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
//			case "showSlots":
//				System.out.println("showSlots case");
//				System.out.println("msg[1] =" +msg[1]+"append");
//				System.out.println("msg[2] =" +msg[2]+"append");
//				String res = showSlots(msg[1],msg[2]);
//				replyToFe = prepareReturnPackage(res);
//				//replyToFe = prepareReturnPackage(showSlots("SHEP","Physician"));
//				replyToFront(replyToFe);
//				break;
//            case "checkCred":
//                System.out.println("checkCred");
//                replyToFe = prepareReturnPackage(checkCred(msg[1],msg[2]));
//                replyToFront(replyToFe);
//                break;


            //need to add a default here in case of bugs......

        }

    }

//
//    public boolean checkCred(String AdminID, String code) {
//        if (code.equals("MTLA")) {
//            return (mtlObj.checkCred(AdminID));
//        }
//        if (code.equals("SHEA")) {
//            return (sheObj.checkCred(AdminID));
//
//        }
//        if (code.equals("QUEA")) {
//            return (queObj.checkCred(AdminID));
//
//        }
//        return false;
//    }

    private boolean swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType){
        System.out.println("In client swap");

        boolean res=false;
        try{
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

        }catch (Exception e){
            e.printStackTrace();
        }


        return res;
    }

    public String bookAppointment(String patientID, String appointmentID, String appointmentType) {
        System.out.println(patientID);
        System.out.println(appointmentID);
        System.out.println(appointmentType);


        boolean responce = false;
        // THIS IS WRONG, WE NEED TO REQUESTED LOCATION, NOT THE PATIENT HOME LOC...fixed
        String loc= appointmentID.substring(0, 3);
        System.out.println("loc ="+loc);
        try{
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
        }catch (Exception e){
            e.printStackTrace();
        }

        return (responce==true)?"Success":"Failed";
    }

    public String getAppointmentSchedule(String patientID) {
        String build = "";

        return build;
    }

    public String cancelAppointment(String patientID,String appointmentID,String appointmentType) {
        String loc = appointmentID.substring(0, 3);
        boolean responce = false;
        try{
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
        }catch (Exception e){
            e.printStackTrace();
        }
        return (responce==true)?"Success":"Failed";

    }

    public String listAppointmentAvailability(String appointmentType) {
        //String loc = patientID.substring(0, 3);

        String response = "";
//        responce = mtlObj.listAppointmentAvailability(appointmentType);

//			responce =responce + "\n"+ sheObj.listAppointmentAvailability(appointmentType);
//
//        	responce =responce + "\n"+queObj.listAppointmentAvailability(appointmentType);


        return response;
    }


    //admin
    public String addAppointment(String appointmentID, String appointmentType, String capcity) {
        String loc = appointmentID.substring(0, 3);
        boolean response =false;
        if (loc.equals("MTL")) {
            response= mtlObj.addAppointment(appointmentID, appointmentType, capcity);
        }

        if (loc.equals("SHE")) {

            response= sheObj.addAppointment(appointmentID, appointmentType, capcity);

        }
        if (loc.equals("QUE")) {

            response= queObj.addAppointment(appointmentID, appointmentType, capcity);
        }

        return (response==true)?"Success":"Failed";
    }

    //admin
    public String removeAppointment(String appointmentID, String appointmentType) {
        String loc = appointmentID.substring(0, 3);
        boolean response =false;
        if (loc.equals("MTL")) {
            response= mtlObj.removeAppointment(appointmentID, appointmentType);
        }
        if (loc.equals("SHE")) {
            response=sheObj.removeAppointment(appointmentID, appointmentType);

        }
        if (loc.equals("QUE")) {
            response= queObj.removeAppointment(appointmentID, appointmentType);
        }
        return (response==true)?"Success":"Failed";
    }








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

        if(compareStdMap(r1.stdPhysicianMTL,r2.stdPhysicianMTL) && compareStdMap(r1.stdSurgeonMTL, r2.stdSurgeonMTL) && compareStdMap(r1.stdDentalMTL,r2.stdDentalMTL)
        && compareStdMap(r1.stdPhysicianQUE,r2.stdPhysicianQUE) && compareStdMap(r1.stdSurgeonQUE, r2.stdSurgeonQUE) && compareStdMap(r1.stdDentalQUE,r2.stdDentalQUE)
        && compareStdMap(r1.stdPhysicianSHE,r2.stdPhysicianSHE) && compareStdMap(r1.stdSurgeonSHE, r2.stdSurgeonSHE) && compareStdMap(r1.stdDentalSHE,r2.stdDentalSHE)){
            return true;
        } else {
            return false;
        }
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


    private void testCompareMap(){
        Map<String,ArrayList<String>> physician1 = new HashMap<>();
        Map<String,ArrayList<String>> physician2 = new HashMap<>();
        ArrayList<String> list1 = new ArrayList<>();
        ArrayList<String> list2 = new ArrayList<>();
        ArrayList<String> list3 = new ArrayList<>();
        ArrayList<String> list4 = new ArrayList<>();
        list1.add("3");list1.add("MTLP1");list1.add("QUEP2");list1.add("SHEP1");
        list2.add("3");list2.add("MTLP1");list2.add("QUEP2");list2.add("SHEP1");
        list3.add("1");list3.add("MTLP1");list3.add("QUEP2");list3.add("SHEP1");
        list4.add("1");list4.add("MTLP1");list4.add("QUEP2");list4.add("SHEP1");
        physician1.put("MTLA111111",list1);
        physician2.put("MTLA111111",list2);
        physician1.put("MTLP123123",list3);
        physician2.put("MTLP123123",list4);

        ReplicaManager replicaManager = new ReplicaManager();
        boolean result = replicaManager.compareStdMap(physician1,physician2);
        System.out.println(result);
    }


    public void detectFailAndRecover(){
        StdMaps stdRemoteMap1 = null;
        StdMaps stdRemoteMap2 = null;
        StdMaps stdLocalMap = stdMaps;

        int remotePort1 = 0002;
        int remotePort2 = 0002;

        try {
            stdRemoteMap1 = UDPRm.getRemoteStdMaps(remotePort1);
            stdRemoteMap2 = UDPRm.getRemoteStdMaps(remotePort2);
        } catch (Exception e){
            e.printStackTrace();
        }

        String failedStr = failedReplica(stdLocalMap, stdRemoteMap1, stdRemoteMap2);
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


    public void setStdMapsFromUniqueMaps(){
        stdMaps.stdPhysicianMTL = getPhysicianMap(MTLServer.getMap());
        stdMaps.stdSurgeonMTL = getSurgeonMap(MTLServer.getMap());
        stdMaps.stdDentalMTL = getDentalMap(MTLServer.getMap());

        stdMaps.stdPhysicianQUE = getPhysicianMap(QUEServer.getMap());
        stdMaps.stdSurgeonQUE = getPhysicianMap(QUEServer.getMap());
        stdMaps.stdDentalQUE = getPhysicianMap(QUEServer.getMap());

        stdMaps.stdPhysicianSHE = getPhysicianMap(SHEServer.getMap());
        stdMaps.stdSurgeonSHE = getPhysicianMap(SHEServer.getMap());
        stdMaps.stdDentalSHE = getPhysicianMap(SHEServer.getMap());
    }

    public Map<String,ArrayList<String>> getPhysicianMap(Map<String, Map<String,ArrayList<String>>> map){
        return map.get("Physician");
    }

    public Map<String,ArrayList<String>> getSurgeonMap(Map<String, Map<String,ArrayList<String>>> map){
        return map.get("Surgeon");
    }

    public Map<String,ArrayList<String>> getDentalMap(Map<String, Map<String,ArrayList<String>>> map){
        return map.get("Dental");
    }

    public void setUniqueMap(StdMaps stdMaps){
        Map<String, Map<String,ArrayList<String>>> mtlMap = new HashMap<>();
        mtlMap.put("Physician",stdMaps.stdPhysicianMTL);
        mtlMap.put("Surgeon",stdMaps.stdSurgeonMTL);
        mtlMap.put("Dental",stdMaps.stdDentalMTL);
        MTLServer.setMap(mtlMap);

        Map<String, Map<String,ArrayList<String>>> queMap = new HashMap<>();
        queMap.put("Physician",stdMaps.stdPhysicianQUE);
        queMap.put("Surgeon",stdMaps.stdSurgeonQUE);
        queMap.put("Dental",stdMaps.stdDentalQUE);
        QUEServer.setMap(queMap);

        Map<String, Map<String,ArrayList<String>>> sheMap = new HashMap<>();
        sheMap.put("Physician",stdMaps.stdPhysicianSHE);
        sheMap.put("Surgeon",stdMaps.stdSurgeonSHE);
        sheMap.put("Dental",stdMaps.stdDentalSHE);
        SHEServer.setMap(sheMap);
    }


    class Listening extends Thread{
        public DatagramSocket socketSer;
        public void run(){

            try {
                socketSer = new DatagramSocket(1000);

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
//							writeTxtServerMTL("-","-","-","-","Send DB", "Success");
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
