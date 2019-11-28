package cn.edu.zju.bmi.service;

import cn.edu.zju.bmi.config.MachineLearningConfig;
import cn.edu.zju.bmi.entity.DAO.*;
import cn.edu.zju.bmi.repository.*;
import cn.edu.zju.bmi.support.FourElementTuple;
import cn.edu.zju.bmi.support.machineLearningDataRequestConfig.Config;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
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
    private MachineLearningConfig machineLearningConfig;



    @Autowired
    public MachineLearningDataPrepareService(DiagnosisRepository diagnosisRepository, LabTestRepository labTestRepository,
                                             MachineLearningConfig machineLearningConfig,
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
        this.machineLearningConfig = machineLearningConfig;
    }

    public String fetchData(String unifiedPatientID, String hospitalCode, String visitType,
                            String visitID, String model, String predictTask) throws Exception {
        Yaml yaml = new Yaml(new Constructor(Config.class));
        InputStream inputStream = this.getClass()
                .getClassLoader().getResourceAsStream(machineLearningConfig.getMachineLearningResourceRoot()+model+".yml");
        Config config = yaml.load(inputStream);

        String dataType = config.getDataType();
        List<String> featureList = config.getDatabaseRequest();

        List<FourElementTuple<String, String, String, Long>> validList = null;

        String returnStr = null;
        switch (dataType){
            case MachineLearningConfig.SINGLE_VISIT:
            case MachineLearningConfig.MULTI_VISIT_BEFORE_TARGET_VISIT:
                throw new Exception("To Be Done");
            case MachineLearningConfig.MULTI_VISIT_BEFORE_AND_EQUAL_TARGET_VISIT: {
                validList = getVisitListBeforeOrEqualToTargetVisit(unifiedPatientID, hospitalCode, visitType, visitID);
                String unPreprocessedData = getDataFromDatabase(featureList, unifiedPatientID, validList);

                String fileName = String.valueOf(new Date().getTime());
                BufferedWriter writer = new BufferedWriter(new FileWriter(
                        machineLearningConfig.getHawkesRNNFolderPath()+fileName));
                writer.write(unPreprocessedData);
                writer.close();

                String command = "python " +
                        machineLearningConfig.getHawkesRNNFolderPath()+"data_convert.py " +
                        fileName + " " + predictTask;
                Process proc = Runtime.getRuntime().exec(command);
                proc.waitFor();

                BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                String line = null;
                while ((line = in.readLine()) != null) {
                    returnStr = line;
                    break;
                }
                in.close();
                proc.waitFor();
            }
        }

        return returnStr;
    }

    private String getDataFromDatabase(List<String> featureTypeList, String unifiedPatientID,
                                       List<FourElementTuple<String, String, String, Long>> visitList){
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
            singleVisitRequest.put("hospitalCode", hospitalCode);
            singleVisitRequest.put("visitType", visitType);
            singleVisitRequest.put("visitID", visitID);
            singleVisitRequest.put("unifiedPatientID", unifiedPatientID);

            // 此处的
            request.put(String.valueOf(globalAdmissionTime), singleVisitRequest);
        }
        System.out.println(request.toString());
        return request.toString();
    }

    private JSONObject getSingleVisitData(List<String> featureList, String unifiedPatientID, String hospitalCode,
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