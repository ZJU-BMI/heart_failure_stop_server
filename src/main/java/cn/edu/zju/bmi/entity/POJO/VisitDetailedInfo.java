package cn.edu.zju.bmi.entity.POJO;

import lombok.Data;

import java.util.List;

@Data
public class VisitDetailedInfo {
    private String patientName;
    private String sex;
    private String age;
    private String hospitalName;
    private String visitType;
    private String visitID;
    private String admissionTime;
    private String dischargeTime;
    private List<String> mainDiagnosis;
    private String treatResult;
    private List<String> operation;
    private List<String> otherDiagnosis;
    private String deathFlag;
    private String symptom;

    public VisitDetailedInfo(String name, String sex, String age, String hospitalName, String visitType, String visitID,
                             String admissionTime, String dischargeTime, List<String> mainDiagnosis,
                             List<String> operation, List<String> otherDiagnosis,String deathFlag, String symptom){
        this.patientName = name;
        this.sex = sex;
        this.age = age;
        this.hospitalName = hospitalName;
        this.visitID = visitID;
        this.visitType = visitType;
        this.admissionTime = admissionTime;
        this.dischargeTime = dischargeTime;
        this.mainDiagnosis = mainDiagnosis;
        this.operation = operation;
        this.otherDiagnosis = otherDiagnosis;
        this.deathFlag = deathFlag;
        this.symptom = symptom;
    }
}
