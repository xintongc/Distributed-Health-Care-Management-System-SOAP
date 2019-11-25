package RM_1;


import java.util.HashMap;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;


public interface ServerInterface {
	
	String bookAppointment(String patientID, String appointmentID, String appointmentType);

	String getAppointmentSchedule(String patientID);

	String cancelAppointment(String patientID, String appointmentID, String appointmentType);

	String addAppointment(String appointmentID, String appointmentType, int capacity);

	String removeAppointment(String appointmentID, String appointmentType);

	String listAppointmentAvailability(String appointmentType);

	// You will need to implement this
	
	boolean checkCred(String adminID);

	boolean swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType,
			String newAppointmentID, String newAppointmentType);

//	@WebMethod
//	HashMap<String, Integer> getDocMap();
//
//	@WebMethod
//	void setDocMap(HashMap<String, Integer> docMap);
//
//	@WebMethod
//	public HashMap<String, Integer> getDentMap();
//
//	@WebMethod
//	void setDentMap(HashMap<String, Integer> dentMap);
//
//	@WebMethod
//	HashMap<String, Integer> getSurgMap();
//
//	@WebMethod
//	void setSurgMap(HashMap<String, Integer> surgMap);

//	@WebMethod
//		 HashMap<String, Object> getUsers();
//	@WebMethod
//		 void setUsers(HashMap<String, Object> users);

}
