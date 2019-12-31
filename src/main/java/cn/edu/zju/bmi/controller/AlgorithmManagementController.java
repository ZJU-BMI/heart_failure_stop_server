package cn.edu.zju.bmi.controller;
import cn.edu.zju.bmi.entity.DAO.MachineLearningModel;
import cn.edu.zju.bmi.service.AlgorithmManagementService;
import cn.edu.zju.bmi.support.ParameterName;
import cn.edu.zju.bmi.support.PathName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import java.util.List;


@RestController
@RequestMapping(path= PathName.ALGORITHM_MANAGEMENT)
@RolesAllowed("ROLE_USER")
public class AlgorithmManagementController {
    /*
    此处模型的所有方法组成了两个模块的功能
    1.  模型上传至Spring Boot，在Spring Boot的Model Save Path下保存，更新，删除模型副本，
        包括更改数据库信息。这部分内容是平台无关的，任意平台的模型都应当被保存为4个模块（模型，数据配置，预处理，文档）
    2.  模型的具体部署。这一部分是平台相关的。在当前我们仅提供对Tensorflow的支持
* */
    private AlgorithmManagementService algorithmManagementService;

    @Autowired
    public AlgorithmManagementController(AlgorithmManagementService algorithmManagementService){
        this.algorithmManagementService=algorithmManagementService;
    }

    @PostMapping(value = PathName.CREATE_NEW_MODEL)
    public ResponseEntity createModel(
            @RequestParam(ParameterName.USER_NAME) String user,
            @RequestParam(ParameterName.ACCESS_CONTROL) String accessControl,
            @RequestParam(ParameterName.PLATFORM) String platform,
            @RequestParam(ParameterName.MODEL_NAME_CHINESE) String modelNameChinese,
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelNameEnglish,
            @RequestParam(ParameterName.MAIN_CATEGORY) String mainCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_CHINESE) String modelFunctionChinese,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunctionEnglish,
            @RequestParam(ParameterName.MODEL_FILE) MultipartFile modelFile,
            @RequestParam(ParameterName.MODEL_DOC) MultipartFile modelDoc,
            @RequestParam(ParameterName.MODEL_PREPROCESS) MultipartFile modelPreprocess,
            @RequestParam(ParameterName.MODEL_CONFIG) MultipartFile modelConfig){
        return algorithmManagementService.createNewModel(modelNameChinese, modelNameEnglish, mainCategory,
                modelFunctionChinese, modelFunctionEnglish, platform, user, accessControl, modelFile,modelDoc,
                modelPreprocess, modelConfig);
    }

    @PostMapping(value = PathName.UPLOAD_MODEL_FILE)
    public ResponseEntity updateModelFile(
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelName,
            @RequestParam(ParameterName.MAIN_CATEGORY) String mainCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunction,
            @RequestParam(ParameterName.MODEL_FILE) MultipartFile modelFile){
        return algorithmManagementService.updateModelFile(mainCategory, modelName, modelFunction, modelFile);
    }

    @PostMapping(value = PathName.UPLOAD_MODEL_DOCUMENT)
    public ResponseEntity updateDoc(
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelName,
            @RequestParam(ParameterName.MAIN_CATEGORY) String mainCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunction,
            @RequestParam(ParameterName.MODEL_DOC) MultipartFile modelDoc){
        return algorithmManagementService.updateModelDoc(mainCategory, modelName, modelFunction, modelDoc);
    }

    @PostMapping(value = PathName.UPLOAD_MODEL_CONFIG)
    public ResponseEntity updateConfig(
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelName,
            @RequestParam(ParameterName.MAIN_CATEGORY) String mainCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunction,
            @RequestParam(ParameterName.MODEL_CONFIG) MultipartFile modelConfig){
        return algorithmManagementService.updateModelConfig(mainCategory, modelName, modelFunction, modelConfig);
    }

    @PostMapping(value = PathName.UPLOAD_PREPROCESSING_MODULE)
    public ResponseEntity updatePreprocess(
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelName,
            @RequestParam(ParameterName.MAIN_CATEGORY) String mainCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunction,
            @RequestParam(ParameterName.MODEL_PREPROCESS) MultipartFile preprocess){
        return algorithmManagementService.updatePreprocess(mainCategory, modelName, modelFunction, preprocess);
    }

    @GetMapping(value = PathName.DOWNLOAD_MODEL_FILE)
    public ResponseEntity downloadModel(
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelName,
            @RequestParam(ParameterName.MAIN_CATEGORY) String mainCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunction ) {
        return algorithmManagementService.readModelFile(mainCategory, modelName, modelFunction);
    }

    @GetMapping(value = PathName.DOWNLOAD_MODEL_CONFIG)
    public ResponseEntity downloadModelConfig(
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelName,
            @RequestParam(ParameterName.MAIN_CATEGORY) String mainCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunction ) {
        return algorithmManagementService.readModelConfig(mainCategory, modelName, modelFunction);
    }

    @GetMapping(value = PathName.DOWNLOAD_MODEL_DOCUMENT)
    public ResponseEntity downloadModelDoc(
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelName,
            @RequestParam(ParameterName.MAIN_CATEGORY) String mainCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunction ) {
        return algorithmManagementService.readModelDoc(mainCategory, modelName, modelFunction);
    }

    @GetMapping(value = PathName.DOWNLOAD_PREPROCESSING_MODULE)
    public ResponseEntity downloadPreprocessing(
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelName,
            @RequestParam(ParameterName.MAIN_CATEGORY) String mainCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunction ) {
        return algorithmManagementService.readPreprocessModule(mainCategory, modelName, modelFunction);
    }

    @GetMapping(value = PathName.DELETE_EXIST_MODEL)
    public ResponseEntity deleteModel(
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelName,
            @RequestParam(ParameterName.MAIN_CATEGORY) String mainCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunction ) {
        return algorithmManagementService.deleteModel(mainCategory, modelName, modelFunction);
    }

    @GetMapping(value = PathName.FETCH_MODEL_LIST)
    public List<MachineLearningModel> fetchModelList() {
        return algorithmManagementService.fetchModelList();
    }
}
