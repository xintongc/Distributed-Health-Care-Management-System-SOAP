package Server;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import java.io.IOException;

@WebService
@SOAPBinding(style = Style.RPC)
public interface ServerInterface{

	@WebMethod
	boolean bookAppointment (String patientID, String appointmentID, String appointmentType) throws Exception;
	@WebMethod
	boolean getAppointmentSchedule (String patientID);
	@WebMethod
	boolean cancelAppointment (String patientID, String appointmentID, String appointmentType) throws ClassNotFoundException, IOException;
	@WebMethod
	boolean swapAppointment (String clientID, String patientID, String oldAppointmentID, String oldAppointmentType, String newAppointmentID, String newAppointmentType) throws ClassNotFoundException, IOException;
	@WebMethod
	boolean addAppointment (String appointmentID, String appointmentType, String capacity, String appointmentWeekStr);
	@WebMethod
	boolean checkAppointmentExisted (String appointmentID, String appointmentType);
	@WebMethod
	boolean removeAppointment (String appointmentID, String appointmentType);
	@WebMethod
	boolean listAppointmentAvailability (String appointmentType);

	@WebMethod
	void writeTxtClient (String clientID, String task, String resultStr);
	@WebMethod
	void writeTxtServerMTL (String clientID, String patientID, String appointmentType, String appointmentID, String task, String resultStr);
	@WebMethod
	void writeTxtServerQUE (String clientID, String patientID, String appointmentType, String appointmentID, String task, String resultStr);
	@WebMethod
	void writeTxtServerSHE (String clientID, String patientID, String appointmentType, String appointmentID, String task, String resultStr);

}
