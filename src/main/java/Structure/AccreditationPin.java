/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Structure;

/**
 *
 * @author Wewe
 */
public class AccreditationPin {
    public AccreditationPin() {
              
    }
        private String phic_code;
        private String pin;
        private String his_date;       
        private String his_start_date;
        private String his_expire_date;
        private String date_status;

    public String getPhic_code() {
        return phic_code;
    }

    public void setPhic_code(String phic_code) {
        this.phic_code = phic_code;
    }

    public String getHis_date() {
        return his_date;
    }

    public void setHis_date(String his_date) {
        this.his_date = his_date;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getHis_start_date() {
        return his_start_date;
    }

    public void setHis_start_date(String his_start_date) {
        this.his_start_date = his_start_date;
    }

    public String getHis_expire_date() {
        return his_expire_date;
    }

    public void setHis_expire_date(String his_expire_date) {
        this.his_expire_date = his_expire_date;
    }

    public String getDate_status() {
        return date_status;
    }

    public void setDate_status(String date_status) {
        this.date_status = date_status;
    } 
}
