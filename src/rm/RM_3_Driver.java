package rm;

import Server.MTLServer;
import Server.QUEServer;
import Server.SHEServer;

public class RM_3_Driver {

    public static void main(String[] args) {
    	RemoteManager3 rm = new RemoteManager3();
        StdMaps stdMaps = rm.getStdMaps();

        StdMaps m2 = new StdMaps();
        m2.setTestMap();
        stdMaps.print();

//        try {
//            StdMaps maps = UDPRm.getRemoteStdMaps(9898);
//            maps.print();
//
//        } catch (Exception e){
//            e.printStackTrace();
//        }


        try {
           UDPRm.recoverRemoteMaps(9898, m2);

        } catch (Exception e){
            e.printStackTrace();
        }

    	
    	//test all methods here!
    	
    	
//    	try {
//			System.out.println(rm.listAppointmentAvailability("Physician"));
//			System.out.println(rm.listAppointmentAvailability("Surgeon"));
//			System.out.println(rm.listAppointmentAvailability("Dental"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	try {
//			System.out.println(rm.bookAppointment ("MTLP1","SHEA101119","Surgeon"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		try {
//			System.out.println(rm.getAppointmentSchedule("MTLP1"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		try {
//			System.out.println(rm.cancelAppointment ("MTLP1","SHEA101119","Surgeon"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		try {
//			System.out.println(rm.removeAppointment ("QUEA101119","Dental"));
//			System.out.println(rm.listAppointmentAvailability("Dental"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		try {
//			System.out.println(rm.addAppointment ("MTLA123456","Dental","3"));
//			System.out.println(rm.listAppointmentAvailability("Dental"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		try {
//			System.out.println(rm.addAppointment ("MTLA123456","Dental","3"));
//			System.out.println(rm.listAppointmentAvailability("Dental"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//		try {
//			System.out.println(rm.swapAppointment("MTLP1","SHEA101119","Surgeon","MTLA111111","Dental"));
//			System.out.println(rm.listAppointmentAvailability("Dental"));
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
   
    }


}