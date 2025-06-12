/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Structure;
import javax.xml.bind.annotation.XmlRootElement;
/**
 *
 * @author jeballeta
 */
@XmlRootElement
public class AdmissionDashboard {
    private String referenceNo;
    private String admissionCode;
    private String admissionDate;
    private String dateSubmitted;

    // Constructors
    public AdmissionDashboard() {}

    public AdmissionDashboard(String referenceNo, String admissionCode, String admissionDate, String dateSubmitted) {
        this.referenceNo = referenceNo;
        this.admissionCode = admissionCode;
        this.admissionDate = admissionDate;
        this.dateSubmitted = dateSubmitted;
    }

    // Getters and Setters
    public String getReferenceNo() { 
        return referenceNo; 
    }
    public void setReferenceNo(String referenceNo) { 
        this.referenceNo = referenceNo; 
    }

    public String getAdmissionCode() { 
        return admissionCode; 
    }
    public void setAdmissionCode(String admissionCode) { 
        this.admissionCode = admissionCode; 
    }

    public String getAdmissionDate() { 
        return admissionDate; 
    }
    public void setAdmissionDate(String admissionDate) { 
        this.admissionDate = admissionDate; 
    }

    public String getDateSubmitted() { 
        return dateSubmitted; 
    }
    public void setDateSubmitted(String dateSubmitted) { 
        this.dateSubmitted = dateSubmitted; 
    }

    @Override
    public String toString() {
        return referenceNo + " | " + admissionCode + " | " + admissionDate + " | " + dateSubmitted;
    }
}
