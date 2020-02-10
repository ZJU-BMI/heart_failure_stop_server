package cn.edu.zju.bmi.repository;

import cn.edu.zju.bmi.entity.DAO.Diagnosis;
import cn.edu.zju.bmi.entity.DAO.key.DiagnosisPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, DiagnosisPrimaryKey> {
    List<Diagnosis> findTop2ByOrderByKeyUnifiedPatientID();

    List<Diagnosis> findAllByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCodeAndKeyDiagnosisTypeOrderByKeyDiagnosisNo(
            String unifiedPatientID, String visitID, String visitType, String hospitalCode, String diagnosisCode);

    List<Diagnosis> findAllByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCode(
            String unifiedPatientID, String visitID, String visitType, String hospitalCode);

    @Query(value = "select new cn.edu.zju.bmi.entity.DAO.Diagnosis(u.key.unifiedPatientID, u.key.hospitalCode, " +
            "u.key.visitType, u.key.visitID, u.key.diagnosisType, u.key.diagnosisNo, u.diagnosisDesc, " +
            "u.diagnosisCode, u.codeVersion) from cn.edu.zju.bmi.entity.DAO.Diagnosis u " +
            "where (u.key.diagnosisType=?2 or u.key.diagnosisType=?3) and u.diagnosisCode like ?1")
    List<Diagnosis> findByDiagnosis(String diagnosisCode, String type1, String type2);

    @Query(value = "select new cn.edu.zju.bmi.entity.DAO.Diagnosis(u.key.unifiedPatientID, u.key.hospitalCode, " +
            "u.key.visitType, u.key.visitID, u.key.diagnosisType, u.key.diagnosisNo, u.diagnosisDesc, " +
            "u.diagnosisCode, u.codeVersion) from cn.edu.zju.bmi.entity.DAO.Diagnosis u " +
            "where u.key.diagnosisType=?2 and u.diagnosisCode like ?1")
    List<Diagnosis> findByMainDiagnosis(String diagnosisCode, String type);
}
