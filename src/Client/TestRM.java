package Client;

import Server.Message;
import rm.StdMaps;
import rm.UDPRm;

public class TestRM {

    public static void main(String[] args) {
        System.out.println("start testing");

        try {
            StdMaps stdMaps = UDPRm.getRemoteStdMaps(1000);
            System.out.println(stdMaps.getStr());

        } catch (Exception e){
            e.printStackTrace();
        }


    }
}
