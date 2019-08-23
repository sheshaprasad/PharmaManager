package com.silverbeta.medicinemanager.Models;

import java.io.Serializable;

public class DoctorsList implements Serializable {

    String doctorName,doctorClinic,doctorId,doctorNumber;

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorClinic() {
        return doctorClinic;
    }

    public void setDoctorClinic(String doctorClinic) {
        this.doctorClinic = doctorClinic;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorNumber() {
        return doctorNumber;
    }

    public void setDoctorNumber(String doctorNumber) {
        this.doctorNumber = doctorNumber;
    }
}
