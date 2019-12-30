package cn.edu.zju.bmi.service;

import cn.edu.zju.bmi.entity.DAO.MachineLearningModel;
import cn.edu.zju.bmi.repository.MachineLearningModelRepository;
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
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Date;

@Service
public class AlgorithmManagementService {
    @Value(value="${app.machineLearningModelRoot}")
    private String root;
    private MachineLearningModelRepository machineLearningModelRepository;

    @Autowired
    public AlgorithmManagementService(MachineLearningModelRepository machineLearningModelRepository){
        this.machineLearningModelRepository = machineLearningModelRepository;
    }

    public ResponseEntity<?> createNewModel(String modelChineseName,
                                            String modelEnglishName,
                                            String modelCategory,
                                            String modelFunctionChinese,
                                            String modelFunctionEnglish,
                                            String platform,
                                            String user,
                                            String accessControl,
                                            MultipartFile modelFile,
                                            MultipartFile modelDoc,
                                            MultipartFile modelPreprocess,
                                            MultipartFile modelConfig){
        // 创建模型存在三个步骤，第一，将模型存储在Spring Boot中；第二，在数据库中添加相关记录；第三，将模型信息传输到TF Server中

        createNewFolder(modelCategory, modelEnglishName, modelFunctionEnglish);
        saveModelFile(modelCategory, modelEnglishName, modelFunctionEnglish, modelFile);
        saveModelDoc(modelCategory, modelEnglishName, modelFunctionEnglish, modelDoc);
        saveConfig(modelCategory, modelEnglishName, modelFunctionEnglish, modelConfig);
        saveModelDoc(modelCategory, modelEnglishName, modelFunctionEnglish, modelPreprocess);

        Date date = new Date(System.currentTimeMillis());

        MachineLearningModel entity = new MachineLearningModel(user, modelCategory,
                modelChineseName, modelEnglishName, modelFunctionChinese,
                modelFunctionEnglish, platform, accessControl, date, date);
        machineLearningModelRepository.save(entity);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/text"))
                .body("createSuccess");
    }

    public ResponseEntity<?> updateModelFile(String modelCategory, String modelName, String modelFunction,
                                             MultipartFile modelFile){
        saveModelFile(modelCategory, modelName, modelFunction, modelFile);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/text"))
                .body("updateModelFileSuccess");
    }

    private void saveModelFile(String modelCategory, String modelName, String modelFunction,
                               MultipartFile modelFile) {
        try {
            String path = root+"/"+modelCategory+"/"+modelName+"/"+modelFunction+"/"+modelFile.getOriginalFilename();
            modelFile.transferTo(new File(path));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public ResponseEntity<?> updateModelConfig(String modelCategory, String modelName, String modelFunction,
                                             MultipartFile modelConfig){
        saveConfig(modelCategory, modelName, modelFunction, modelConfig);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/text"))
                .body("updateConfigSuccess");
    }

    private void saveConfig(String modelCategory, String modelName, String modelFunction, MultipartFile modelConfig){
        try {
            String path = root+"/"+modelCategory+"/"+modelName+"/"+modelFunction+"/"+modelConfig.getOriginalFilename();
            modelConfig.transferTo(new File(path));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    public ResponseEntity<?> updateModelDoc(String modelCategory, String modelName, String modelFunction,
                                             MultipartFile modelDoc){
        saveModelDoc(modelCategory, modelName, modelFunction, modelDoc);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/text"))
                .body("updateDocSuccess");
    }

    private void saveModelDoc(String modelCategory, String modelName, String modelFunction, MultipartFile modelDoc){
        try {
            String path = root+"/"+modelCategory+"/"+modelName+"/"+modelFunction+"/"+modelDoc.getOriginalFilename();
            modelDoc.transferTo(new File(path));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    public ResponseEntity<?> updatePreprocess(String mainCategory, String modelName, String modelFunction,
                                              MultipartFile preprocess){
        savePreprocess(mainCategory, modelName, modelFunction, preprocess);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/text"))
                .body("updatePreprocessSuccess");
    }

    private void savePreprocess(String modelCategory, String modelName, String modelFunction, MultipartFile preprocess){
        try {
            String path = root+"/"+modelCategory+"/"+modelName+"/"+modelFunction+"/"+preprocess.getOriginalFilename();
            preprocess.transferTo(new File(path));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    private void createNewFolder(String mainCategory, String modelName, String modelFunction){
        Path folder = Paths.get(root+"/"+mainCategory+"/"+modelName+"/"+modelFunction);
        if (!Files.exists(folder)) {
            try {
                Files.createDirectories(folder);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ResponseEntity<Resource> readModelFile(String mainCategory, String modelName, String modelFunction){
        String pathStr = root+"/"+mainCategory+"/"+modelName+"/"+modelFunction+"/"+"model.zip";
        return getResourceResponseEntity(pathStr, "application/zip");
    }

    public ResponseEntity<Resource> readPreprocessModule(String mainCategory, String modelName, String modelFunction){
        String pathStr = root+"/"+mainCategory+"/"+modelName+"/"+modelFunction+"/"+"preprocess.zip";
        return getResourceResponseEntity(pathStr, "application/zip");
    }

    public ResponseEntity<Resource> readModelDoc(String mainCategory, String modelName, String modelFunction){
        String pathStr = root+"/"+mainCategory+"/"+modelName+"/"+modelFunction+"/"+"modelDoc.md";
        return getResourceResponseEntity(pathStr, "text/plain");
    }

    public ResponseEntity<Resource> readModelConfig(String mainCategory, String modelName, String modelFunction){
        String pathStr = root+"/"+mainCategory+"/"+modelName+"/"+modelFunction+"/"+"config.yml";
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

    public ResponseEntity<Resource> deleteModel(String mainCategory, String modelName, String modelFunction){
        String pathStr = root+"/"+mainCategory+"/"+modelName+"/"+modelFunction;
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
        MachineLearningModel machineLearningModel =
                machineLearningModelRepository.findFirstByModelEnglishFunctionNameAndModelEnglishNameAndMainCategory(
                        modelFunction, modelName, mainCategory
                );
        machineLearningModelRepository.delete(machineLearningModel);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/plain"))
                .header(HttpHeaders.CONTENT_DISPOSITION)
                .body(null);
    }
}
