package cn.edu.zju.bmi.repository;

import cn.edu.zju.bmi.entity.DAO.HospitalMap;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HospitalMapRepository extends JpaRepository<HospitalMap, String> {
    public HospitalMap findHospitalMapByHospitalCode(String hospitalCode);
}
