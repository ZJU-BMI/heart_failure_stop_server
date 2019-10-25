package cn.edu.zju.bmi.entity.POJO;

import lombok.Data;

import java.util.Date;

@Data
public class VitalSignResult {
    private String vitalSignName;
    private Date recordTime;
    private String result;
    private String unit;

    public VitalSignResult(String vitalSignName, Date recordTime, String result, String unit){
        this.vitalSignName = vitalSignName;
        this.recordTime = recordTime;
        this.result = result;
        this.unit = unit;
    }
}
