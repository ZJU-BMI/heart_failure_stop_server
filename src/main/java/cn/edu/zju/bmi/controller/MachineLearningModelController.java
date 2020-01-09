package cn.edu.zju.bmi.controller;
import cn.edu.zju.bmi.service.AlgorithmManagementService;
import cn.edu.zju.bmi.service.MachineLearningDataPrepareService;
import cn.edu.zju.bmi.support.ParameterName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.security.RolesAllowed;


@RestController
@RequestMapping(path="/backend/machineLearning")
@RolesAllowed("ROLE_USER")
public class MachineLearningModelController {
    private RestTemplate restTemplate;
    private MachineLearningDataPrepareService machineLearningDataPrepareService;
    private AlgorithmManagementService algorithmManagementService;

    @Value("${app.tensorflowServerAddress}")
    private String tensorflowAddress;

    @Autowired
    public MachineLearningModelController(MachineLearningDataPrepareService machineLearningDataPrepareService,
                                          AlgorithmManagementService algorithmManagementService,
                                          RestTemplate restTemplate){
        this.restTemplate = restTemplate;
        this.machineLearningDataPrepareService = machineLearningDataPrepareService;
        this.algorithmManagementService = algorithmManagementService;
    }

    @GetMapping(value = "/singleVisitInvokeMachineLearningService")
    public String singleVisitInvokeMachineLearningService(
            @RequestParam(ParameterName.UNIFIED_PATIENT_ID) String unifiedPatientID,
            @RequestParam(ParameterName.HOSPITAL_CODE) String hospitalCode,
            @RequestParam(ParameterName.VISIT_TYPE) String visitType,
            @RequestParam(ParameterName.VISIT_ID) String visitID,
            @RequestParam(ParameterName.MODEL_FUNCTION) String modelFunction,
            @RequestParam(ParameterName.MODEL_NAME) String modelName,
            @RequestParam(ParameterName.MODEL_CATEGORY) String modelCategory) throws Exception {
        String requestBodyString = machineLearningDataPrepareService.fetchData(unifiedPatientID, hospitalCode,
                visitType, visitID, modelCategory, modelName, modelFunction);


        String platform = algorithmManagementService.getModelInfo(modelCategory, modelName, modelFunction).getPlatform();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBodyString, headers);

        if(platform.equals("Tensorflow")) {
            String url = tensorflowAddress + modelCategory + "_" + modelName + "_" + modelFunction + ":predict";
            ResponseEntity<String> answer = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return answer.getBody();
        }
        else{
            // prepare for pytorch and sklearn, To Be Done
            String url = tensorflowAddress + modelCategory + "/" + modelName + "/" + modelFunction + ":predict";
            ResponseEntity<String> answer = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return answer.getBody();
        }
    }
}
