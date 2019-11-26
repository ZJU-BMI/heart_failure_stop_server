package cn.edu.zju.bmi.entity.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private String orderName;
    private String orderClass;
    private String dosage;
    private String unit;
    private String frequency;
    private String startTime;
    private String endTime;
}
