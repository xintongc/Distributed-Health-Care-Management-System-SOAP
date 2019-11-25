package RM_1;

import java.util.HashMap;

public class SheBookingDb {
    String patID;
    private String loc;
    private HashMap<String, String> appMap = new HashMap<String, String>();

    SheBookingDb() {

    }

    public HashMap<String, String> getAppMap() {
        return appMap;
    }

    public void setAppMap(HashMap<String, String> appMap) {
        this.appMap = appMap;
    }

    public void setPatID(String patID) {
        this.patID = patID;
    }

    public String getPatID() {
        return patID;
    }

    SheBookingDb(String patID) {
        this.patID = patID;
        loc = patID.substring(0, 3);
    }

    private void setPID(String patID) {
        this.patID = patID;
    }

    public void book(String ID, String type) {
        appMap.put(ID, type);
        System.out.println(appMap);
    }

    private boolean isBookable(String ID, String type) {
        if (appMap.isEmpty()) {
            appMap.put(ID, type);
            return true;
        }

        // same type of appointment on same day
        if (!appMap.containsKey(ID)) {
            String test = ID.substring(4, 8);
            String temp;
            String locTemp;
            int count = 0;
            double date = Double.valueOf("." + ID.substring(4, 8));
            for (String key : appMap.keySet()) {
                String year = ID.substring(8);
                String month = ID.substring(6, 8);
                String day = ID.substring(4, 6);
                temp = key.substring(4, 8);
                locTemp = key.substring(0, 3);
                if (key.substring(8).equals(year) && key.substring(6, 8).equals(month) && key.substring(4, 6).equals(day) && appMap.get(key).equals(type)) {
                    System.out.println("Can not book the same type of app in the same day");
                    return false;
                }
            }
            //Same appointment at same time in other city
            for (String key : appMap.keySet()) {
                String year = ID.substring(8);
                String month = ID.substring(6, 8);
                String day = ID.substring(4, 6);
                String time = ID.substring(3, 4);
                temp = key.substring(4, 8);
                locTemp = key.substring(0, 3);
                if (key.substring(8).equals(year) && key.substring(6, 8).equals(month) && key.substring(4, 6).equals(day) && key.substring(3, 4).equals(time)) {
                    System.out.println("Have another appointment booked someplace else");
                    return false;
                }
            }
            // 3 in a week check
            for (String key : appMap.keySet()) {
                locTemp = key.substring(0, 3);

                if (!locTemp.equals(loc) && ID.substring(6).equals(key.substring(6))
                        && ((Double.valueOf("." + key.substring(4, 8)) - date < 0.07)
                        || (Double.valueOf("." + key.substring(4, 8)) - date > -0.07))) {
                    count++;

                }
                if (count >= 3) {
                    System.out.println("Can not book more than 3 [internal!! ]appointments in one week outside your home city");
                    return false;
                }
            }
            // appMap.put(ID,type);

            return true;
        }
        System.out.println("Unable to book");
        return false;
    }

}
