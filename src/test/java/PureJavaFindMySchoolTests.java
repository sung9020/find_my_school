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

/*
 *
 * @author 123msn
 * @since 2019-07-20
 */
public class PureJavaFindMySchoolTests {

    // FIXME 단순 패턴 형태는 형태소를 거르지 못함, 오히려 미학습 OKT보다 적중이 높은듯..

    private final Logger logger = LoggerFactory.getLogger(PureJavaFindMySchoolTests.class);
    private List<String> mySchools;
    private List<String> dic;
    private Pattern patten = Pattern.compile("[^-<@(?!)❗❣#★❤\"\\[.,/ ㅠㅜㅎ~>♡']+(초등학교|초|중학교|중|고등학교|고|대학교|대)");

    @Before
    public void init(){
        dic = getBasicData();
        mySchools = getMySchoolData();
    }

    @Test
    public void 데이터_필터링하기_by_패턴_and_전국학교데이터() {
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

    private List<String> getBasicData(){
        List<String> basicData = new ArrayList<>();
        try{
            Stream<String> lines = Files.lines(Paths.get("comments.csv"));
            lines.forEach(str -> {
                if(str.length() > 0) {
                    basicData.add(str);
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
