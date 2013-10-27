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

import edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Document;
import edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Token;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.process.PTBTokenizer.PTBTokenizerFactory;
import edu.stanford.nlp.process.Tokenizer;

public class CopyOfDocumentVectorAnnotator extends JCasAnnotator_ImplBase {

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
    FSIterator<Annotation> DocumentIter = jcas.getAnnotationIndex(Document.type).iterator();
    ArrayList<HashMap<String, Integer>> list = new ArrayList<HashMap<String, Integer>>();

    HashMap<String, Integer> hashMapDictionary = new HashMap<String, Integer>();// store all the
                                                                                // dictionary
    int currentID = 1; // initialize currentID
    int tokenIndex = 0;// initialize tokenIndex
    while (DocumentIter.hasNext()) {
      // iter.moveToNext();
      Document doc = (Document) DocumentIter.next();

      createTermFreqVector(jcas, doc, stopWords);

      if (doc.getQueryID() != currentID) {
        list.add(hashMapDictionary);
        currentID++;
        tokenIndex = 0;
        hashMapDictionary = new HashMap<String, Integer>();

      }

      for (int i = 0; i < doc.getTokenArray().size(); i++) {// construct dictionary
        if (!hashMapDictionary.containsKey(doc.getTokenArray(i).getText())) {
          // && !stopWords.contains(doc.getTokenArray(i).getText())) {
          hashMapDictionary.put(doc.getTokenArray(i).getText(), tokenIndex++);
        }

      }

    }// end of while
    list.add(hashMapDictionary);

    IntegerArray intArray = new IntegerArray(jcas, list.get(0).size());
    for (int i = 0; i < list.get(0).size(); i++) {
      intArray.set(i, 0);
    }
    currentID = 1;
    DocumentIter = jcas.getAnnotationIndex(Document.type).iterator();
    HashMap currentHashMap = list.get(0);

    while (DocumentIter.hasNext()) { // for each document, compute the vector
      Document doc = (Document) DocumentIter.next();
      if (doc.getQueryID() != currentID) {
        currentID++;
        currentHashMap = list.get(currentID - 1);

        intArray = new IntegerArray(jcas, list.get(currentID - 1).size());



      } else {
        intArray = new IntegerArray(jcas, list.get(currentID - 1).size());


      }
      for (int j = 0; j < doc.getTokenArray().size(); j++) {
        int currentIndex = (Integer) currentHashMap.get(doc.getTokenArray(j).getText());
        intArray.set(currentIndex, doc.getTokenArray(j).getFrequency());
      }
      //
      // intArray.set( , v);
      doc.setVector(intArray);

    }// end of while

  }

  /**
   * 
   * @param jcas
   * @param doc
   */

  private void createTermFreqVector(JCas jcas, Document doc, Set<String> stopWords) {

    // String docText = doc.getText();
    String docText = doc.getText();
    TokenizerFactory<Word> factory = PTBTokenizerFactory.newTokenizerFactory();
    Tokenizer<Word> tokenizer = factory.getTokenizer(new StringReader(docText));
    // Set<String> set = new HashSet<String>();
    HashMap<String, edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Token> hash = new HashMap<String, edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Token>();
    edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Token tmpToken;
    FSArray tokenList;// = new FSArray(jcas, 1);
    // IntegerArray intArray = new IntegerArray();
    while (tokenizer.hasNext()) {
      Word temp = tokenizer.next();
      // if(stopWords.contains(temp.word()))
      if (!stopWords.contains(temp.word())) {
        // hash.
        // System.out.println(temp.word() + " " + temp.word().hashCode());
        // if(stopWords)
        if (hash.containsKey(temp.word())) {
          // System.out.println("I am here!");
          tmpToken = hash.get(temp.word());
          tmpToken.setFrequency(tmpToken.getFrequency());
          // doc.set
        } else {
          // tokenListk = new FSArray(jcas,hash.size()+1);
          // tokenListk.
          edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Token annotation = new edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Token(
                  jcas);
          annotation.setBegin(temp.beginPosition() + doc.getBegin());
          // System.out.println(annotation.getBegin());
          annotation.setEnd(temp.endPosition() + doc.getBegin());
          // System.out.println(annotation.getEnd());
          annotation.setText(temp.word().toLowerCase());
          annotation.setFrequency(1);
          annotation.addToIndexes();
          hash.put(temp.word(), annotation);
          // hash.

        }

      }
      // TO DO: construct a vector of tokens and update the tokenList in CAS
      Iterator<Token> x = hash.values().iterator();
      tokenList = new FSArray(jcas, hash.size());

      int i = 0;
      while (x.hasNext()) {
        tokenList.set(i++, x.next());
        // x.next().getText()
      }
      doc.setTokenArray(tokenList);
      // Token a = doc.getTokenArray(1);

      // doc.setTokenList(tokenList);
      // StringList a = new StringList(jcas);
      // a.
    }
    // TO DO: construct a vector of tokens and update the tokenList in CAS

  }
}