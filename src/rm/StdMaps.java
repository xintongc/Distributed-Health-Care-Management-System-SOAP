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

    public void setTestMap(){
        ArrayList arrayList = new ArrayList();
        arrayList.add("Testing");
        stdPhysicianMTL.put("MTLA123456",arrayList);

    }

    public String test(){
        return stdPhysicianMTL.get("MTLA123456").get(0);
    }

}
