package cn.edu.zju.bmi.controller.test;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;


@Controller
@RequestMapping(path="/tensorflow")
public class TestTensorflowController {

    private RestTemplate restTemplate;

    @Autowired
    public TestTensorflowController(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    @RequestMapping(value = "/test")
    public String tensorflowTest() throws JSONException {
        double[][] images = new double[1][784];
        for(int i=0; i<images.length; i++){
            for(int j=0; j<images[0].length;j++){
                images[i][j] = 0.1;
            }
        }

        JSONObject request = new JSONObject();
        request.put("instances", images);
        request.put("signature_name", "predict_images");
        String requestBodyString = request.toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestBodyString, headers);

        String url = "http://localhost:8501/v1/models/mnist:predict";

        ResponseEntity<String> answer = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        return answer.toString();
    }
}
