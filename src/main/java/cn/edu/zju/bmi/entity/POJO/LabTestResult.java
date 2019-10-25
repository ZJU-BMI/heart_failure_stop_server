package cn.edu.zju.bmi.entity.POJO;
import lombok.Data;

import java.util.Date;

@Data
public class LabTestResult {
    private String labTestName;
    private String result;
    private String unit;
    private Date testTime;

    public LabTestResult(String labTestName, String result, String unit, Date testTime){
        this.labTestName = labTestName;
        this.result = result;
        this.unit = unit;
        this.testTime = testTime;
    }
}
