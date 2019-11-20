package rm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;

public class ReplicaManager {
    static int seq=0;
    public static void main(String[] args) {

        System.out.println("Inside send Data run by thread" + Thread.currentThread().getName());
        DatagramSocket aSocket = null;

        HashMap<String,String> msgQue = new HashMap<>();

        try {
        System.out.println("Remote Manager 1 up and running");
        aSocket = new DatagramSocket(9001);
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
    public static void replyToFront(byte[] outgoing) {
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

    public static String sampleMethod(){
        return "This is my return string from sample method";
    }

    //takes a string for my code
    public static byte[] prepareReturnPackage(String returnMsg){
        byte[] pkg;
        pkg = (seq +" " + returnMsg).getBytes();
        return pkg;
    }

    //takes a boolean for your code
    public static byte[] prepareReturnPackage(boolean returnMsg){
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


    public static void unPack(String incomeReq){
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
                replyToFe = prepareReturnPackage(bookAppointment(msg[1],msg[2],msg[3]));
                replyToFront(replyToFe);

                break;
            case "cancelAppointment":
                //not the same
                System.out.println("cancelAppointment");
                replyToFe = prepareReturnPackage(cancelAppointment(msg[1],msg[2]));
                replyToFront(replyToFe);
                break;
            case "swapAppointment":
                System.out.println("swapAppointment");
                replyToFe = prepareReturnPackage(swapAppointment(msg[1],msg[2],msg[3],msg[4],msg[5]));
                replyToFront(replyToFe);
                break;
            case "addAppointemnt":
                System.out.println("addAppointemnt");
                //not the same
                replyToFe = prepareReturnPackage(addAppointment(msg[1],msg[2],msg[3], Integer.valueOf(msg[4])));
                replyToFront(replyToFe);
                break;
            case "checkAppointment":
                System.out.println("checkAppointment");
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


        }

    }


    public static String bookAppointment(String patientID, String appointmentID, String appointmentType){
        return "ok";
    }

    public static String getAppointmentSchedule (String patientID){
        return "ok";
    }

    public static String cancelAppointment(String patientID,String appointmentID){
        return "ok";
    }

    public static void sendData(){

    }

    public static String recvData(String appointmentType){
        return "ok";
    }

    public static String showSlots(String appointmentType){
        return "ok";
    }

    public static String addAppointment(String adminID, String appointmentID,String appointmentType, int capacity){
        return "ok";
    }

    public static String removeAppointment (String appointmentID, String appointmentType){
        return "ok";
    }

    public static String listAppointmentAvailability (String appointmentType){
        return "ok";
    }

    public static boolean checkCred(String adminID){
        return true;
    }

    public static String getAppList(String locReq, String patID){
        return "ok";
    }

    public static boolean isBookableExt(String patLoc, String ID, String type, String patId){
        return true;
    }

    public static boolean swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType){
        return true;
    }


    public boolean compareStdMap(Map<String,ArrayList<String>>map1, Map<String,ArrayList<String>> map2){

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


    public boolean compareTwoReplicas(Replica r1, Replica r2){
        if(compareStdMap(r1.stdPhysicianMTL,r2.stdPhysicianMTL) && compareStdMap(r1.stdSurgeonMTL, r2.stdSurgeonMTL) && compareStdMap(r1.stdDentalMTL,r2.stdDentalMTL)
        && compareStdMap(r1.stdPhysicianQUE,r2.stdPhysicianQUE) && compareStdMap(r1.stdSurgeonQUE, r2.stdSurgeonQUE) && compareStdMap(r1.stdDentalQUE,r2.stdDentalQUE)
        && compareStdMap(r1.stdPhysicianSHE,r2.stdPhysicianSHE) && compareStdMap(r1.stdSurgeonSHE, r2.stdSurgeonSHE) && compareStdMap(r1.stdDentalSHE,r2.stdDentalSHE)){
            return true;
        } else {
            return false;
        }
    }

    public String detectFail(Replica r1, Replica r2, Replica r3){
        if(compareTwoReplicas(r1,r2) && compareTwoReplicas(r1, r3) && compareTwoReplicas(r2,r3)){
            return "No Fail";
        } else if(compareTwoReplicas(r1, r2)){ // if r1 and r2 are same, means r3 fail
            return "Replica3 Fail";
        } else if(compareTwoReplicas(r1, r3)){
            return "Replica2 Fail";
        } else if(compareTwoReplicas(r2, r3)){
            return "Replica1 Fail";
        }
        return "Warning: more than 2 replicas fail";
    }

    public void recover(String failedReplica){


    }

    private void testCompare(){
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



}
