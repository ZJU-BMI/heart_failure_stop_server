package cn.edu.zju.bmi.controller;

import cn.edu.zju.bmi.entity.POJO.*;
import cn.edu.zju.bmi.service.GetSinglePatInfoService;
import cn.edu.zju.bmi.support.ParameterName;
import cn.edu.zju.bmi.support.PathName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path= PathName.TRAJECTORY_ANALYSIS_DATA)
public class DataAccessController {
    private GetSinglePatInfoService getSinglePatInfoService;

    @Autowired
    public DataAccessController(GetSinglePatInfoService getSinglePatInfoService){
        this.getSinglePatInfoService = getSinglePatInfoService;
    }

    @GetMapping(value = PathName.TRAJECTORY_ANALYSIS_DATA_UNIFIED_PATIENT_ID)
    public String getUnifiedPatientID(@RequestParam(ParameterName.PATIENT_ID) String patientID,
                                      @RequestParam(ParameterName.HOSPITAL_CODE) String hospitalCode){
        return getSinglePatInfoService.getUnifiedPatientID(patientID, hospitalCode);
    }

    @GetMapping(value = PathName.TRAJECTORY_ANALYSIS_DATA_BASIC_INFO)
    public PatientBasicInfo getPatientBasicInfo(@RequestParam(ParameterName.UNIFIED_PATIENT_ID) String unifiedPatientID){
        return getSinglePatInfoService.getPatientBasicInfo(unifiedPatientID);
    }

    @GetMapping(value = PathName.TRAJECTORY_ANALYSIS_DATA_TRAJECTORY)
    public List<VisitInTrajectory> getTrajectory(@RequestParam(ParameterName.UNIFIED_PATIENT_ID) String unifiedPatientID){
        return getSinglePatInfoService.getPatientTrajectory(unifiedPatientID);
    }

    @GetMapping(value = PathName.TRAJECTORY_ANALYSIS_DATA_VISIT_DETAILED_INFO)
    public VisitDetailedInfo getVisitDetailedInfo(@RequestParam(ParameterName.UNIFIED_PATIENT_ID) String unifiedPatientID,
                                                  @RequestParam(ParameterName.HOSPITAL_CODE) String hospitalCode,
                                                  @RequestParam(ParameterName.VISIT_TYPE) String visitType,
                                                  @RequestParam(ParameterName.VISIT_ID) String visitID){
        return getSinglePatInfoService.getVisitDetailedInfo(unifiedPatientID, hospitalCode, visitType, visitID);
    }

    @GetMapping(value = PathName.TRAJECTORY_ANALYSIS_DATA_VISIT_BRIEF_INFO)
    public VisitBriefInfo getVisitBriefInfo(@RequestParam(ParameterName.UNIFIED_PATIENT_ID) String unifiedPatientID,
                                            @RequestParam(ParameterName.HOSPITAL_CODE) String hospitalCode,
                                            @RequestParam(ParameterName.VISIT_TYPE) String visitType,
                                            @RequestParam(ParameterName.VISIT_ID) String visitID){
        return getSinglePatInfoService.getVisitBriefInfo(unifiedPatientID, hospitalCode, visitType, visitID);
    }

    @GetMapping(value = PathName.TRAJECTORY_ANALYSIS_DATA_LAB_TEST)
    public Map<String, List<LabTestResult>> getLabTest(@RequestParam(ParameterName.UNIFIED_PATIENT_ID) String unifiedPatientID,
                                                       @RequestParam(ParameterName.HOSPITAL_CODE) String hospitalCode,
                                                       @RequestParam(ParameterName.VISIT_TYPE) String visitType,
                                                       @RequestParam(ParameterName.VISIT_ID) String visitID){
        return getSinglePatInfoService.getLabTest(unifiedPatientID, hospitalCode, visitType, visitID);
    }

    @GetMapping(value = PathName.TRAJECTORY_ANALYSIS_DATA_MEDICAL_ORAL_INTERVENTION)
    public Map<String, List<MedicalOralIntervention>> getMedicalOralIntervention(@RequestParam(ParameterName.UNIFIED_PATIENT_ID) String unifiedPatientID,
                                                                                 @RequestParam(ParameterName.HOSPITAL_CODE) String hospitalCode,
                                                                                 @RequestParam(ParameterName.VISIT_TYPE) String visitType,
                                                                                 @RequestParam(ParameterName.VISIT_ID) String visitID){
        return getSinglePatInfoService.getMedicalOralIntervention(unifiedPatientID, hospitalCode, visitType, visitID);
    }

    @GetMapping(value = PathName.TRAJECTORY_ANALYSIS_DATA_VITAL_SIGN)
    public Map<String, List<VitalSignResult>> getVitalSignOfVisit(@RequestParam(ParameterName.UNIFIED_PATIENT_ID) String unifiedPatientID,
                                                                  @RequestParam(ParameterName.HOSPITAL_CODE) String hospitalCode,
                                                                  @RequestParam(ParameterName.VISIT_TYPE) String visitType,
                                                                  @RequestParam(ParameterName.VISIT_ID) String visitID){
        return getSinglePatInfoService.getVitalSignOfVisit(unifiedPatientID, hospitalCode, visitType, visitID);
    }

    @GetMapping(value = PathName.TRAJECTORY_ANALYSIS_DATA_EXAM)
    public List<ExamResult> getExamResult(@RequestParam(ParameterName.UNIFIED_PATIENT_ID) String unifiedPatientID,
                                          @RequestParam(ParameterName.HOSPITAL_CODE) String hospitalCode,
                                          @RequestParam(ParameterName.VISIT_TYPE) String visitType,
                                          @RequestParam(ParameterName.VISIT_ID) String visitID){
        return getSinglePatInfoService.getExamResult(unifiedPatientID, hospitalCode, visitType, visitID);
    }

}
