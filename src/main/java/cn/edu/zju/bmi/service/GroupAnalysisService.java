package cn.edu.zju.bmi.service;
import cn.edu.zju.bmi.entity.DAO.*;
import cn.edu.zju.bmi.entity.POJO.VisitIdentifier;
import cn.edu.zju.bmi.entity.POJO.VisitInfoForGroupAnalysis;
import cn.edu.zju.bmi.repository.*;
import cn.edu.zju.bmi.support.ParameterName;
import cn.edu.zju.bmi.support.TwoElementTuple;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class GroupAnalysisService {
    private VisitIdentifierRepository visitIdentifierRepository;
    private VitalSignRepository vitalSignRepository;

    // 由于此处需要翻页，由于我们的功能无法直接通过Pageable API实现，因此需要将查询结果缓存起来
    // 为了防止内存溢出，设定只同时支持对5个查询进行缓存，多出的即删除
    private CacheQueue cachePool;

    // 以下4个Map一直驻留，不可改变，用于在筛选出合适的visit后，快速的获取展示Visit所需的信息
    private Map<String, PatientVisit> patientVisitMap;
    private Map<String, Patient> patientMap;
    private Map<String, List<Diagnosis>> diagnosisMap;
    private Map<String, String> hospitalMap;
    private Map<String, String> idMap;

    private SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd" );

    @Autowired
    public GroupAnalysisService(VisitIdentifierRepository visitIdentifierRepository,
                                PatientRepository patientRepository,
                                PatientVisitRepository patientVisitRepository,
                                DiagnosisRepository diagnosisRepository,
                                HospitalMapRepository hospitalMapRepository,
                                IdMappingRepository idMappingRepository,
                                VitalSignRepository vitalSignRepository
                                )
    {
        this.visitIdentifierRepository = visitIdentifierRepository;
        this.vitalSignRepository = vitalSignRepository;
        this.cachePool = new CacheQueue(5);

        List<PatientVisit> patientVisitList = patientVisitRepository.findAll();
        List<Patient> patientList = patientRepository.findAll();
        List<Diagnosis> diagnosisList = diagnosisRepository.findAll();
        List<HospitalMap> hospitalMapList = hospitalMapRepository.findAll();
        List<IdMapping> idMappingList = idMappingRepository.findAll();

        patientVisitMap = new HashMap<>();
        for(PatientVisit patientVisit: patientVisitList){
            String hospitalCode = patientVisit.getKey().getHospitalCode();
            String unifiedPatientID = patientVisit.getKey().getUnifiedPatientID();
            String visitID = patientVisit.getKey().getVisitID();
            String visitType = patientVisit.getKey().getVisitType();
            String id = unifiedPatientID+"_"+hospitalCode+"_"+visitType+"_"+visitID;
            patientVisitMap.put(id, patientVisit);
        }

        patientMap = new HashMap<>();
        for(Patient patient: patientList){
            String unifiedPatientID = patient.getUnifiedPatientID();
            patientMap.put(unifiedPatientID, patient);
        }

        diagnosisMap = new HashMap<>();
        for(Diagnosis diagnosis: diagnosisList){
            String hospitalCode = diagnosis.getKey().getHospitalCode();
            String unifiedPatientID = diagnosis.getKey().getUnifiedPatientID();
            String visitID = diagnosis.getKey().getVisitID();
            String visitType = diagnosis.getKey().getVisitType();
            String id = unifiedPatientID+"_"+hospitalCode+"_"+visitType+"_"+visitID;
            if(!diagnosisMap.containsKey(id)){
                diagnosisMap.put(id, new ArrayList<>());
            }
            diagnosisMap.get(id).add(diagnosis);
        }

        hospitalMap = new HashMap<>();
        for(HospitalMap hospital: hospitalMapList){
            hospitalMap.put(hospital.getHospitalCode(), hospital.getHospitalName());
        }

        idMap = new HashMap<>();
        for(IdMapping idMapping: idMappingList){
            String hospitalCode = idMapping.getHospitalCode();
            String unifiedPatientID = idMapping.getUnifiedPatientID();
            String localPatientID = idMapping.getHospitalPatID();
            idMap.put(unifiedPatientID+"_"+hospitalCode, localPatientID);
        }
    }

    public List<VisitInfoForGroupAnalysis> getVisitInfoForGroupAnalysisList(String filter, String userName,
                                                                            Long timeStamp, Integer startIdx,
                                                                            Integer endIdx) throws Exception {
        // 从群体到患者个体需要缓存查询结果以辅助分页（这部分没办法通过Spring 原生API做完）
        // 因此用userName+timeStamp做缓存结果的ID
        String id = userName+"_"+timeStamp;
        if(cachePool.contains(id)){
            return cachePool.getContent(id).subList(startIdx, endIdx);
        }
        else{
            List<VisitIdentifier> targetList =  parseFilterAndSearchVisit(filter);
            List<VisitInfoForGroupAnalysis> visitInfoForGroupAnalysisList = getVisitInfoForGroupAnalysis(targetList);
            cachePool.add(id, visitInfoForGroupAnalysisList);
            return cachePool.getContent(id).subList(startIdx, endIdx);
        }
    }

    private List<VisitInfoForGroupAnalysis> getVisitInfoForGroupAnalysis(List<VisitIdentifier> targetList){
        List<VisitInfoForGroupAnalysis> visitInfoForGroupAnalysisList = new ArrayList<>();
        for(VisitIdentifier visitIdentifier: targetList){
            String unifiedPatientID = visitIdentifier.getUnifiedPatientID();
            String visitID = visitIdentifier.getVisitID();
            String hospitalCode = visitIdentifier.getHospitalCode();
            String visitType = visitIdentifier.getVisitType();
            String id = unifiedPatientID+"_"+hospitalCode+"_"+visitType+"_"+visitID;

            // 要求targetList必须能在几个map中都能找到
            if(!diagnosisMap.containsKey(id)&&patientMap.containsKey(unifiedPatientID)&&
                    patientVisitMap.containsKey(id)&&idMap.containsKey(unifiedPatientID+"_"+hospitalCode)){
                continue;
            }
            String localPatientID = idMap.get(unifiedPatientID+"_"+hospitalCode);
            String name = patientMap.get(unifiedPatientID).getName();
            String hospitalName = hospitalMap.get(hospitalCode);
            String sex = patientMap.get(unifiedPatientID).getSex();
            StringBuilder stringBuilder = new StringBuilder();
            for(Diagnosis diagnosis: diagnosisMap.get(id)){
                String diagnosisType = diagnosis.getKey().getDiagnosisType();
                if(diagnosisType.equals("3")){
                    stringBuilder.append(diagnosis.getDiagnosisDesc());
                }
            }
            String mainDiagnosis = stringBuilder.toString();

            Date admissionDateTime = patientVisitMap.get(id).getAdmissionDateTime();
            Date dischargeDateTime = patientVisitMap.get(id).getDischargeDateTime();
            String los = String.valueOf((int)Math.ceil((double)(dischargeDateTime.getTime()-
                    admissionDateTime.getTime())/1000/3600/24));

            String admissionTimeStr = sdf.format(admissionDateTime);
            Calendar admissionTime = Calendar.getInstance();
            admissionTime.setTime(admissionDateTime);
            int toYear = admissionTime.get(Calendar.YEAR);

            Date birthday = patientMap.get(unifiedPatientID).getBirthday();
            Calendar birthdayCalender = Calendar.getInstance();
            birthdayCalender.setTime(birthday);
            String age = String.valueOf(toYear-birthdayCalender.get(Calendar.YEAR));

            VisitInfoForGroupAnalysis visitInfoForGroupAnalysis =
                    new VisitInfoForGroupAnalysis(localPatientID, unifiedPatientID, name, hospitalCode, hospitalName,
                            visitID, visitType, sex, age, mainDiagnosis, los, admissionTimeStr);
            visitInfoForGroupAnalysisList.add(visitInfoForGroupAnalysis);
        }
        return visitInfoForGroupAnalysisList;
    }

    private List<VisitIdentifier> parseFilterAndSearchVisit(String filter) throws Exception {
        // filter 应当是Json格式，转换为对象后，应当是一个list，list中的每一项的第一个元素代表了filter类型，之后的项代表了
        // 具体的过滤细节，依据每中filter自行决定。
        List<List<VisitIdentifier>> list = new ArrayList<>();
        // 初始化包括所有人的信息

        JSONObject jo = new JSONObject(filter);
        JSONArray jsonArray = (JSONArray) jo.get("filter");
        for(int i=0; i<jsonArray.length();i++){
            JSONArray item = (JSONArray) jsonArray.get(i);
            String itemName = item.getString(0);
            switch (itemName){
                case ParameterName.AGE: list.add(parseAgeQuery(item)); break;
                case ParameterName.SEX: list.add(parseSex(item));break;
                case ParameterName.VITAL_SIGN: list.add(parseVitalSignAndUsingFirstRecordOfVisit(item)); break;
                case ParameterName.LAB_TEST: break;
                case ParameterName.ADMISSION_TIME: break;
                case ParameterName.BIRTHDAY: break;
                case ParameterName.HOSPITAL: break;
                case ParameterName.MAIN_DIAGNOSIS: break;
                case ParameterName.DIAGNOSIS: break;
                case ParameterName.OPERATION: break;
                case ParameterName.MEDICINE: break;
                case ParameterName.MACHINE_LEARNING_MODEL: break;
                case ParameterName.EXAM: break;
                default: break;
            }
        }
        return convertToLegalVisitList(list);
    }

    private List<VisitIdentifier> convertToLegalVisitList(List<List<VisitIdentifier>> list ){
        // 只保留符合每个过滤器的患者
        Map<String, TwoElementTuple<Integer, VisitIdentifier>> map = new HashMap<>();
        for(List<VisitIdentifier> list1: list){
            for(VisitIdentifier visitIdentifier: list1){
                String id = visitIdentifier.getUnifiedPatientID()+"_"+visitIdentifier.getHospitalCode()+"_"+
                        visitIdentifier.getVisitType()+"_"+visitIdentifier.getVisitID();
                if(!map.containsKey(id)){
                    map.put(id, new TwoElementTuple<>(1, visitIdentifier));
                }
                else {
                    map.put(id, new TwoElementTuple<>(map.get(id).getA()+1, visitIdentifier));
                }
            }
        }

        List<VisitIdentifier> targetList = new ArrayList<>();
        for(String id: map.keySet()){
            // 当id的计数和总数一致时，说明每个filter中都有这次访问
            int count = map.get(id).getA();
            if(count==list.size()){
                targetList.add(map.get(id).getB());
            }
        }
        return targetList;
    }

    private List<VisitIdentifier> parseSex(JSONArray item){
        String sex = item.get(1).equals("male")?"男":"女";
        return visitIdentifierRepository.findVisitBySex(sex);
    }

    private List<VisitIdentifier> parseAgeQuery(JSONArray item){
        // item = ["age", int low_threshold, int high_threshold]

        // 此处由于一些技术原因，需要用365往上乘
        int maxAge = 365*(int)item.get(2);
        int minAge = 365*(int)item.get(1);
        List<VisitIdentifier> list;
        if(maxAge!=-365&&minAge!=-365){
            list = visitIdentifierRepository.findVisitIdentifierByAgeBetween(minAge, maxAge);
        }
        else if(maxAge==-365&&minAge==-365){
            list = new ArrayList<>();
            System.out.println("maxAge and minAge not set");
        }
        else if(maxAge!=-365){
            list = visitIdentifierRepository.findAllVisit();
        }
        else {
            list = visitIdentifierRepository.findVisitIdentifierByAgeLargerThan(minAge);
        }
        return list;
    }

    private List<VisitIdentifier> parseVitalSignAndUsingFirstRecordOfVisit(JSONArray item) throws Exception {
        // 这个函数由于要遍历vitalSign表，可能存在内存溢出风险，注意
        // item = ["vitalSign", vitalSignType, low_threshold, high_threshold]
        // 如果上限或下限未被设定，则取-1，代表全要，但是最好在前端禁止这种操作
        String vitalSignType = (String)item.get(1);
        int highThreshold = Integer.parseInt((String)item.get(3));
        int lowThreshold = Integer.parseInt((String)item.get(2));

        switch (vitalSignType) {
            case ParameterName.SYSTOLIC_BLOOD_PRESSURE: {
                Map<String, VitalSign> map = getVitalSignFirstRecordOfVisit("血压high", lowThreshold, highThreshold);
                return mapToList(map);
            }
            case ParameterName.DIASTOLIC_BLOOD_PRESSURE: {
                Map<String, VitalSign> map = getVitalSignFirstRecordOfVisit("血压Low", lowThreshold, highThreshold);
                return mapToList(map);
            }
            case ParameterName.BMI: {
                Map<String, VitalSign> heightMap = getVitalSignFirstRecordOfVisit("身高", -1, -1);
                Map<String, VitalSign> weightMap = getVitalSignFirstRecordOfVisit("体重", -1, -1);
                List<VisitIdentifier> list = new ArrayList<>();
                for(String id: heightMap.keySet()){
                    if(!weightMap.containsKey(id)){
                        continue;
                    }
                    double height = heightMap.get(id).getResult();
                    double weight = weightMap.get(id).getResult();
                    double bmi = weight/(height*height)*10000;
                    VitalSign vitalSign = heightMap.get(id);
                    String unifiedPatientID = vitalSign.getKey().getUnifiedPatientID();
                    String visitType = vitalSign.getKey().getVisitType();
                    String visitID = vitalSign.getKey().getVisitID();
                    String hospitalCode = vitalSign.getKey().getHospitalCode();

                    if(highThreshold!=-1&&lowThreshold!=-1){
                        if(bmi<highThreshold&&bmi>lowThreshold){
                            list.add(new VisitIdentifier(unifiedPatientID, visitType, visitID, hospitalCode));
                        }
                    }
                    else if(highThreshold==-1&&lowThreshold==-1){
                        list.add(new VisitIdentifier(unifiedPatientID, visitType, visitID, hospitalCode));
                    }
                    else if(highThreshold!=-1){
                        if(bmi<highThreshold&&bmi>12){
                            list.add(new VisitIdentifier(unifiedPatientID, visitType, visitID, hospitalCode));
                        }
                    }
                    else {
                        if(bmi>lowThreshold&&bmi<35){
                            list.add(new VisitIdentifier(unifiedPatientID, visitType, visitID, hospitalCode));
                        }
                    }
                }
                return list;
            }
            default:
                throw new Exception("no case matched");
        }
    }

    private List<VisitIdentifier> mapToList(Map<String, VitalSign> map){
        List<VisitIdentifier> list = new ArrayList<>();
        for(String key: map.keySet()){
            VitalSign vitalSign = map.get(key);
            String unifiedPatientID = vitalSign.getKey().getUnifiedPatientID();
            String visitType = vitalSign.getKey().getVisitType();
            String visitID = vitalSign.getKey().getVisitID();
            String hospitalCode = vitalSign.getKey().getHospitalCode();
            list.add(new VisitIdentifier(unifiedPatientID, visitType, visitID, hospitalCode));
        }
        return list;
    }

    private Map<String, VitalSign> getVitalSignFirstRecordOfVisit(String type, int lowThreshold, int highThreshold){
        List<VitalSign> list;
        if(highThreshold!=-1&&lowThreshold!=-1){
            list = vitalSignRepository.findByKeyVitalSignAndResultLessThanAndResultGreaterThan(
                    type, highThreshold, lowThreshold);
        }
        else if(highThreshold==-1&&lowThreshold==-1){
            list = vitalSignRepository.findByKeyVitalSign(type);
        }
        else if(highThreshold!=-1){
            list = vitalSignRepository.findByKeyVitalSignAndResultLessThan(type, highThreshold);
        }
        else {
            list = vitalSignRepository.findByKeyVitalSignAndResultGreaterThan(type, lowThreshold);
        }

        Map<String, VitalSign> map = new HashMap<>();
        for(VitalSign vitalSign: list){
            String unifiedPatientID = vitalSign.getKey().getUnifiedPatientID();
            String visitType = vitalSign.getKey().getVisitType();
            String visitID = vitalSign.getKey().getVisitID();
            String hospitalCode = vitalSign.getKey().getHospitalCode();
            String id = unifiedPatientID+"_"+hospitalCode+"_"+visitType+"_"+visitID;
            if(!map.containsKey(id)){
                map.put(id, vitalSign);
            }
            else{
                if(map.get(id).getRecordTime().after(vitalSign.getRecordTime())){
                    map.put(id, vitalSign);
                }
            }
        }
        return map;
    }
}

class CacheQueue{
    private List<TwoElementTuple<String, List<VisitInfoForGroupAnalysis>>> list;
    private int maxSize;
    CacheQueue(int maxSize){
        this.list = new ArrayList<>();
        this.maxSize = maxSize;
    }

    List<VisitInfoForGroupAnalysis> getContent(String id){
        for(TwoElementTuple<String, List<VisitInfoForGroupAnalysis>> tuple: list){
            if(id.equals(tuple.getA())){
                return tuple.getB();
            }
        }
        return new ArrayList<>();
    }

    void add(String newId, List<VisitInfoForGroupAnalysis> content){
        list.add(0, new TwoElementTuple<>(newId, content));
        if(list.size()>maxSize){
            list = list.subList(0, maxSize);
        }
    }

    boolean contains(String newId){
        boolean isIdContained = false;
        for(TwoElementTuple<String, List<VisitInfoForGroupAnalysis>> tuple: list){
            String id = tuple.getA();
            if (id.equals(newId)) {
                isIdContained = true;
                break;
            }
        }
        return isIdContained;
    }
}
