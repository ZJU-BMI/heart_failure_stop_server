package cn.edu.zju.bmi.repository;

import cn.edu.zju.bmi.entity.DAO.IdMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdMappingRepository extends JpaRepository<IdMapping, String> {
    IdMapping findIdMappingByHospitalPatIDAndHospitalCode(String hospitalPatID,  String hospitalCode);
}
