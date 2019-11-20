package rm;

import Server.MTLServer;
import Server.QUEServer;
import Server.SHEServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Replica {

    Map<String,ArrayList<String>> stdPhysicianMTL;
    Map<String,ArrayList<String>> stdSurgeonMTL;
    Map<String,ArrayList<String>> stdDentalMTL;

    Map<String,ArrayList<String>> stdPhysicianQUE;
    Map<String,ArrayList<String>> stdSurgeonQUE;
    Map<String,ArrayList<String>> stdDentalQUE;

    Map<String,ArrayList<String>> stdPhysicianSHE;
    Map<String,ArrayList<String>> stdSurgeonSHE;
    Map<String,ArrayList<String>> stdDentalSHE;


    MTLServer mtlServer;
    QUEServer queServer;
    SHEServer sheServer;


    Replica(){

        stdPhysicianMTL = new HashMap<>();
        stdSurgeonMTL = new HashMap<>();
        stdDentalMTL = new HashMap<>();

        stdPhysicianQUE = new HashMap<>();
        stdSurgeonQUE = new HashMap<>();
        stdDentalQUE = new HashMap<>();

        stdPhysicianSHE = new HashMap<>();
        stdSurgeonSHE = new HashMap<>();
        stdDentalSHE = new HashMap<>();


        mtlServer = new MTLServer();
        queServer = new QUEServer();
        sheServer = new SHEServer();

        setStdMaps();
    }


    public void setStdMaps(){
        stdPhysicianMTL = getPhysicianMap(mtlServer.getMap());
        stdSurgeonMTL = getSurgeonMap(mtlServer.getMap());
        stdDentalMTL = getDentalMap(mtlServer.getMap());

        stdPhysicianQUE = getPhysicianMap(queServer.getMap());
        stdSurgeonQUE = getPhysicianMap(queServer.getMap());
        stdDentalQUE = getPhysicianMap(queServer.getMap());

        stdPhysicianSHE = getPhysicianMap(sheServer.getMap());
        stdSurgeonSHE = getPhysicianMap(sheServer.getMap());
        stdDentalSHE = getPhysicianMap(sheServer.getMap());
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


    public void setMTLMap(Map<String,ArrayList<String>> stdPhysicianMTL, Map<String,ArrayList<String>> stdSurgeonMTL, Map<String,ArrayList<String>> stdDentalMTL){
        Map<String, Map<String,ArrayList<String>>> map = new HashMap<>();
        map.put("Physician",stdPhysicianMTL);
        map.put("Surgeon",stdSurgeonMTL);
        map.put("Dental",stdDentalMTL);
        mtlServer.setMap(map);
    }

    public void setQUEMap(Map<String,ArrayList<String>> stdPhysicianQUE, Map<String,ArrayList<String>> stdSurgeonQUE, Map<String,ArrayList<String>> stdDentalQUE){
        Map<String, Map<String,ArrayList<String>>> map = new HashMap<>();
        map.put("Physician",stdPhysicianQUE);
        map.put("Surgeon",stdSurgeonQUE);
        map.put("Dental",stdDentalQUE);
        queServer.setMap(map);
    }

    public void setSHEMap(Map<String,ArrayList<String>> stdPhysicianSHE, Map<String,ArrayList<String>> stdSurgeonSHE, Map<String,ArrayList<String>> stdDentalSHE){
        Map<String, Map<String,ArrayList<String>>> map = new HashMap<>();
        map.put("Physician",stdPhysicianSHE);
        map.put("Surgeon",stdSurgeonSHE);
        map.put("Dental",stdDentalSHE);
        sheServer.setMap(map);
    }




}
