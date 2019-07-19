import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.openkoreantext.processor.KoreanPosJava;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import scala.collection.Seq;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.Assert.assertThat;

/*
 *
 * @author 123msn
 * @since 2019-07-20
 */
public class OtkFindMySchoolTests {

    private List<String> full;
    private List<String> dic;
    private Pattern patten = Pattern.compile("[^@(?!)❗❣#★❤\"\\[.,/ ㅠㅜ~>♡']+?(초등학교|초|중학교|중|고등학교|고|대학교|대)");

    @Before
    public void init(){
        dic = new ArrayList<>();
        full = new ArrayList<>();
        try{
            Stream<String> lines = Files.lines(Paths.get("comments.csv"));

            lines.forEach(str -> {
                full.add(str);
                CharSequence normalize = OpenKoreanTextProcessorJava.normalize(str);
                Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalize);
                List<KoreanTokenJava> koreanTokenJavaList = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);
                for(KoreanTokenJava koreanTokenJava : koreanTokenJavaList){
                    if(!koreanTokenJava.getPos().equals(KoreanPosJava.Noun)){
                        dic.add(koreanTokenJava.getText());
                    }
                }
            });
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Test
    public void fileParser() {

        List<String> words = new ArrayList<>();

        dic.forEach(str -> {
            Matcher matcher = patten.matcher(str);
            while(matcher.find()){
                String[] temp = matcher.group().split(",");
                words.addAll(Arrays.asList(temp));
            }

        });

        System.out.println(words.toString());
        assertThat(words, Matchers.notNullValue());

    }

    public void getDicCount(){

    }

}
