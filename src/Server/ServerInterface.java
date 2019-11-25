package Server;

import java.io.IOException;

public interface ServerInterface{


	boolean bookAppointment (String patientID, String appointmentID, String appointmentType)throws ClassNotFoundException, IOException;

	boolean getAppointmentSchedule (String patientID);

	boolean cancelAppointment (String patientID, String appointmentID, String appointmentType)throws ClassNotFoundException, IOException;

	boolean swapAppointment (String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType)throws ClassNotFoundException, IOException;

	boolean addAppointment (String appointmentID, String appointmentType, String capacity);

	boolean checkAppointmentExisted (String appointmentID, String appointmentType);

	boolean removeAppointment (String appointmentID, String appointmentType);

	boolean listAppointmentAvailability (String appolointmentType);

	boolean checkCred(String adminID);



	void writeTxtClient (String clientID, String task, String resultStr);

	void writeTxtServerMTL (String clientID, String patientID, String appointmentType, String appointmentID, String task, String resultStr);

	void writeTxtServerQUE (String clientID, String patientID, String appointmentType, String appointmentID, String task, String resultStr);

	void writeTxtServerSHE (String clientID, String patientID, String appointmentType, String appointmentID, String task, String resultStr);

}
