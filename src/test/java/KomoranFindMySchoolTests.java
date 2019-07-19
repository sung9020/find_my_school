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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.junit.Assert.assertThat;
import org.hamcrest.Matchers;
import org.openkoreantext.processor.KoreanPosJava;
import org.openkoreantext.processor.KoreanTokenJava;
import org.openkoreantext.processor.OpenKoreanTextProcessorJava;
import org.openkoreantext.processor.phrase_extractor.KoreanPhraseExtractor;
import org.openkoreantext.processor.tokenizer.KoreanTokenizer;
import org.openkoreantext.processor.util.KoreanPos;
import scala.collection.Seq;

public class KomoranFindMySchoolTests {

    private List<String> full;
    private List<String> dic;
    private Pattern patten = Pattern.compile("[^@(?!)❗❣#★❤\"\\[.,/ ㅠㅜ~>♡']+?(초등학교|초|중학교|중|고등학교|고|대학교|대)");

    @Before
    public void init(){
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        dic = new ArrayList<>();
        full = new ArrayList<>();
        try{
            Stream<String> lines = Files.lines(Paths.get("comments.csv"));
            lines.forEach(str -> {
                full.add(str);
                if(str.length() > 0){
                    KomoranResult analyzeResultList = komoran.analyze(str);
                    List<Token> tokenList = analyzeResultList.getTokenList();
                    for (Token token : tokenList) {
                        if(token.getPos().contains("NN")){
                            dic.add(token.getMorph());
                        }
                    }
                }

            });
        }catch(IOException e){
            e.printStackTrace();
        }
    }

//    @Before
//    public void init(){
//        dic = new ArrayList<>();
//        full = new ArrayList<>();
//        try{
//            Stream<String> lines = Files.lines(Paths.get("comments.csv"));
//
//            lines.forEach(str -> {
//                full.add(str);
//                CharSequence normalize = OpenKoreanTextProcessorJava.normalize(str);
//                Seq<KoreanTokenizer.KoreanToken> tokens = OpenKoreanTextProcessorJava.tokenize(normalize);
//                List<KoreanTokenJava> koreanTokenJavaList = OpenKoreanTextProcessorJava.tokensToJavaKoreanTokenList(tokens);
//                for(KoreanTokenJava koreanTokenJava : koreanTokenJavaList){
//                    if(!koreanTokenJava.getPos().equals(KoreanPosJava.Noun)){
//                        dic.add(koreanTokenJava.getText());
//                    }
//                }
//            });
//        }catch(IOException e){
//            e.printStackTrace();
//        }
//    }

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
