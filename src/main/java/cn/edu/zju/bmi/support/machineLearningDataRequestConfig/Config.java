package cn.edu.zju.bmi.support.machineLearningDataRequestConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Config {
    private String dataType;
    private ModelSummary modelSummary;
    private List<String> databaseRequest;

}