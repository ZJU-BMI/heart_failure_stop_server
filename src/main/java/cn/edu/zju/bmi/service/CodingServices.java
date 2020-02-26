package cn.edu.zju.bmi.service;

import org.json.JSONArray;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CodingServices {
    // 本应用中存在编码（诊断、手术、药物、实验室检查）等标记，本services用于提供coding解析的服务
    // 具体的说，LabTest中记录每个检查的coding，名称，数据类型，单位等信息
    // Diagnosis中记录ICD-10前三位编码及对应的诊断类型（由于301医院采用私有6位icd编码，编码表未提供，因此用6位编码无意义）
    // 不用4位编码则是因为目前无相关需求，3位可以满足分类需求，如果以后有相关需求可以进行相关拓展
    // Medicine记录用药的编码和相应的名称
    // Operation Coding Map记录5位ICD-9编码。由于301医院采用的icd-9编码也是私有编码，我们没有编码表，因此我们只采取和公有编码重合的部分
    // 注意，此处四个文件必须的BOM可能会造成一些问题，在Mac和Win下需要注意
    private String diagnosisCodeJson;
    private String labTestCodeJson;
    private String operationCodeJson;
    private String medicineCodeJson;
    private Map<String, String> diagnosisCodeMap = new HashMap<>();
    private Map<String, List<String>> labTestCodeMap = new HashMap<>();
    private Map<String, String> medicineCodeMap = new HashMap<>();
    private Map<String, String> operationCodeMap = new HashMap<>();

    public CodingServices() throws IOException {
        Resource diagnosisResource = new ClassPathResource("./codeList/diagnosis_code_list.txt");
        BufferedReader diagnosisReader = new BufferedReader(new InputStreamReader(diagnosisResource.getInputStream()));
        diagnosisCodeJson = diagnosisReader.readLine();
        diagnosisReader.close();

        Resource labTestResource = new ClassPathResource("./codeList/lab_test_code_list.txt");
        BufferedReader labTestReader = new BufferedReader(new InputStreamReader(labTestResource.getInputStream()));
        labTestCodeJson = labTestReader.readLine();
        labTestReader.close();

        Resource operationResource = new ClassPathResource("./codeList/operation_code_list.txt");
        BufferedReader operationReader = new BufferedReader(new InputStreamReader(operationResource.getInputStream()));
        operationCodeJson = operationReader.readLine();
        operationReader.close();

        Resource medicineResource = new ClassPathResource("./codeList/drug_code_list.txt");
        BufferedReader medicineReader = new BufferedReader(new InputStreamReader(medicineResource.getInputStream()));
        medicineCodeJson = medicineReader.readLine();
        medicineReader.close();
        JSONArray diagnosisJsonArray = new JSONArray(diagnosisCodeJson);
        JSONArray medicineJsonArray = new JSONArray(medicineCodeJson);
        JSONArray operationJsonArray = new JSONArray(operationCodeJson);
        JSONArray labTestJsonArray = new JSONArray(labTestCodeJson);

        // 组织编码表
        for(int i=0; i<diagnosisJsonArray.length();i++){
            JSONArray diagnosisItem = diagnosisJsonArray.getJSONArray(i);
            diagnosisCodeMap.put(diagnosisItem.getString(0), diagnosisItem.getString(1));
        }
        for(int i=0; i<medicineJsonArray.length();i++){
            JSONArray medicineItem = medicineJsonArray.getJSONArray(i);
            medicineCodeMap.put(medicineItem.getString(0), medicineItem.getString(1));
        }
        for(int i=0; i<operationJsonArray.length();i++){
            JSONArray operationItem = operationJsonArray.getJSONArray(i);
            operationCodeMap.put(operationItem.getString(0), operationItem.getString(1));
        }
        for(int i=0; i<labTestJsonArray.length();i++){
            JSONArray labTestItem = labTestJsonArray.getJSONArray(i);
            labTestCodeMap.put(labTestItem.getString(0), new ArrayList<>());
            labTestCodeMap.get(labTestItem.getString(0)).add(labTestItem.getString(1));
            labTestCodeMap.get(labTestItem.getString(0)).add(labTestItem.getString(2));
            labTestCodeMap.get(labTestItem.getString(0)).add(labTestItem.getString(3));
        }
    }

    public String getDiagnosisCodingListJson(){return diagnosisCodeJson;}
    public String getLabTestCodingListJson(){return labTestCodeJson;}
    public String getMedicineCodingListJson(){return medicineCodeJson;}
    public String getOperationCodingListJson(){return operationCodeJson;}
    String getDiagnosisCodeName(String code){return diagnosisCodeMap.getOrDefault(code, "");}
    List<String> getLabTestCodeInfo(String code){return labTestCodeMap.getOrDefault(code, new ArrayList<>());}
    String getOperationCodeName(String code){return operationCodeMap.getOrDefault(code, "");}
    String getMedicineCodeName(String code){return medicineCodeMap.getOrDefault(code, "");}
}
