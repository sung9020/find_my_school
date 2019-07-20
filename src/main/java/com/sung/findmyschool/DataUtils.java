package com.sung.findmyschool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/*
 *
 * @author 123msn
 * @since 2019-07-19
 */
public class DataUtils {
    private final static Logger logger = LoggerFactory.getLogger(DataUtils.class);

    public static List<String> getDatabyCsv(String fileName){
        List<String> Data = new ArrayList<>();
        try{
            Data = Files.lines(Paths.get(fileName)).collect(Collectors.toList());
        }catch (IOException e){
            e.printStackTrace();
            logger.error("# 파일 읽기 에러");
        }

        return Data;
    }

    public static void setDataByCsv(String fileName, Map<String,Integer> data){
        StringBuilder stringBuilder = new StringBuilder();

        for(String key : data.keySet()){
            stringBuilder.append(key)
                    .append("\t")
                    .append(data.get(key))
                    .append("\n");
        }

        try{
            Files.write(Paths.get(fileName), stringBuilder.toString().getBytes());
        }catch (IOException e){
            e.printStackTrace();
            logger.error("# 파일 쓰기 에러");
        }

    }
}
