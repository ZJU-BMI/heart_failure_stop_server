package cn.edu.zju.bmi.entity.POJO;

import lombok.Data;

@Data
public class MedicalOralIntervention {
    private String interventionName;
    private String dosageWithUnit;
    private String frequency;
    private String startTime;
    private String endTime;

    public MedicalOralIntervention(String interventionName, String dosageWithUnit, String frequency, String startTime,
                                   String endTime){
        this.interventionName = interventionName;
        this.dosageWithUnit = dosageWithUnit;
        this.frequency = frequency;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
