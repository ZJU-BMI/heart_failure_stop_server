package cn.edu.zju.bmi.repository;

import cn.edu.zju.bmi.entity.DAO.LabTestNameDict;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabTestNameRepository extends JpaRepository<LabTestNameDict, String> {
    public List<LabTestNameDict> findAll();
}
