package cn.edu.zju.bmi.repository;

import cn.edu.zju.bmi.entity.DAO.Diagnosis;
import cn.edu.zju.bmi.entity.DAO.key.DiagnosisPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, DiagnosisPrimaryKey> {
    List<Diagnosis> findTop2ByOrderByKeyUnifiedPatientID();

    List<Diagnosis> findAllByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCodeAndKeyDiagnosisTypeOrderByKeyDiagnosisNo(
            String key_unifiedPatientID, String key_visitID, String key_visitType, String key_hospitalCode, String diagnosisCode);

}
