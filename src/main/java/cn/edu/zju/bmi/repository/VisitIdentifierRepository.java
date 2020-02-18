package cn.edu.zju.bmi.repository;

import cn.edu.zju.bmi.entity.POJO.VisitIdentifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VisitIdentifierRepository extends JpaRepository<VisitIdentifier, Long> {
    // 用于群体分析过滤器所需的多表查询
    @Query(value = "select " +
            "new cn.edu.zju.bmi.entity.POJO.VisitIdentifier(" +
            "u.key.unifiedPatientID, u.key.hospitalCode, u.key.visitType, u.key.visitID)"
            + "from cn.edu.zju.bmi.entity.DAO.PatientVisit u, cn.edu.zju.bmi.entity.DAO.Patient l " +
            "where u.key.unifiedPatientID = l.unifiedPatientID and " +
            "?1<= (FUNCTION('DATEDIFF', u.admissionDateTime, l.birthday)) and " +
            "FUNCTION('DATEDIFF', u.admissionDateTime, l.birthday) <= ?2")
    List<VisitIdentifier> findVisitIdentifierByAgeBetween(int minAge, int maxAge);

    @Query(value = "select " +
            "new cn.edu.zju.bmi.entity.POJO.VisitIdentifier(" +
            "u.key.unifiedPatientID, u.key.hospitalCode, u.key.visitType, u.key.visitID)"
            + "from cn.edu.zju.bmi.entity.DAO.PatientVisit u, cn.edu.zju.bmi.entity.DAO.Patient l " +
            "where u.key.unifiedPatientID = l.unifiedPatientID and " +
            "?1<= (FUNCTION('DATEDIFF', u.admissionDateTime, l.birthday))")
    List<VisitIdentifier> findVisitIdentifierByAgeLargerThan(int minAge);

    @Query(value = "select " +
            "new cn.edu.zju.bmi.entity.POJO.VisitIdentifier(" +
            "u.key.unifiedPatientID, u.key.hospitalCode, u.key.visitType, u.key.visitID)"
            + "from cn.edu.zju.bmi.entity.DAO.PatientVisit u, cn.edu.zju.bmi.entity.DAO.Patient l " +
            "where u.key.unifiedPatientID = l.unifiedPatientID and " +
            "?1>= (FUNCTION('DATEDIFF', u.admissionDateTime, l.birthday))")
    List<VisitIdentifier> findVisitIdentifierByAgeSmallerThan(int maxAge);

    @Query(value = "select " +
            "new cn.edu.zju.bmi.entity.POJO.VisitIdentifier(" +
            "u.key.unifiedPatientID, u.key.hospitalCode, u.key.visitType, u.key.visitID)"
            + "from cn.edu.zju.bmi.entity.DAO.PatientVisit u, cn.edu.zju.bmi.entity.DAO.Patient l " +
            "where l.sex=:sex and l.unifiedPatientID=u.key.unifiedPatientID")
    List<VisitIdentifier> findVisitBySex(@Param("sex") String sex);

    @Query(value = "select " +
            "new cn.edu.zju.bmi.entity.POJO.VisitIdentifier(" +
            "u.key.unifiedPatientID, u.key.hospitalCode, u.key.visitType, u.key.visitID)"
            + "from cn.edu.zju.bmi.entity.DAO.PatientVisit u, cn.edu.zju.bmi.entity.DAO.Patient l " +
            "where u.key.hospitalCode=:hospitalCode and l.unifiedPatientID=u.key.unifiedPatientID")
    List<VisitIdentifier> findVisitByHospitalCode(@Param("hospitalCode") String hospitalCode);
}
