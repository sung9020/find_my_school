import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.openkoreantext.processor.KoreanPosJava;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.Seq;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertThat;

/*
 *
 * @author 123msn
 * @since 2019-07-20
 */
public class OtkFindMySchoolTests {

    //FIXME 학습 시켜야할 고유명사 데이터가 너무 많음(하나하나 구분이 불가)

    private final Logger logger = LoggerFactory.getLogger(OtkFindMySchoolTests.class);
    private List<String> mySchools;
    private List<String> dic;
    private Pattern patten = Pattern.compile("[^ ]+(초등학교|초|중학교|중|고등학교|고|대학교|대)");

    @Before
    public void init(){
        dic = getBasicDataByOtk();
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
        assertThat(dicCounts.get("연희미용고"), Matchers.greaterThan(0));
        assertThat(dicCounts.get("인제대학교"), Matchers.greaterThan(0));
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
            logger.error("# 파일 쓰기 에러");
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

    private List<String> getBasicDataByOtk(){
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        komoran.setUserDic("user_dic.txt");
        List<String> basicData = new ArrayList<>();
        try{
            Stream<String> lines = Files.lines(Paths.get("comments.csv"));
            lines.forEach(str -> {
                if(str.length() > 0){
                    CharSequence normalize = OpenKoreanTextProcessorJava.normalize(str);
                    Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalize);
                    List<KoreanTokenJava> koreanTokenJavaList = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);
                    for(KoreanTokenJava koreanTokenJava : koreanTokenJavaList){
                        if(koreanTokenJava.getPos().equals(KoreanPosJava.Noun) || koreanTokenJava.getPos().equals(KoreanPosJava.ProperNoun)){
                            basicData.add(koreanTokenJava.getText());
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
            Files.lines(Paths.get("mySchool.csv")).forEach(str ->{
                mySchools.add(str);
            });
        }catch (IOException e){
            e.printStackTrace();
        }

        return mySchools;
    }

}
