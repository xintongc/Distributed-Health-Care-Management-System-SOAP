package rm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StdMaps implements Serializable {

    Map<String,ArrayList<String>> stdPhysicianMTL = new HashMap<>();
    Map<String,ArrayList<String>> stdSurgeonMTL= new HashMap<>();
    Map<String,ArrayList<String>> stdDentalMTL= new HashMap<>();

    Map<String,ArrayList<String>> stdPhysicianQUE= new HashMap<>();
    Map<String,ArrayList<String>> stdSurgeonQUE= new HashMap<>();
    Map<String,ArrayList<String>> stdDentalQUE= new HashMap<>();

    Map<String,ArrayList<String>> stdPhysicianSHE= new HashMap<>();
    Map<String,ArrayList<String>> stdSurgeonSHE= new HashMap<>();
    Map<String,ArrayList<String>> stdDentalSHE= new HashMap<>();

    String str = "";

    public StdMaps(){

    }

    public StdMaps(String string){
        str = string;
    }

    public StdMaps(StdMaps maps) {
        this.stdPhysicianMTL = maps.stdPhysicianMTL;
        this.stdSurgeonMTL = maps.stdSurgeonMTL;
        this.stdDentalMTL = maps.stdDentalMTL;
        this.stdPhysicianQUE = maps.stdPhysicianQUE;
        this.stdSurgeonQUE = maps.stdSurgeonQUE;
        this.stdDentalQUE = maps.stdDentalQUE;
        this.stdPhysicianSHE = maps.stdPhysicianSHE;
        this.stdSurgeonSHE = maps.stdSurgeonSHE;
        this.stdDentalSHE = maps.stdDentalSHE;
    }

    public String getStr() {
        return str;
    }

    public void print(){
        System.out.println(stdPhysicianMTL);
        System.out.println(stdSurgeonMTL);
        System.out.println(stdDentalMTL);

        System.out.println(stdPhysicianQUE);
        System.out.println(stdSurgeonQUE);
        System.out.println(stdDentalQUE);

        System.out.println(stdPhysicianSHE);
        System.out.println(stdSurgeonSHE);
        System.out.println(stdDentalSHE);
    }

    public Map<String, ArrayList<String>> getStdPhysicianMTL() {
        return stdPhysicianMTL;
    }

    public void setStdPhysicianMTL(Map<String, ArrayList<String>> stdPhysicianMTL) {
        this.stdPhysicianMTL = stdPhysicianMTL;
    }

    public Map<String, ArrayList<String>> getStdSurgeonMTL() {
        return stdSurgeonMTL;
    }

    public void setStdSurgeonMTL(Map<String, ArrayList<String>> stdSurgeonMTL) {
        this.stdSurgeonMTL = stdSurgeonMTL;
    }

    public Map<String, ArrayList<String>> getStdDentalMTL() {
        return stdDentalMTL;
    }

    public void setStdDentalMTL(Map<String, ArrayList<String>> stdDentalMTL) {
        this.stdDentalMTL = stdDentalMTL;
    }

    public Map<String, ArrayList<String>> getStdPhysicianQUE() {
        return stdPhysicianQUE;
    }

    public void setStdPhysicianQUE(Map<String, ArrayList<String>> stdPhysicianQUE) {
        this.stdPhysicianQUE = stdPhysicianQUE;
    }

    public Map<String, ArrayList<String>> getStdSurgeonQUE() {
        return stdSurgeonQUE;
    }

    public void setStdSurgeonQUE(Map<String, ArrayList<String>> stdSurgeonQUE) {
        this.stdSurgeonQUE = stdSurgeonQUE;
    }

    public Map<String, ArrayList<String>> getStdDentalQUE() {
        return stdDentalQUE;
    }

    public void setStdDentalQUE(Map<String, ArrayList<String>> stdDentalQUE) {
        this.stdDentalQUE = stdDentalQUE;
    }

    public Map<String, ArrayList<String>> getStdPhysicianSHE() {
        return stdPhysicianSHE;
    }

    public void setStdPhysicianSHE(Map<String, ArrayList<String>> stdPhysicianSHE) {
        this.stdPhysicianSHE = stdPhysicianSHE;
    }

    public Map<String, ArrayList<String>> getStdSurgeonSHE() {
        return stdSurgeonSHE;
    }

    public void setStdSurgeonSHE(Map<String, ArrayList<String>> stdSurgeonSHE) {
        this.stdSurgeonSHE = stdSurgeonSHE;
    }

    public Map<String, ArrayList<String>> getStdDentalSHE() {
        return stdDentalSHE;
    }

    public void setStdDentalSHE(Map<String, ArrayList<String>> stdDentalSHE) {
        this.stdDentalSHE = stdDentalSHE;
    }

    public void setTestMap(){
        ArrayList arrayList = new ArrayList();
        arrayList.add("Testing");
        stdPhysicianMTL.put("MTLA123456",arrayList);

    }

    public String test(){
        return stdPhysicianMTL.get("MTLA123456").get(0);
    }

}
