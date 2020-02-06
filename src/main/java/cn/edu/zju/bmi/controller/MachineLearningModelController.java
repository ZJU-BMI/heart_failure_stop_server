package cn.edu.zju.bmi.controller;
import cn.edu.zju.bmi.service.AlgorithmManagementService;
import cn.edu.zju.bmi.service.MachineLearningDataPrepareService;
import cn.edu.zju.bmi.support.ParameterName;
import cn.edu.zju.bmi.support.PathName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.security.RolesAllowed;


@RestController
@RequestMapping(path= PathName.INVOKE_MACHINE_LEARNING_SERVICE)
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

    @GetMapping(value = PathName.FETCH_MACHINE_LEARNING_MODEL_DATA)
    public String getMachineLearningModelData(
            @RequestParam(ParameterName.UNIFIED_PATIENT_ID) String unifiedPatientID,
            @RequestParam(ParameterName.HOSPITAL_CODE) String hospitalCode,
            @RequestParam(ParameterName.VISIT_TYPE) String visitType,
            @RequestParam(ParameterName.VISIT_ID) String visitID,
            @RequestParam(ParameterName.MODEL_FUNCTION) String modelFunction,
            @RequestParam(ParameterName.MODEL_NAME) String modelName,
            @RequestParam(ParameterName.MODEL_CATEGORY) String modelCategory) throws Exception {
        return machineLearningDataPrepareService.fetchData(unifiedPatientID, hospitalCode,
                visitType, visitID, modelCategory, modelName, modelFunction);
    }

    @PostMapping(value = PathName.EXECUTE_MACHINE_LEARNING_MODEL)
    public String executeModel(
            @RequestParam(ParameterName.MODEL_NAME) String modelName,
            @RequestParam(ParameterName.MODEL_CATEGORY) String modelCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION) String modelFunction,
            @RequestParam(ParameterName.MODEL_INPUT) String input){

        String platform = algorithmManagementService.getModelInfo(modelCategory, modelName, modelFunction).getPlatform();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(input, headers);

        if(platform.equals("Tensorflow")) {
            String url = tensorflowAddress + modelCategory + "_" + modelName + "_" + modelFunction + ":predict";
            try{
                ResponseEntity<String> answer = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                return answer.getBody();
            }
            catch (Exception e){
                return "{\n \"outputs\": [\n [\n 0\n ]\n ]\n }";
            }
        }
        else{
            // prepare for pytorch and sklearn, To Be Done
            String url = tensorflowAddress + modelCategory + "/" + modelName + "/" + modelFunction + ":predict";
            ResponseEntity<String> answer = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return answer.getBody();
        }
    }
}
