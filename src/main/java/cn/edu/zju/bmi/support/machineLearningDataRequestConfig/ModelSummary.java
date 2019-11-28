package cn.edu.zju.bmi.support.machineLearningDataRequestConfig;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModelSummary {
    private String modelName;
    private String modelVersion;
    private String developer;
    private String literature;
    private String machineLearningFramework;
    private String machineLearningFrameworkVersion;
}
