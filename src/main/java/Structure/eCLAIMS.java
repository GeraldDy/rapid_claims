/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Structure;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Wewe
 */

@XmlRootElement
public class eCLAIMS {
    private CF2 cf2;

    @XmlElement(name = "CF2")
    public CF2 getCf2() {
        return cf2;
    }

    public void setCf2(CF2 cf2) {
        this.cf2 = cf2;
    }

    public static class CF2 {
        private String pPatientReferred;
        private String pReferredIHCPAccreCode;
        private String pDisposition;
        private String pExpiredDate;
        private String pExpiredTime;
        private String pAdmissionDate;
        private String pHospitalCode;
        private String pmcc;
        
        @XmlElement(name = "pAdmissionDate")
      
        public String getpAdmissionDate(){
            return pAdmissionDate;
        }
        public void setpAdmissionDate(String pAdmissionDate){
            this.pAdmissionDate = pAdmissionDate;
        
        }
      

        @XmlElement(name = "pPatientReferred")
        public String getpPatientReferred() {
            return pPatientReferred;
        }

        public void setpPatientReferred(String pPatientReferred) {
            this.pPatientReferred = pPatientReferred;
        }
        
        @XmlElement(name = "pReferredIHCPAccreCode")
        public String getpReferredIHCPAccreCode() {
            return pReferredIHCPAccreCode;
        }

        public void setpReferredIHCPAccreCode(String pReferredIHCPAccreCode) {
            this.pReferredIHCPAccreCode = pReferredIHCPAccreCode;
        }
        
        @XmlElement(name = "pDisposition")
        public String getpDisposition() {
            return pDisposition;
        }

        public void setpDisposition(String pDisposition) {
            this.pDisposition = pDisposition;
        }
        @XmlElement(name = "pExpiredDate")
        public String getpExpiredDate() {
            return pExpiredDate;
        }
                
        public void setpExpiredDate(String pExpiredDate) {
            this.pExpiredDate = pExpiredDate;
        }
        @XmlElement(name = "pExpiredDate")
        public String getpExpiredTime() {
            return pExpiredTime;
        }

        public void setpExpiredTime(String pExpiredTime) {
            this.pExpiredTime = pExpiredTime;
        }
        
        @XmlElement(name = "pHospitalCode")
        public String getpHospitalCode() {
            return pHospitalCode;
        }

        public void setpHospitalCode(String pHospitalCode) {
            this.pHospitalCode = pHospitalCode;
        }

        public String getPmcc() {
            return pmcc;
        }

        public void setPmcc(String pmcc) {
            this.pmcc = pmcc;
        }
        
    }
    
}
