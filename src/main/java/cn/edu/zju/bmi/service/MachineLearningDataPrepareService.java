package cn.edu.zju.bmi.service;
import cn.edu.zju.bmi.entity.DAO.*;
import cn.edu.zju.bmi.repository.*;
import cn.edu.zju.bmi.support.FourElementTuple;
import cn.edu.zju.bmi.support.ParameterName;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
public class MachineLearningDataPrepareService {
    private DiagnosisRepository diagnosisRepository;
    private LabTestRepository labTestRepository;
    private OperationRepository operationRepository;
    private OrdersRepository ordersRepository;
    private PatientVisitRepository patientVisitRepository;
    private VitalSignRepository vitalSignRepository;
    private PatientRepository patientRepository;
    private ExamRepository examRepository;

    @Value(value="${app.machineLearningModelRoot}")
    private String root;

    @Autowired
    public MachineLearningDataPrepareService(DiagnosisRepository diagnosisRepository, LabTestRepository labTestRepository,
                                             OperationRepository operationRepository, OrdersRepository ordersRepository,
                                             PatientRepository patientRepository, VitalSignRepository vitalSignRepository,
                                             PatientVisitRepository patientVisitRepository, ExamRepository examRepository)
    {
        this.diagnosisRepository = diagnosisRepository;
        this.labTestRepository = labTestRepository;
        this.operationRepository = operationRepository;
        this.ordersRepository = ordersRepository;
        this.patientRepository = patientRepository;
        this.vitalSignRepository = vitalSignRepository;
        this.patientVisitRepository = patientVisitRepository;
        this.examRepository = examRepository;
    }

    public String getUnPreprocessedData(String unifiedPatientID, String hospitalCode, String visitType, String visitID){
        // 当输入相应请求时，返回该病人之前的所有数据（这里可能会多传很多不需要的信息，留待以后优化）
        List<FourElementTuple<String, String, String, Long>> validList=
                getVisitListBeforeOrEqualToTargetVisit(unifiedPatientID, hospitalCode, visitType, visitID);
        return getFullDataFromDatabase(unifiedPatientID, validList);
    }

    public String fetchData(String unifiedPatientID, String hospitalCode, String visitType, String visitID,
                            String modelCategory, String modelName, String modelFunction) throws Exception {

        String folder = root+modelCategory+"/"+modelName+"/"+modelFunction+"/";

        // 当输入相应请求时，返回该病人之前的所有数据（这里可能会多传很多不需要的信息，留待以后优化）
        List<FourElementTuple<String, String, String, Long>> validList=
                getVisitListBeforeOrEqualToTargetVisit(unifiedPatientID, hospitalCode, visitType, visitID);
        String unPreprocessedData = getFullDataFromDatabase(unifiedPatientID, validList);

        // 由于数据数量较大，不能直接作为参数传入，因此先暂存一下
        String fileName = new Date().getTime() +unifiedPatientID+hospitalCode+visitType+visitID;
        FileOutputStream output = new FileOutputStream(folder+"preprocess/"+fileName);
        output.write(unPreprocessedData.getBytes());
        output.close();

        // 利用外源性py脚本做数据预处理
        // 执行外部Python脚本可能带来效率问题，以后构建微服务解决
        String command = "python " + folder+"preprocess/data_convert.py " + fileName;
        Process proc = Runtime.getRuntime().exec(command);
        proc.waitFor();

        BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String line;
        String returnStr = null;
        while ((line = in.readLine()) != null) {
            returnStr = line;
            break;
        }
        in.close();

        //用完删除数据文件
        Files.delete(Paths.get(folder+"preprocess/"+fileName));
        return returnStr;
    }

    private String getFullDataFromDatabase(String unifiedPatientID,
                                           List<FourElementTuple<String, String, String, Long>> visitList){
        // featureTypeList 包括
        String[] featureTypeList = new String[]{
                "basicInfo", "visitInfo", "medicine", "operation", "labTest",
                "exam", "vitalSign", "diagnosis"};

        JSONObject request = new JSONObject();

        // for multiple Visit, return in JSON string
        for(FourElementTuple<String, String, String, Long> visit: visitList){
            String hospitalCode = visit.getA();
            String visitType = visit.getB();
            String visitID = visit.getC();
            // 此处的globalTime指的是Date.getTime()的返回值
            String globalAdmissionTime = String.valueOf(visit.getD());
            JSONObject singleVisitRequest =
                    getSingleVisitData(featureTypeList, unifiedPatientID, hospitalCode, visitID, visitType);
            singleVisitRequest.put(ParameterName.HOSPITAL_CODE, hospitalCode);
            singleVisitRequest.put(ParameterName.VISIT_TYPE, visitType);
            singleVisitRequest.put(ParameterName.VISIT_ID, visitID);
            singleVisitRequest.put(ParameterName.UNIFIED_PATIENT_ID, unifiedPatientID);

            // 此处的
            request.put(String.valueOf(globalAdmissionTime), singleVisitRequest);
        }
        return request.toString();
    }

    private JSONObject getSingleVisitData(String[] featureList, String unifiedPatientID, String hospitalCode,
                                      String visitID, String visitType){
        JSONObject request = new JSONObject();

        for(String featureType : featureList){
            switch (featureType){
                case "basicInfo": {
                    Patient patient = patientRepository.findPatientByUnifiedPatientID(unifiedPatientID);
                    Map<String, String> patientMap = new HashMap<>();
                    patientMap.put("ethnicGroup", patient.getEthnicGroup());
                    patientMap.put("name", patient.getName());
                    patientMap.put("sex", patient.getSex());
                    patientMap.put("unifiedPatientID", patient.getUnifiedPatientID());
                    patientMap.put("birthday", patient.getBirthday().toString());
                    request.put("basicInfo", patientMap);
                    break;
                }
                case "visitInfo": {
                    PatientVisit patientVisit =
                            patientVisitRepository.findPatientVisitByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCode(
                                    unifiedPatientID, visitID, visitType, hospitalCode);
                    Map<String, String> visitMap = new HashMap<>();
                    visitMap.put("deathFlag", patientVisit.getDeathFlag());
                    visitMap.put("admissionDept", patientVisit.getDeptNameAdmissionTo());
                    visitMap.put("dischargeDept", patientVisit.getDeptNameDischargeFrom());
                    visitMap.put("admissionTime", patientVisit.getAdmissionDateTime().toString());
                    visitMap.put("dischargeTime", patientVisit.getDischargeDateTime().toString());
                    request.put("visitInfo", visitMap);
                    break;
                }
                case "medicine": {
                    List<Orders> medicines = ordersRepository.findByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCodeAndOrderClass(
                            unifiedPatientID, visitID, visitType, hospitalCode, "A");
                    request.put("medicines", medicines);
                    break;
                }
                case "operation": {
                    List<Operation> operations =
                    operationRepository.findByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCode(
                            unifiedPatientID, visitID, visitType, hospitalCode);
                    request.put("operations", operations);
                    break;
                }
                case "labTest": {
                    List<LabTest> labTests =
                            labTestRepository.findByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCode(
                                    unifiedPatientID, visitID, visitType, hospitalCode);
                    request.put("labTests", labTests);
                    break;
                }
                case "exam":{
                    List<Exam> exams =
                            examRepository.findByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCode(
                                    unifiedPatientID, visitID, visitType, hospitalCode);
                    request.put("exams", exams);
                    break;
                }
                case "vitalSign":{
                    List<VitalSign> vitalSigns =
                            vitalSignRepository.findByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCode(
                                    unifiedPatientID, visitID, visitType, hospitalCode);
                    request.put("vitalSigns", vitalSigns);
                    break;
                }
                case "diagnosis":{
                    List<Diagnosis> diagnoses =
                            diagnosisRepository.findAllByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCode(
                                    unifiedPatientID, visitID, visitType, hospitalCode);
                    request.put("diagnoses", diagnoses);
                    break;
                }
            }
        }
        return request;
    }

    private List<FourElementTuple<String, String, String, Long>>
        getVisitListBeforeOrEqualToTargetVisit(String unifiedPatientID, String hospitalCode, String visitType,
                                               String visitID) {

        List<FourElementTuple<String, String, String, Long>> visitList = new ArrayList<>();

        PatientVisit patientVisit =
                patientVisitRepository.findPatientVisitByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCode(
                unifiedPatientID, visitID, visitType, hospitalCode);
        Date admissionTime = patientVisit.getAdmissionDateTime();
        List<PatientVisit> candidateVisitList = patientVisitRepository.findAllByKeyUnifiedPatientID(unifiedPatientID);

        for (PatientVisit candidateVisit: candidateVisitList) {
            Date candidateVisitAdmissionTime = candidateVisit.getAdmissionDateTime();
            if(admissionTime.after(candidateVisitAdmissionTime) || admissionTime.equals(candidateVisitAdmissionTime)){
                String candidateVisitID = candidateVisit.getKey().getVisitID();
                String candidateVisitType = candidateVisit.getKey().getVisitType();
                String candidateHospitalCode = candidateVisit.getKey().getHospitalCode();
                Long timeStamp = candidateVisit.getAdmissionDateTime().getTime();
                visitList.add(new FourElementTuple<>(candidateHospitalCode, candidateVisitType,
                        candidateVisitID, timeStamp));
            }
        }

        visitList.sort(Comparator.comparing(FourElementTuple::getD));
        return visitList;
    }
}