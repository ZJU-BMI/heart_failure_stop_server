package cn.edu.zju.bmi.repository;

import cn.edu.zju.bmi.entity.DAO.Orders;
import cn.edu.zju.bmi.entity.DAO.key.OrdersPrimaryKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, OrdersPrimaryKey> {
    List<Orders> findTop2ByOrderByKeyUnifiedPatientID();
    List<Orders> findByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCode(String unifiedPatientID,
                                                                                         String visitID,
                                                                                         String visitType,
                                                                                         String hospitalCode);

    List<Orders> findByKeyUnifiedPatientIDAndKeyVisitIDAndKeyVisitTypeAndKeyHospitalCodeAndOrderClass(
            String unifiedPatientID,
            String visitID,
            String visitType,
            String hospitalCode,
            String orderClass);
}
