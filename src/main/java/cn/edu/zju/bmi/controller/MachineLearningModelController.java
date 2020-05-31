package cn.edu.zju.bmi.controller;
import cn.edu.zju.bmi.service.AlgorithmManagementService;
import cn.edu.zju.bmi.service.MachineLearningDataPrepareService;
import cn.edu.zju.bmi.support.ParameterName;
import cn.edu.zju.bmi.support.PathName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.security.RolesAllowed;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.HashMap;


@RestController
@RequestMapping(path= PathName.INVOKE_MACHINE_LEARNING_SERVICE)
@RolesAllowed("ROLE_USER")
public class MachineLearningModelController {
    private RestTemplate restTemplate;
    private MachineLearningDataPrepareService machineLearningDataPrepareService;
    private AlgorithmManagementService algorithmManagementService;

    @Value("${app.tensorflowServerAddress}")
    private String tensorflowAddress;

    @Value("http://localhost:5000/models")
    private String pyTorchAddress;

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
    //仅开发使用，得到json数据
    @GetMapping(value = PathName.FETCH_UN_PREPROCESSED_MACHINE_LEARNING_MODEL_DATA)
    public String getMachineLearningModelData(
            @RequestParam(ParameterName.UNIFIED_PATIENT_ID) String unifiedPatientID,
            @RequestParam(ParameterName.HOSPITAL_CODE) String hospitalCode,
            @RequestParam(ParameterName.VISIT_TYPE) String visitType,
            @RequestParam(ParameterName.VISIT_ID) String visitID) {
        return machineLearningDataPrepareService.getUnPreprocessedData(unifiedPatientID, hospitalCode,
                visitType, visitID);
    }

    @PostMapping(value = PathName.EXECUTE_MACHINE_LEARNING_MODEL)
    public String executeModel(
            @RequestParam(ParameterName.MODEL_NAME) String modelName,
            @RequestParam(ParameterName.MODEL_CATEGORY) String modelCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION) String modelFunction,
            @RequestParam(ParameterName.MODEL_INPUT) String input) throws IOException, InterruptedException {
//            假设去掉input
//            ) throws IOException, InterruptedException {


        //说明已经进来断点这里了
        String platform = algorithmManagementService.getModelInfo(modelCategory, modelName, modelFunction).getPlatform();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //测试代码用
        HttpEntity<String> entity = new HttpEntity<>("", headers);

        if(platform.equals("Tensorflow")||platform.equals("tensorflow")) {
            String url = tensorflowAddress + modelCategory + "_" + modelName + "_" + modelFunction + ":predict";
            try{
                ResponseEntity<String> answer = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
                return answer.getBody();
            }
            catch (Exception e){
                return "{\n \"outputs\": [\n [\n 0\n ]\n ]\n }";
            }
        }
        //应该会跳进去这里
        else if (platform.equals("PyTorch") || platform.equals("pyTorch") || platform.equals("pytorch") || platform.equals("Pytorch")){

            //填充输入数据
            var values = new HashMap<String, String>() {{
                put("data", input);
            }};

            var objectMapper = new ObjectMapper();
            String requestBody = objectMapper
                    .writeValueAsString(values);

            HttpClient client = HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:5000/models"))
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());

            return response.body();


        }else{
//             prepare for pytorch and sklearn, To Be Done
            String url = tensorflowAddress + modelCategory + "/" + modelName + "/" + modelFunction + ":predict";
            ResponseEntity<String> answer = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return answer.getBody();
        }
    }
}
