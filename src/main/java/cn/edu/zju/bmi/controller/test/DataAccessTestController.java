package cn.edu.zju.bmi.controller.test;

import cn.edu.zju.bmi.entity.DAO.*;
import cn.edu.zju.bmi.repository.*;
import cn.edu.zju.bmi.service.IndividualAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path="/connection_test")
public class DataAccessTestController {
    private DiagnosisRepository diagnosisRepository;
    private LabTestRepository labTestRepository;
    private OperationRepository operationRepository;
    private OrdersRepository ordersRepository;
    private PatientVisitRepository patientVisitRepository;
    private VitalSignRepository vitalSignRepository;
    private PatientRepository patientRepository;
    private ExamRepository examRepository;
    private IndividualAnalysisService individualAnalysisService;

    @Autowired
    public DataAccessTestController(DiagnosisRepository diagnosisRepository, LabTestRepository labTestRepository,
                                    OperationRepository operationRepository, OrdersRepository ordersRepository,
                                    PatientRepository patientRepository, VitalSignRepository vitalSignRepository,
                                    PatientVisitRepository patientVisitRepository, ExamRepository examRepository,
                                    IndividualAnalysisService individualAnalysisService){
        this.diagnosisRepository = diagnosisRepository;
        this.labTestRepository = labTestRepository;
        this.operationRepository = operationRepository;
        this.ordersRepository = ordersRepository;
        this.patientRepository = patientRepository;
        this.vitalSignRepository = vitalSignRepository;
        this.patientVisitRepository = patientVisitRepository;
        this.examRepository = examRepository;
        this.individualAnalysisService = individualAnalysisService;
    }

    @GetMapping(value = "/diagnosis")
    public List<Diagnosis> getTop2Diagnosis() { return diagnosisRepository.findTop2ByOrderByKeyUnifiedPatientID(); }

    @GetMapping(value = "/lab_test")
    public List<LabTest> getTop2LabTest() {
        return labTestRepository.findTop2ByOrderByKeyUnifiedPatientID();
    }

    @GetMapping(value = "/operation")
    public List<Operation> getTop2Operation() {
        return operationRepository.findTop2ByOrderByKeyUnifiedPatientID();
    }

    @GetMapping(value = "/Orders")
    public List<Orders> getTop2Order() {
        return ordersRepository.findTop2ByOrderByKeyUnifiedPatientID();
    }

    @GetMapping(value = "/patient_visit")
    public List<PatientVisit> getTop2PatientVisit() {
        return patientVisitRepository.findTop2ByOrderByKeyUnifiedPatientID();
    }

    @GetMapping(value = "/vital_sign")
    public List<VitalSign> getTop2VitalSign() {
        return vitalSignRepository.findTop2ByOrderByKeyUnifiedPatientID();
    }

    @GetMapping(value = "/patient")
    public List<Patient> getTop2Patient() {
        return patientRepository.findTop2ByOrderByUnifiedPatientID();
    }

    @GetMapping(value = "/exam")
    public List<Exam> getTop2Exam() {
        return examRepository.findTop2ByOrderByKeyUnifiedPatientID();
    }
}
