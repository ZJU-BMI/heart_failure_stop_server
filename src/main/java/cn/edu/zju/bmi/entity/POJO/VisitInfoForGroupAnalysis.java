package cn.edu.zju.bmi.entity.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class VisitInfoForGroupAnalysis {
    private String localPatientID;
    private String unifiedPatientID;
    private String name;
    private String hospitalCode;
    private String hospitalName;
    private String visitID;
    private String visitType;
    private String sex;
    private String age;
    private String mainDiagnosis;
    private String los;
    private String admissionTime;
}
