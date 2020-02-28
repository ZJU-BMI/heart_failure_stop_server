package cn.edu.zju.bmi.repository;

import cn.edu.zju.bmi.entity.DAO.VitalSign;
import cn.edu.zju.bmi.entity.DAO.key.VitalSignPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface VitalSignRepository extends JpaRepository<VitalSign, VitalSignPrimaryKey> {
    List<VitalSign> findTop2ByOrderByKeyUnifiedPatientID();
    List<VitalSign> findByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCode(String unifiedPatientID,
                                                                                            String visitID,
                                                                                            String visitType,
                                                                                            String hospitalCode);

    List<VitalSign> findByKeyVitalSign(String key_vitalSign);
}
