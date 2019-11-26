package cn.edu.zju.bmi.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@EnableConfigurationProperties
@ConfigurationProperties("app.tensorflow")
public class TensorflowConfig {
    private String ip = "http://localhost:8501/v1/models/";
}
