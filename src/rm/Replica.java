package rm;

import Server.MTLServer;
import Server.QUEServer;
import Server.SHEServer;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Replica {


    StdMaps stdMaps;

    MTLServer mtlServer;
    QUEServer queServer;
    SHEServer sheServer;

    Listening listening = new Listening();

    Replica(){

        stdMaps = new StdMaps();
        mtlServer = new MTLServer();
        queServer = new QUEServer();
        sheServer = new SHEServer();

        setStdMapsFromUniqueMaps();

        listening.start();
    }


    public void setStdMapsFromUniqueMaps(){
        stdMaps.stdPhysicianMTL = getPhysicianMap(mtlServer.getMap());
        stdMaps.stdSurgeonMTL = getSurgeonMap(mtlServer.getMap());
        stdMaps.stdDentalMTL = getDentalMap(mtlServer.getMap());

        stdMaps.stdPhysicianQUE = getPhysicianMap(queServer.getMap());
        stdMaps.stdSurgeonQUE = getPhysicianMap(queServer.getMap());
        stdMaps.stdDentalQUE = getPhysicianMap(queServer.getMap());

        stdMaps.stdPhysicianSHE = getPhysicianMap(sheServer.getMap());
        stdMaps.stdSurgeonSHE = getPhysicianMap(sheServer.getMap());
        stdMaps.stdDentalSHE = getPhysicianMap(sheServer.getMap());
    }

    public StdMaps getStdMaps() {
        return stdMaps;
    }

    public void setStdMaps(StdMaps stdMaps) {
        this.stdMaps = stdMaps;
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
        mtlServer.setMap(mtlMap);

        Map<String, Map<String,ArrayList<String>>> queMap = new HashMap<>();
        queMap.put("Physician",stdMaps.stdPhysicianQUE);
        queMap.put("Surgeon",stdMaps.stdSurgeonQUE);
        queMap.put("Dental",stdMaps.stdDentalQUE);
        queServer.setMap(queMap);

        Map<String, Map<String,ArrayList<String>>> sheMap = new HashMap<>();
        sheMap.put("Physician",stdMaps.stdPhysicianSHE);
        sheMap.put("Surgeon",stdMaps.stdSurgeonSHE);
        sheMap.put("Dental",stdMaps.stdDentalSHE);
        sheServer.setMap(sheMap);

    }


    class Listening extends Thread {
        public DatagramSocket socketSer;

        public void run(){
            try{
                socketSer = new DatagramSocket(0000);

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
                        str = msg.getStr();

                        if(str.equals("Connect for listing")){
                            StdMaps msgSend = new StdMaps(stdMaps);
                            ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
                            ObjectOutput os = new ObjectOutputStream(outputStream1);
                            os.writeObject(msgSend);

                            InetAddress IPAddress = incomingPacket.getAddress();
                            int port = incomingPacket.getPort();

                            byte[] dataSend = outputStream1.toByteArray();
                            DatagramPacket replyPacket = new DatagramPacket(dataSend, dataSend.length, IPAddress, port);
                            socketSer.send(replyPacket);
                            outputStream1.close();
                            os.close();
                        }

                        if(str.equals("Connect for modifying")){

                            StdMaps msgSend = new StdMaps(stdMaps);
                            ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
                            ObjectOutput os = new ObjectOutputStream(outputStream1);
                            os.writeObject(msgSend);

                            InetAddress IPAddress = incomingPacket.getAddress();
                            int port = incomingPacket.getPort();

                            byte[] dataSend = outputStream1.toByteArray();
                            DatagramPacket replyPacket = new DatagramPacket(dataSend, dataSend.length, IPAddress, port);
                            socketSer.send(replyPacket);
                            byte[] dataBack = incomingPacket.getData();

                            StdMaps msg1 = null;
                            try {
                                msg1 = (StdMaps) is.readObject();
                            } catch (ClassNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            stdMaps = msg1;

                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        }


                    } catch (SocketException e) {
                        e.printStackTrace();

                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }

            }catch(Exception e){
                e.printStackTrace();
            }

        }


    }




}
