package com.sung.findmyschool;

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*
 *
 * @author 123msn
 * @since 2019-07-19
 */
public class FindMySchool {

    private final static Logger logger = LoggerFactory.getLogger(FindMySchool.class);
    private final static Pattern patten = Pattern.compile("[^ ]+(초등학교|초|중학교|중|고등학교|고|대학교|대)");
    private final static String NOUNS = "NN";

    private final static String USER_DIC_PATH = "user_dic.txt";
    private final static String MY_SCHOOL_DATA_PATH = "mySchool.csv";
    private final static String COMMENT_DATA_PATH = "comments.csv";
    private final static String RESULT_DATA_PATH = "result.txt";

    // 형태소 분석 후 기본 데이터 가져오기
    private static List<String> getBasicDataByKomoran(List<String> basicData){
        List<String> fileData = new ArrayList<>();
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        komoran.setUserDic(USER_DIC_PATH); // 유저 학습 데이터
        basicData.forEach(str -> {
            if(str.length() > 0){
                KomoranResult analyzeResultList = komoran.analyze(str);
                List<Token> tokenList = analyzeResultList.getTokenList();
                for (Token token : tokenList) {
                    if(token.getPos().contains(NOUNS)){
                        fileData.add(token.getMorph());
                    }
                }
            }
        });
        return fileData;
    }

    // 패턴 & 공공 데이터(학교명) 활용 필터링
    private static List<String> getFilterData(List<String> basicData){
        List<String> filterData = new ArrayList<>();
        basicData.forEach(str -> {
            Matcher matcher = patten.matcher(str);
            while(matcher.find()){
                filterData.addAll(Arrays.asList(matcher.group().split(","))
                        .stream()
                        .filter(FindMySchool::isSchool)
                        .collect(Collectors.toList())
                );
            }
        });
        return filterData;
    }

    //학교 이름 판단 함수
    private static boolean isSchool(String name){
        List<String> mySchools = DataUtils.getDataByCsv(MY_SCHOOL_DATA_PATH);

        for(String school : mySchools){
            if(name.contains(school)){
                return true;
            }
        }
        return false;
    }

    // 학교별 출현 횟수 카운트
    private static Map<String, Integer> getMySchoolCount(List<String> basicData){
        Map<String, Integer> mySchoolCount = new TreeMap<>();

        basicData.forEach(str -> {
            if(mySchoolCount.containsKey(str)){
                mySchoolCount.put(str, mySchoolCount.get(str) + 1);
            }else{
                mySchoolCount.put(str, 1);
            }
        });

        return mySchoolCount;
    }

    public static void main(String[] args) {
        List<String> basicData = getBasicDataByKomoran(DataUtils.getDataByCsv(COMMENT_DATA_PATH));
        logger.info("##형태소 필터 완료");

        List<String> filterData = getFilterData(basicData);
        logger.info("##패턴 & 공공 데이터 필터 완료");

        Map<String, Integer> mySchoolCount = getMySchoolCount(filterData);
        logger.info("##학교별 개수 추출 완료");

        DataUtils.setDataByCsv(RESULT_DATA_PATH, mySchoolCount);
        logger.info("##최종 파일 생성 완료");

    }
}
