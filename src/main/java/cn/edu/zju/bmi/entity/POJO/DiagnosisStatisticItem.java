package cn.edu.zju.bmi.entity.POJO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiagnosisStatisticItem {
    private String diagnosisCode;
    private int diagnosisCount;
    private double diagnosisRatio;
}
