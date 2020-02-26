package cn.edu.zju.bmi.entity.POJO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabTestStatisticItem {
    private String code;
    private double avgValue;
    private double std;
    private double missingRate;
}