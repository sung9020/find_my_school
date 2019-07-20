/*
 *
 * @author 123msn
 * @since 2019-07-19
 */

import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertThat;

public class KomoranFindMySchoolTests {
    private final Logger logger = LoggerFactory.getLogger(KomoranFindMySchoolTests.class);

    private List<String> mySchools;
    private List<String> dic;
    private Pattern patten = Pattern.compile("[^ ]+(초등학교|초|중학교|중|고등학교|고|대학교|대)");


    @Before
    public void init(){
        dic = getBasicDataByKomoran();
        mySchools = getMySchoolData();
    }

    @Test
    public void 데이터_필터링하기_by_패턴_전국학교데이터() {
        List<String> filterData = new ArrayList<>();
        dic.forEach(str -> {
            Matcher matcher = patten.matcher(str);
                while(matcher.find()){
                    filterData.addAll(Arrays.asList(matcher.group().split(","))
                            .stream()
                            .filter(this::isSchool)
                            .collect(Collectors.toList())
                    );
            }

        });

        logger.info(filterData.toString());
        assertThat(filterData, Matchers.notNullValue());

    }
    @Test
    public void 단어_빈도_구하기(){
        Map<String, Integer> dicCounts = new TreeMap<>();

        List<String> filterData = new ArrayList<>();
        dic.forEach(str -> {
            Matcher matcher = patten.matcher(str);
            while(matcher.find()){
                filterData.addAll(Arrays.asList(matcher.group().split(","))
                        .stream()
                        .filter(this::isSchool)
                        .collect(Collectors.toList())
                );
            }
        });

        filterData.forEach(str -> {
            if(dicCounts.containsKey(str)){
                dicCounts.put(str, dicCounts.get(str) + 1);
            }else{
                dicCounts.put(str, 1);
            }
        });
        logger.info(dicCounts.toString());
        assertThat(dicCounts.size(), Matchers.notNullValue());
        assertThat(dicCounts.get("개봉중학교"), Matchers.greaterThan(0));
    }

    @Test
    public void 최종_데이터_파일써보기(){
        Map<String, Integer> dicCounts = new TreeMap<>();

        List<String> filterData = new ArrayList<>();
        dic.forEach(str -> {
            Matcher matcher = patten.matcher(str);
            while(matcher.find()){
                filterData.addAll(Arrays.asList(matcher.group().split(","))
                        .stream()
                        .filter(this::isSchool)
                        .collect(Collectors.toList())
                );
            }
        });

        filterData.forEach(str -> {
            if(dicCounts.containsKey(str)){
                dicCounts.put(str, dicCounts.get(str) + 1);
            }else{
                dicCounts.put(str, 1);
            }
        });

        StringBuilder stringBuilder = new StringBuilder();

        for(String key : dicCounts.keySet()){
            stringBuilder.append(key)
                    .append("\t")
                    .append(dicCounts.get(key))
                    .append("\n");
        }

        try{
            Files.write(Paths.get("result.txt"), stringBuilder.toString().getBytes());
            assertThat(stringBuilder.length(), Matchers.notNullValue());
            assertThat(Files.size(Paths.get("result.txt")), Matchers.greaterThan(0L));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private boolean isSchool(String name){

        for(String school : mySchools){
            if(name.contains(school)){
                return true;
            }
        }

        return false;
    }

    private List<String> getBasicDataByKomoran(){
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        komoran.setUserDic("user_dic.txt");
        List<String> basicData = new ArrayList<>();
        try{
            Stream<String> lines = Files.lines(Paths.get("comments.csv"));
            lines.forEach(str -> {
                if(str.length() > 0){
                    KomoranResult analyzeResultList = komoran.analyze(str);
                    List<Token> tokenList = analyzeResultList.getTokenList();
                    for (Token token : tokenList) {
                        if(token.getPos().contains("NN")){
                            basicData.add(token.getMorph());
                        }
                    }
                }
            });
        }catch(IOException e){
            e.printStackTrace();
        }

        return basicData;
    }

    private List<String> getMySchoolData(){
        List<String> mySchools = new ArrayList<>();
        try{
            mySchools = Files.lines(Paths.get("mySchool.csv")).collect(Collectors.toList());
        }catch (IOException e){
            e.printStackTrace();
        }

        return mySchools;
    }
}
