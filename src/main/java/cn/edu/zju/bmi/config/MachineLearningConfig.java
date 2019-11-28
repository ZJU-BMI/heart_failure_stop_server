package cn.edu.zju.bmi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@EnableConfigurationProperties
@ConfigurationProperties("app.tensorflow")
public class MachineLearningConfig {
    private final String tensorflowAddress = "http://localhost:8501/v1/models/";
    private final String machineLearningResourceRoot = "machineLearningConfigAndPreprocess/";
    private final String fullMachineLearningResourceRoot = "/machineLearningConfigAndPreprocess/";
    private final String hawkesRNNFolderPath = "/Users/sunzhoujian/tensorflowExtended/hawkesRNN/";


    public static final String SINGLE_VISIT = "SINGLE_VISIT";
    public static final String MULTI_VISIT_BEFORE_TARGET_VISIT = "MULTI_VISIT_BEFORE_TARGET_VISIT";
    public static final String MULTI_VISIT_BEFORE_AND_EQUAL_TARGET_VISIT = "MULTI_VISIT_BEFORE_AND_EQUAL_TARGET_VISIT";
}
