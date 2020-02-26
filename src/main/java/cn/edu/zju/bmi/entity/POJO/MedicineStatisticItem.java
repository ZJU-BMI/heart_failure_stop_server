package cn.edu.zju.bmi.entity.POJO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineStatisticItem {
    private String medicineCode;
    private int medicineCount;
    private double medicineRatio;
}
