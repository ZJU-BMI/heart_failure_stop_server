package cn.edu.zju.bmi.controller;

import cn.edu.zju.bmi.config.MachineLearningConfig;
import cn.edu.zju.bmi.service.MachineLearningDataPrepareService;
import cn.edu.zju.bmi.support.ParameterName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


@RestController
@RequestMapping(path="/backend/machineLearning")
public class MachineLearningModelController {
    private RestTemplate restTemplate;
    private MachineLearningConfig machineLearningConfig;
    private MachineLearningDataPrepareService machineLearningDataPrepareService;

    @Autowired
    public MachineLearningModelController(MachineLearningDataPrepareService machineLearningDataPrepareService,
                                          RestTemplate restTemplate, MachineLearningConfig machineLearningConfig){
        this.restTemplate = restTemplate;
        this.machineLearningConfig = machineLearningConfig;
        this.machineLearningDataPrepareService = machineLearningDataPrepareService;
    }

    @RequestMapping(value = "/tensorflow/hawkesRNN")
    public String hawkesRNN(@RequestParam(ParameterName.PREDICT_TASK) String predictTask,
                            @RequestParam(ParameterName.UNIFIED_PATIENT_ID) String unifiedPatientID,
                            @RequestParam(ParameterName.HOSPITAL_CODE) String hospitalCode,
                            @RequestParam(ParameterName.VISIT_TYPE) String visitType,
                            @RequestParam(ParameterName.VISIT_ID) String visitID) throws Exception {
        String requestBodyString = machineLearningDataPrepareService.fetchData(unifiedPatientID, hospitalCode,
                visitType, visitID, "HawkesRNN", predictTask);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBodyString, headers);
        String url = machineLearningConfig.getTensorflowAddress()+predictTask+":predict";
        ResponseEntity<String> answer = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return answer.getBody();
    }
}
