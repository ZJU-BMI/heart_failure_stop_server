package cn.edu.zju.bmi.controller;

import cn.edu.zju.bmi.config.TensorflowConfig;
import cn.edu.zju.bmi.support.ParameterName;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Random;

@RestController
@RequestMapping(path="/machineLearning")
public class MachineLearningModelController {
    private RestTemplate restTemplate;
    private TensorflowConfig tensorflowConfig;

    @Autowired
    public MachineLearningModelController(RestTemplate restTemplate, TensorflowConfig tensorflowConfig){
        this.restTemplate = restTemplate;
        this.tensorflowConfig = tensorflowConfig;
    }

    @RequestMapping(value = "/tensorflow/hawkesRNN")
    public String hawkesRNN(@RequestParam(ParameterName.PREDICT_TASK) String predictTask,
                            @RequestParam(ParameterName.UNIFIED_PATIENT_ID) String unifiedPatientID,
                            @RequestParam(ParameterName.HOSPITAL_CODE) String hospitalCode,
                            @RequestParam(ParameterName.VISIT_TYPE) String visitType,
                            @RequestParam(ParameterName.VISIT_ID) String visitID) throws JSONException {
        String requestBodyString = dataSynthetic();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(requestBodyString, headers);
        String url = tensorflowConfig.getIp()+predictTask+":predict";
        ResponseEntity<String> answer = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        return answer.getBody();
    }


    private String dataSynthetic(){
        Random rand = new Random();

        int batchSize = 1;
        double[][][] event = new double[10][1][11];
        for(int i=0; i<event.length; i++){
            for (int j=0; j<event[0].length; j++){
                for (int k=0; k<event[0][0].length; k++)
                    event[i][j][k] = rand.nextDouble();
            }
        }
        double[][][] context = new double[10][1][189];
        for(int i=0; i<context.length; i++){
            for (int j=0; j<context[0].length; j++){
                for (int k=0; k<context[0][0].length; k++)
                    context[i][j][k] = rand.nextDouble();
            }
        }

        double[][] base = new double[11][1];
        for(int i=0; i<base.length; i++){
            for (int j=0; j<base[0].length; j++){
                base[i][j] = rand.nextDouble();
            }
        }

        double[][] mutual = new double[11][11];
        for(int i=0; i<mutual.length; i++){
            for (int j=0; j<mutual[0].length; j++){
                mutual[i][j] = rand.nextDouble();
            }
        }

        int[][] timeList = new int[1][10];
        for(int i=0; i<timeList.length; i++){
            for (int j=0; j<timeList[0].length; j++){
                timeList[i][j] = 10;
            }
        }

        int taskIndex = 3;

        int[] sequenceLength = new int[1];
        Arrays.fill(sequenceLength, 4);

        int phase = 1;


        JSONObject content = new JSONObject();
        content.put("event", event);
        content.put("context", context);
        content.put("base", base);
        content.put("batch", batchSize);
        content.put("mutual", mutual);
        content.put("phase", phase);
        content.put("time_list", timeList);
        content.put("task", taskIndex);
        content.put("sequence_length", sequenceLength);

        JSONObject request = new JSONObject();
        request.put("inputs", content);
        return request.toString();
    }
}
