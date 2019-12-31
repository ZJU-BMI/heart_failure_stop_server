package cn.edu.zju.bmi.repository;

import cn.edu.zju.bmi.entity.DAO.MachineLearningModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MachineLearningModelRepository extends JpaRepository<MachineLearningModel, Integer> {
    List<MachineLearningModel> findAll();
    MachineLearningModel findFirstByModelEnglishFunctionNameAndModelEnglishNameAndMainCategory(
            String modelEnglishFunctionName, String ModelEnglishName, String mainCategory
    );
    List<MachineLearningModel> findByPlatform(String platform);

    @Override
    void delete(MachineLearningModel entity);
}
