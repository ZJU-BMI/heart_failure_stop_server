package cn.edu.zju.bmi.repository;

import cn.edu.zju.bmi.entity.DAO.PatientVisit;
import cn.edu.zju.bmi.entity.DAO.key.PatientVisitPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PatientVisitRepository extends JpaRepository<PatientVisit, PatientVisitPrimaryKey> {
    List<PatientVisit> findTop2ByOrderByKeyUnifiedPatientID();
    List<PatientVisit> findAllByKeyUnifiedPatientID(String unifiedPatientID);
    PatientVisit findPatientVisitByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCode(String unifiedPatientID,
                                                                                                     String visitID,
                                                                                                     String visitType,
                                                                                                     String hospitalCode);
}
