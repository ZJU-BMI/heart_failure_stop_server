package cn.edu.zju.bmi.repository;

import cn.edu.zju.bmi.entity.DAO.Diagnosis;
import cn.edu.zju.bmi.entity.DAO.key.DiagnosisPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, DiagnosisPrimaryKey> {
    List<Diagnosis> findTop2ByOrderByKeyUnifiedPatientID();

    List<Diagnosis> findAllByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCodeAndKeyDiagnosisTypeOrderByKeyDiagnosisNo(
            String unifiedPatientID, String visitID, String visitType, String hospitalCode, String diagnosisCode);

    List<Diagnosis> findAllByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCode(
            String unifiedPatientID, String visitID, String visitType, String hospitalCode);

}
