package cn.edu.zju.bmi.controller;
import cn.edu.zju.bmi.service.AlgorithmManagementService;
import cn.edu.zju.bmi.support.ParameterName;
import cn.edu.zju.bmi.support.PathName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;


@RestController
@RequestMapping(path= PathName.ALGORITHM_MANAGEMENT)
@RolesAllowed("ROLE_USER")
public class AlgorithmManagementController {
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
            @RequestParam(ParameterName.MAIN_CATEGORY) String modelCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_CHINESE) String modelFunctionChinese,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunctionEnglish,
            @RequestParam(ParameterName.MODEL_FILE) MultipartFile modelFile,
            @RequestParam(ParameterName.MODEL_DOC) MultipartFile modelDoc,
            @RequestParam(ParameterName.MODEL_PREPROCESS) MultipartFile modelPreprocess,
            @RequestParam(ParameterName.MODEL_CONFIG) MultipartFile modelConfig){
        return algorithmManagementService.createNewModel(modelNameChinese, modelNameEnglish, modelCategory,
                modelFunctionChinese, modelFunctionEnglish, platform, user, accessControl, modelFile,modelDoc,
                modelPreprocess, modelConfig);
    }

    @PostMapping(value = PathName.UPLOAD_MODEL_FILE)
    public ResponseEntity updateModelFile(
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelName,
            @RequestParam(ParameterName.MAIN_CATEGORY) String modelCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunction,
            @RequestParam(ParameterName.MODEL_FILE) MultipartFile modelFile){
        return algorithmManagementService.updateModelFile(modelCategory, modelName, modelFunction, modelFile);
    }

    @PostMapping(value = PathName.UPLOAD_MODEL_DOCUMENT)
    public ResponseEntity updateDoc(
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelName,
            @RequestParam(ParameterName.MAIN_CATEGORY) String modelCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunction,
            @RequestParam(ParameterName.MODEL_DOC) MultipartFile modelDoc){
        return algorithmManagementService.updateModelDoc(modelCategory, modelName, modelFunction, modelDoc);
    }

    @PostMapping(value = PathName.UPLOAD_MODEL_CONFIG)
    public ResponseEntity updateConfig(
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelName,
            @RequestParam(ParameterName.MAIN_CATEGORY) String modelCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunction,
            @RequestParam(ParameterName.MODEL_CONFIG) MultipartFile modelConfig){
        return algorithmManagementService.updateModelConfig(modelCategory, modelName, modelFunction, modelConfig);
    }

    @PostMapping(value = PathName.UPLOAD_PREPROCESSING_MODULE)
    public ResponseEntity updatePreprocess(
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelName,
            @RequestParam(ParameterName.MAIN_CATEGORY) String modelCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunction,
            @RequestParam(ParameterName.MODEL_PREPROCESS) MultipartFile preprocess){
        return algorithmManagementService.updatePreprocess(modelCategory, modelName, modelFunction, preprocess);
    }

    @GetMapping(value = PathName.DOWNLOAD_MODEL_FILE)
    public ResponseEntity downloadModel(
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelName,
            @RequestParam(ParameterName.MAIN_CATEGORY) String modelCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunction ) {
        return algorithmManagementService.readModelFile(modelCategory, modelName, modelFunction);
    }

    @GetMapping(value = PathName.DOWNLOAD_MODEL_CONFIG)
    public ResponseEntity downloadModelConfig(
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelName,
            @RequestParam(ParameterName.MAIN_CATEGORY) String modelCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunction ) {
        return algorithmManagementService.readModelConfig(modelCategory, modelName, modelFunction);
    }

    @GetMapping(value = PathName.DOWNLOAD_MODEL_DOCUMENT)
    public ResponseEntity downloadModelDoc(
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelName,
            @RequestParam(ParameterName.MAIN_CATEGORY) String modelCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunction ) {
        return algorithmManagementService.readModelDoc(modelCategory, modelName, modelFunction);
    }

    @GetMapping(value = PathName.DOWNLOAD_PREPROCESSING_MODULE)
    public ResponseEntity downloadPreprocessing(
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelName,
            @RequestParam(ParameterName.MAIN_CATEGORY) String modelCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunction ) {
        return algorithmManagementService.readPreprocessModule(modelCategory, modelName, modelFunction);
    }

    @GetMapping(value = PathName.DELETE_EXIST_MODEL)
    public ResponseEntity deleteModel(
            @RequestParam(ParameterName.MODEL_NAME_ENGLISH) String modelName,
            @RequestParam(ParameterName.MAIN_CATEGORY) String modelCategory,
            @RequestParam(ParameterName.MODEL_FUNCTION_ENGLISH) String modelFunction ) {
        return algorithmManagementService.deleteModel(modelCategory, modelName, modelFunction);
    }

}
