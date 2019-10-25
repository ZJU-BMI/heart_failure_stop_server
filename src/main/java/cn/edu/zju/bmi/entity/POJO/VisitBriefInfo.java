package cn.edu.zju.bmi.entity.POJO;

import lombok.Data;

import java.util.List;

@Data
public class VisitBriefInfo {
    private String admissionTime;
    private String dischargeTime;
    private String hospitalName;
    private String mainDiagnosis;
    private String symptom;
    private String deathFlag;
    public VisitBriefInfo(String admissionTime, String dischargeTime, String hospitalName, String mainDiagnosis,
                          String symptom, String deathFlag){
        this.admissionTime=admissionTime;
        this.dischargeTime=dischargeTime;
        this.hospitalName=hospitalName;
        this.mainDiagnosis=mainDiagnosis;
        this.symptom=symptom;
        this.deathFlag=deathFlag;
    }
}
