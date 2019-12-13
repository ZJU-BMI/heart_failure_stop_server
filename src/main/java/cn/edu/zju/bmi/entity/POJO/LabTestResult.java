package cn.edu.zju.bmi.entity.POJO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabTestResult {
    private String labTestName;
    private String result;
    private String unit;
    private Date testTime;
    private String hospitalCode;
    private String visitType;
    private String visitID;
    private String hospitalName;

    public LabTestResult(String labTestName, String result, String unit, Date testTime){
        this.labTestName = labTestName;
        this.result = result;
        this.unit = unit;
        this.testTime = testTime;
    }
}
