package edu.cmu.lti.f13.hw4.hw4_shenwu.annotators;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.StringList;
import org.apache.uima.jcas.tcas.Annotation;
import org.uimafit.util.FSCollectionFactory;

import edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Document;
import edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Token;
import edu.cmu.lti.f13.hw4.hw4_shenwu.utils.Utils;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;
import edu.stanford.nlp.process.Tokenizer;

public class DocumentVectorAnnotator extends JCasAnnotator_ImplBase {
  ArrayList<HashMap<String, Integer>> list = new ArrayList<HashMap<String, Integer>>();

  HashMap<String, Integer> hashMapDictionary = new HashMap<String, Integer>();// store all the
                                                                              // dictionary

  int currentID = 1; // initialize currentID

  int tokenIndex = 0;// initialize tokenIndex

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    Set<String> stopWords = new HashSet<String>();
    try {
      FileReader fr = new FileReader("src/main/resources/stopwords.txt");
      BufferedReader br = new BufferedReader(fr);
      String myreadline; // 定义一个String类型的变量,用来每次读取一行
      while (br.ready()) {
        myreadline = br.readLine();// 读取一行
        stopWords.add(myreadline);
        // System.out.println(myreadline);//在屏幕上输出
      }
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    FSIterator<Annotation> DocumentIter = jcas.getAnnotationIndex().iterator();

    if (DocumentIter.isValid()) {
      DocumentIter.moveToNext();

      jcas.getAnnotationIndex().iterator().get();
      Document doc = (Document) DocumentIter.get();
      System.out.println(doc.getText());

      createTermFreqVector(jcas, doc);

//      if (doc.getQueryID() != currentID) {
//        list.add(hashMapDictionary);
//        currentID++;
//        tokenIndex = 0;
//        hashMapDictionary = new HashMap<String, Integer>();
//
//      }

//      for (int i = 0; i < doc.getTokenArray().size(); i++) {// construct dictionary
//        if (!hashMapDictionary.containsKey(doc.getTokenArray(i).getText())) {
//          // && !stopWords.contains(doc.getTokenArray(i).getText())) {
//          hashMapDictionary.put(doc.getTokenArray(i).getText(), tokenIndex++);
//        }
//
//      }

    }// end of while

  }

  /**
   * 
   * @param jcas
   * @param doc
   */

  private void createTermFreqVector(JCas jcas, Document doc) {

    // String docText = doc.getText();
    String docText = doc.getText();
    TokenizerFactory<Word> factory = PTBTokenizerFactory.newTokenizerFactory();
    Tokenizer<Word> tokenizer = factory.getTokenizer(new StringReader(docText));
    // Set<String> set = new HashSet<String>();
    // HashMap<String, edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Token> hash = new HashMap<String,
    // edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Token>();
    edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Token tmpToken;
    FSArray tokenList;// = new FSArray(jcas, 1);
    // IntegerArray intArray = new IntegerArray();
    ArrayList<edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Token> arraylist = new ArrayList<edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Token>();
    while (tokenizer.hasNext()) {
      Word temp = tokenizer.next();
      edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Token annotation = new edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Token(
              jcas);
      annotation.setBegin(temp.beginPosition() + doc.getBegin());
      // System.out.println(annotation.getBegin());
      annotation.setEnd(temp.endPosition() + doc.getBegin());
      // System.out.println(annotation.getEnd());
      annotation.setText(temp.word().toLowerCase());
      annotation.addToIndexes();
      arraylist.add(annotation);

    }
    doc.setTokenList(Utils.fromCollectionToFSList(jcas, arraylist));
    
  }

}
