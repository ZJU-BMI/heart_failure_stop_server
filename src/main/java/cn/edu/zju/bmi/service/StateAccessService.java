package cn.edu.zju.bmi.service;

import cn.edu.zju.bmi.support.StringResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

@Service
public class StateAccessService {
    @Value(value="${app.stateSavePath}")
    private String STATE_SAVE_PATH;

    public StringResponse updateState(String userID, String stateContent){
        try {
            String path = STATE_SAVE_PATH+userID+".txt";
            if (Files.exists(Paths.get(path)))
                Files.delete(Paths.get(path));
            PrintWriter out = new PrintWriter(path);
            out.println(stateContent);
            out.close();
        }
        catch (IOException e){e.printStackTrace();}
        return new StringResponse("success");
    }

    public StringResponse downloadState(String userID){
        String path = STATE_SAVE_PATH+userID+".txt";
        if (Files.exists(Paths.get(path))) {
            StringBuilder contentBuilder = new StringBuilder();

            try (Stream<String> stream = Files.lines(
                    Paths.get(STATE_SAVE_PATH + userID + ".txt"))) {
                stream.forEach(s -> contentBuilder.append(s));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return new StringResponse(contentBuilder.toString());
        }
        else{
            return new StringResponse("CacheStateNotFound");
        }
    }
}
