package cn.edu.zju.bmi.service;

import cn.edu.zju.bmi.entity.DAO.MachineLearningModel;
import cn.edu.zju.bmi.repository.MachineLearningModelRepository;
import cn.edu.zju.bmi.support.ParameterName;
import cn.edu.zju.bmi.support.StringResponse;
import cn.edu.zju.bmi.support.UnZipFiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class AlgorithmManagementService {
    @Value(value="${app.machineLearningModelRoot}")
    private String MODEL_SAVE_PATH;
    @Value(value="${app.tensorflowServerDataRoot}")
    private String TENSORFLOW_SERVER_PATH;
    private MachineLearningModelRepository machineLearningModelRepository;
    private String resourcePath = System.getProperty("user.dir")+"/src/main/resources";

    @Autowired
    public AlgorithmManagementService(MachineLearningModelRepository machineLearningModelRepository){
        this.machineLearningModelRepository = machineLearningModelRepository;
    }

    public ResponseEntity<?> updateAccessControl(String mainCategory, String modelName, String modelFunction,
                                                 String message){
        try {
            MachineLearningModel machineLearningModel =
                    machineLearningModelRepository.findFirstByModelEnglishFunctionNameAndModelEnglishNameAndMainCategory(
                            modelFunction, modelName, mainCategory
                    );
            machineLearningModel.setAccessControl(message);
            machineLearningModelRepository.save(machineLearningModel);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/text"))
                    .body("updateSuccess");
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/text"))
                    .body("updateFailed");
        }
    }

    public ResponseEntity<?> updatePlatform(String mainCategory, String modelName, String modelFunction,
                                                 String message){
        try {
            MachineLearningModel machineLearningModel =
                    machineLearningModelRepository.findFirstByModelEnglishFunctionNameAndModelEnglishNameAndMainCategory(
                            modelFunction, modelName, mainCategory
                    );
            machineLearningModel.setPlatform(message);
            machineLearningModelRepository.save(machineLearningModel);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/text"))
                    .body("updateSuccess");
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/text"))
                    .body("updateFailed");
        }
    }

    public MachineLearningModel getModelInfo(String mainCategory, String modelName, String modelFunction){
        return machineLearningModelRepository.findFirstByModelEnglishFunctionNameAndModelEnglishNameAndMainCategory(
                modelFunction, modelName, mainCategory);
    }

    public StringResponse createNewModel(String modelChineseName,
                                            String modelEnglishName,
                                            String mainCategory,
                                            String modelFunctionChinese,
                                            String modelFunctionEnglish,
                                            String platform,
                                            String user,
                                            String accessControl,
                                            MultipartFile modelFile,
                                            MultipartFile modelDoc,
                                            MultipartFile modelPreprocess){
        try {
            createNewFolder(mainCategory, modelEnglishName, modelFunctionEnglish);
            saveModelFile(mainCategory, modelEnglishName, modelFunctionEnglish, modelFile);
            saveModelDoc(mainCategory, modelEnglishName, modelFunctionEnglish, modelDoc);
            saveAndUnZipPreprocess(mainCategory, modelEnglishName, modelFunctionEnglish, modelPreprocess);

            Date date = new Date(System.currentTimeMillis());

            MachineLearningModel entity = new MachineLearningModel(user, mainCategory,
                    modelChineseName, modelEnglishName, modelFunctionChinese,
                    modelFunctionEnglish, platform, accessControl, date, date);
            machineLearningModelRepository.save(entity);

            modelDeploy(mainCategory, modelEnglishName, modelFunctionEnglish);
            return new StringResponse("createSuccess");
        }
        catch (Exception e){
            e.printStackTrace();
            return new StringResponse("createFailed");
        }
    }

    public ResponseEntity<?> updateModelFile(String mainCategory, String modelName, String modelFunction,
                                             MultipartFile modelFile){
        try {
            saveModelFile(mainCategory, modelName, modelFunction, modelFile);
            modelDeploy(mainCategory, modelName, modelFunction);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/text"))
                    .body("updateSuccess");
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/text"))
                    .body("updateFailed");
        }
    }

    private void saveModelFile(String mainCategory, String modelName, String modelFunction, MultipartFile modelFile) {
        try {
            String path = resourcePath + MODEL_SAVE_PATH + "/"+mainCategory+"/"+modelName+"/"+modelFunction+"/"+
                    modelFile.getOriginalFilename();
            deleteEntirePathIfExist(Paths.get(path));
            modelFile.transferTo(new File(path));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public ResponseEntity<?> updateModelDoc(String mainCategory, String modelName, String modelFunction,
                                             MultipartFile modelDoc){
        try {
            saveModelDoc(mainCategory, modelName, modelFunction, modelDoc);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/text"))
                    .body("updateSuccess");
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/text"))
                    .body("updateFailed");
        }
    }

    private void saveModelDoc(String mainCategory, String modelName, String modelFunction, MultipartFile modelDoc){
        try {
            String path = resourcePath+MODEL_SAVE_PATH+"/"+mainCategory+"/"+modelName+"/"+modelFunction+"/"+
                    modelDoc.getOriginalFilename();
            deleteEntirePathIfExist(Paths.get(path));
            modelDoc.transferTo(new File(path));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    public ResponseEntity<?> updatePreprocess(String mainCategory, String modelName, String modelFunction,
                                              MultipartFile preprocess){
        try {
            saveAndUnZipPreprocess(mainCategory, modelName, modelFunction, preprocess);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/text"))
                    .body("updateSuccess");
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/text"))
                    .body("updateFailed");
        }
    }

    private void saveAndUnZipPreprocess(String mainCategory, String modelName, String modelFunction, MultipartFile preprocess){
        try {
            String folderPath = resourcePath+MODEL_SAVE_PATH+"/"+mainCategory+"/"+modelName+"/"+modelFunction+"/";
            String filePath = folderPath+preprocess.getOriginalFilename();
            deleteEntirePathIfExist(Paths.get(filePath));
            deleteEntirePathIfExist(Paths.get(folderPath+"/preprocess"));
            preprocess.transferTo(new File(filePath));
            UnZipFiles.uniZip(filePath, folderPath);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void deleteEntirePathIfExist(Path path) {
        try {
            if (Files.exists(path)) {
                Files.walk(path)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    private void createNewFolder(String mainCategory, String modelName, String modelFunction){
        String targetPath = resourcePath+MODEL_SAVE_PATH+mainCategory+"/"+modelName+"/"+modelFunction;
        recreateFolder(targetPath);
    }

    public ResponseEntity<Resource> readModelFile(String mainCategory, String modelName, String modelFunction){
        String pathStr = resourcePath+MODEL_SAVE_PATH+mainCategory+"/"+modelName+"/"+modelFunction+"/"+"model.zip";
        return getResourceResponseEntity(pathStr, "application/zip");
    }

    public ResponseEntity<Resource> readPreprocessModule(String mainCategory, String modelName, String modelFunction){
        String pathStr = resourcePath+MODEL_SAVE_PATH+mainCategory+"/"+modelName+"/"+modelFunction+"/"+"preprocess.zip";
        return getResourceResponseEntity(pathStr, "application/zip");
    }

    public ResponseEntity<Resource> readModelDoc(String mainCategory, String modelName, String modelFunction){
        String pathStr = resourcePath+MODEL_SAVE_PATH+mainCategory+"/"+modelName+"/"+modelFunction+"/"+"doc.md";
        return getResourceResponseEntity(pathStr, "text/plain");
    }

    private static ResponseEntity<Resource> getResourceResponseEntity(String pathStr, String contentType) {
        Path path = Paths.get(pathStr);
        System.out.println(path.toAbsolutePath());
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        assert resource != null;
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    public ResponseEntity<String> deleteModel(String mainCategory, String modelName, String modelFunction){
        String pathStr = resourcePath+MODEL_SAVE_PATH+"/"+mainCategory+"/"+modelName+"/"+modelFunction;
        Path path = Paths.get(pathStr);
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        try {
            MachineLearningModel machineLearningModel =
                    machineLearningModelRepository.findFirstByModelEnglishFunctionNameAndModelEnglishNameAndMainCategory(
                            modelFunction, modelName, mainCategory
                    );
            machineLearningModelRepository.delete(machineLearningModel);

            deleteModelDeploy(mainCategory, modelName, modelFunction);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/plain"))
                    .header(HttpHeaders.CONTENT_DISPOSITION)
                    .body(null);
        }

        catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/text"))
                    .body("deleteFailed");
        }

    }

    private void modelDeploy(String mainCategory, String modelName, String modelFunction){
        MachineLearningModel machineLearningModel =
                machineLearningModelRepository.findFirstByModelEnglishFunctionNameAndModelEnglishNameAndMainCategory(
                        modelFunction, modelName, mainCategory
                );
        String platform = machineLearningModel.getPlatform();

        if(platform.equals(ParameterName.TENSORFLOW)){
            // 将模型传到TF Server中存在两个工作
            // 1. 将模型文件具体转移到TF Server中（由于我们上传的是zip文件，此处还涉及解压）
            //    按照程序流设计，当使用ModelDeploy时，相应的模型应当已经下载存储完毕了，因此可以直接从Spring Boot里调用
            // 2. 将数据库中当前可用模型（Access Control不为关闭的）整理，构建TF server所需的models.config文件，覆盖TF server中的config

            // Step 1
            moveAndUnCompressTensorflowModelFile(mainCategory, modelName, modelFunction);
            // Step 2
            updateTensorflowModelConfigFile();
        }
        else if (platform.equals(ParameterName.PYTORCH)){
            moveAndUnCompressTensorflowModelFile(mainCategory, modelName, modelFunction);
        }
        else{
            //此处在未来也可以添加支持其它平台(如Pytorch)的接口
            System.out.println("To Be Done");
        }
    }

    private void deleteModelDeploy(String mainCategory, String modelName, String modelFunction){
        updateTensorflowModelConfigFile();

        String targetPath = resourcePath+TENSORFLOW_SERVER_PATH+mainCategory+"/"+modelName+"/"+modelFunction+"/";
        try {
            Files.walk(Paths.get(targetPath))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        catch (IOException e){e.printStackTrace();}
    }

    private void moveAndUnCompressTensorflowModelFile (String mainCategory, String modelName, String modelFunction){
        // 包含模型迁移及解压工作

        // 创建目标转移路径
        String targetPath = resourcePath+TENSORFLOW_SERVER_PATH+mainCategory+"/"+modelName+"/"+modelFunction+"/";
        recreateFolder(targetPath);

        // 将Spring Boot下存储好的模型转移到Tensorflow Server下
        String source = resourcePath+MODEL_SAVE_PATH+mainCategory+"/"+modelName+"/"+modelFunction+"/"+"model.zip";
        String filePath = targetPath+"model.zip";
        try {
            FileInputStream fis = new FileInputStream(new File(source));
            FileOutputStream fos = new FileOutputStream(new File(filePath), false);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        UnZipFiles.uniZip(filePath, targetPath);
    }

    private void recreateFolder(String pathStr){
        Path path = Paths.get(pathStr);
        try {
            if (Files.exists(path)) {
                deleteEntirePathIfExist(path);
            }
            Files.createDirectories(path);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    private void updateTensorflowModelConfigFile(){
        // config file的更新只依赖于当前数据库
        List<MachineLearningModel> machineLearningModelList =
                machineLearningModelRepository.findByPlatform(ParameterName.TENSORFLOW);

        TensorflowConfigGenerator tensorflowConfigGenerator = new TensorflowConfigGenerator();
        tensorflowConfigGenerator.addHead();
        for(MachineLearningModel machineLearningModel: machineLearningModelList){
            String modelEnglishName = machineLearningModel.getModelEnglishName();
            String modelEnglishFunction = machineLearningModel.getModelEnglishFunctionName();
            String mainCategory = machineLearningModel.getMainCategory();

            String modelPath = "/models/"+mainCategory+"/"+modelEnglishName+"/"+modelEnglishFunction+"/";
            tensorflowConfigGenerator.addItem(
                    mainCategory+"_"+modelEnglishName+"_"+modelEnglishFunction, modelPath);
        }
        tensorflowConfigGenerator.addTail();

        String path = resourcePath+TENSORFLOW_SERVER_PATH+"models.config";
        byte[] strToBytes = tensorflowConfigGenerator.output().getBytes();
        try {
            FileOutputStream fos = new FileOutputStream(new File(path), false);
            fos.write(strToBytes);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public List<MachineLearningModel> fetchModelList (){
        return machineLearningModelRepository.findAll();
    }
}

class TensorflowConfigGenerator{
    private StringBuilder stringBuilder = new StringBuilder();
    void addHead(){
        stringBuilder.append("model_config_list{\n");
    }
    void addItem(String name, String basePath){
        stringBuilder.append("config{\nname: ");
        stringBuilder.append("\"").append(name).append("\"\n");
        stringBuilder.append("base_path: ");
        stringBuilder.append("\"").append(basePath).append("\"\n");
        stringBuilder.append("model_platform: \"tensorflow\"");
        stringBuilder.append("}\n");
    }
    void addTail(){
        stringBuilder.append("}\n");
    }
    String output(){
        return stringBuilder.toString();
    }
}