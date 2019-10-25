package cn.edu.zju.bmi.repository;

import cn.edu.zju.bmi.entity.DAO.Operation;
import cn.edu.zju.bmi.entity.DAO.key.OperationPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OperationRepository extends JpaRepository<Operation, OperationPrimaryKey> {
    List<Operation> findTop2ByOrderByKeyUnifiedPatientID();
    List<Operation> findByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCode(String unifiedPatientID,
                                                                                            String visitID,
                                                                                            String visitType,
                                                                                            String hospitalCode);
}
