package edu.cmu.lti.f13.hw4.hw4_shenwu.casconsumers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.collection.CasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.cas.IntegerArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.ResourceProcessException;
import org.apache.uima.util.ProcessTrace;

import edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Document;
import edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Token;
import edu.cmu.lti.f13.hw4.hw4_shenwu.utils.Utils;

public class RetrievalEvaluator extends CasConsumer_ImplBase {

  /** query id number **/
  public ArrayList<Integer> qIdList;

  int currentQuerID = 0;

  /** query and text relevant values **/
  public ArrayList<Integer> relList;

  public ArrayList<Double> simiList;

  public ArrayList<ArrayList<Double>> similarity;

  public ArrayList<Double> JaccardsimiList;

  public ArrayList<ArrayList<Double>> Jaccardsimilarity;

  public ArrayList<Integer> StandardVector;

  public ArrayList<HashMap<String, Integer>> list;

  public ArrayList<ArrayList<ArrayList<Integer>>> vector;

  public HashMap<String, Integer> hashMapDictionary;

  public Set<String> stopWords;

  public void initialize() throws ResourceInitializationException {

    qIdList = new ArrayList<Integer>();

    relList = new ArrayList<Integer>();

    simiList = new ArrayList<Double>();
    JaccardsimiList = new ArrayList<Double>();
    StandardVector = new ArrayList<Integer>();
    ArrayList<HashMap<String, Integer>> list = new ArrayList<HashMap<String, Integer>>();
    hashMapDictionary = new HashMap<String, Integer>();
    vector = new ArrayList<ArrayList<ArrayList<Integer>>>();
    similarity = new ArrayList<ArrayList<Double>>();
    Jaccardsimilarity = new ArrayList<ArrayList<Double>>();
    stopWords = new HashSet<String>();
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
  }

  /**
   * TODO :: 1. construct the global word dictionary 2. keep the word frequency for each sentence
   */
  @Override
  public void processCas(CAS aCas) throws ResourceProcessException {

    JCas jcas;
    try {
      jcas = aCas.getJCas();
    } catch (CASException e) {
      throw new ResourceProcessException(e);
    }

    FSIterator it = jcas.getAnnotationIndex(Document.type).iterator();

    double tmp = 0;
    double tmp2 = 0;
    // ArrayList<Double> currentSimilarity = new ArrayList<Double>();
    // LinkedList<Double> a = new LinkedList<Double> ();
    // while (it.hasNext()) {
    if (it.hasNext()) {
      Document doc = (Document) it.next();
      // Document doc2 = (Document) it.next();
      System.out.println("currentID: " + currentQuerID);
      if (doc.getRelevanceValue() == 99)// this is the standard sentence
      {
        // StandardVector = doc.getVector();
        StandardVector = new ArrayList<Integer>();
        ArrayList<Token> tmpList = new ArrayList<Token>();
        tmpList = Utils.fromFSListToCollection(doc.getTokenList(), Token.class);
        hashMapDictionary.clear();
        for (int i = 0; i < tmpList.size(); i++) {
          if (!hashMapDictionary.containsKey(tmpList.get(i).getText())) {
            hashMapDictionary.put(tmpList.get(i).getText(), hashMapDictionary.size());
            StandardVector.add(1);

          } else {
            int ttmp = StandardVector.get(hashMapDictionary.get(tmpList.get(i).getText()));
            StandardVector.set(hashMapDictionary.get(tmpList.get(i).getText()), ttmp + 1);
          }
        }
        currentQuerID = doc.getQueryID();

        similarity.add(new ArrayList<Double>());
        Jaccardsimilarity.add(new ArrayList<Double>());

        System.out.println("Standard text is:  " + doc.getText());
        tmp = 0;

      } else if (doc.getQueryID() == currentQuerID)//
      {
        // System.out.println(similarity.toString());
        // ArrayList<Integer> currentVector = new ArrayList<Integer> ();
        ArrayList<Token> tmpList = new ArrayList<Token>();
        int currentVector[] = new int[StandardVector.size()];
        for (int i = 0; i > StandardVector.size(); i++) {
          currentVector[i] = 0;
        }
        tmpList = Utils.fromFSListToCollection(doc.getTokenList(), Token.class);

        for (int i = 0; i < tmpList.size(); i++) {
          if (hashMapDictionary.containsKey(tmpList.get(i).getText())) {
            currentVector[hashMapDictionary.get(tmpList.get(i).getText())]++;
          }

        }

        tmp = computeCosineSimilarity(currentVector, StandardVector, tmpList.size());
        // System.out.println("tmp is :" + tmp);
        // System.out.println(currentVector.toString());
        // System.out.println(StandardVector);

        tmp2 = computeJaccardIndex(currentVector, StandardVector, tmpList.size());
        similarity.get(doc.getQueryID() - 1).add(tmp);

        // Jaccardsimilarity.get(currentQuerID - 1).add(tmp2);

        // similarity.get(currentQuerID-1).add(computeCosineSimilarity(doc.getVector(),StandardVector));
        System.out.println("current text is:  " + doc.getText());
        System.out.println("rel is: " + doc.getRelevanceValue());
        System.out.println("cosine similarity is :  " + tmp);
        System.out.println("Jaccard similarity is :  " + tmp2);

      }
      // System.out.println(doc.getText());
      // Make sure that your previous annotators have populated this in CAS
      FSList fsTokenList = doc.getTokenList();
      // ArrayList<Token>tokenList=Utils.fromFSListToCollection(fsTokenList, Token.class);

      qIdList.add(doc.getQueryID());
      relList.add(doc.getRelevanceValue());
      simiList.add(tmp);
      JaccardsimiList.add(tmp2);
      // Do something useful here
    }

    // double metric_mrr = compute_mrr(similarity);
    // System.out.println(" (MRR) Mean Reciprocal Rank ::" + metric_mrr);
  }// end of process

  /**
   * TODO 1. Compute Cosine Similarity and rank the retrieved sentences 2. Compute the MRR metric
   */
  @Override
  public void collectionProcessComplete(ProcessTrace arg0) throws ResourceProcessException,
          IOException {

    super.collectionProcessComplete(arg0);

    // TODO :: compute the cosine similarity measure

    // TODO :: compute the rank of retrieved sentences

    // TODO :: compute the metric:: mean reciprocal rank
    // System.out.println(similarity.toString());

    double metric_mrr = compute_mrr(similarity, 1);
    double jmetric_mrr = compute_mrr(Jaccardsimilarity, 2);
    System.out.println(" (MRR) Mean Reciprocal Rank ::" + metric_mrr);
    System.out.println(" (MRR) Mean Reciprocal Rank ::" + jmetric_mrr);
    // /System.out.println(" (MRR) Mean Reciprocal Rank ::");
  }

  /**
   * 
   * @return cosine_similarity
   */
  private double computeCosineSimilarity(Map<String, Integer> queryVector,
          Map<String, Integer> docVector) {
    double cosine_similarity = 0.0;
    Iterator<String> queryIterator = queryVector.keySet().iterator();
    double total = 0;
    double dividorA = 0;
    double dividorB = 0;
    double dividor = 0;
    while (queryIterator.hasNext()) {
      String strTmp = queryIterator.next();
      if (docVector.containsKey(strTmp)) {
        total += queryVector.get(strTmp) * docVector.get(strTmp);
      }

      dividorA += queryVector.get(strTmp) * queryVector.get(strTmp);
    }
    // TODO :: compute cosine similarity between two sentences
    dividor = dividorA * Math.sqrt(dividorB);
    total = total / dividor;

    return total;
  }

  private double computeCosineSimilarity(int[] a, ArrayList<Integer> b, int tokenNum) {
    double cosine_similarity = 0.0;

    double total = 0;
    double dividorA = 0;
    double dividorB = 0;
    double dividor = 0;

    for (int i = 0; i < a.length; i++) {
      total += a[i] * b.get(i);
      dividorB += b.get(i) * b.get(i);
    }
    dividor = tokenNum * Math.sqrt(dividorB);
    total = total / dividor;
    return total;
  }

  private double computeCosineSimilarity(IntegerArray a, IntegerArray b) {
    double total = 0;
    double dividorA = 0;
    double dividorB = 0;
    double dividor = 0;

    for (int i = 0; i < a.size(); i++) {
      total += a.get(i) * b.get(i);
      dividorA += a.get(i) * a.get(i);
      dividorB += b.get(i) * b.get(i);
    }
    dividor = Math.sqrt(dividorA) * Math.sqrt(dividorB);
    total = total / dividor;
    return total;

  }

  private double computeCosineSimilarity(IntegerArray a, ArrayList<Integer> b) {
    double total = 0;
    double dividorA = 0;
    double dividorB = 0;
    double dividor = 0;

    for (int i = 0; i < a.size(); i++) {
      total += a.get(i) * b.get(i);
      dividorA += a.get(i) * a.get(i);
      dividorB += b.get(i) * b.get(i);
    }
    dividor = Math.sqrt(dividorA) * Math.sqrt(dividorB);
    total = total / dividor;
    return total;

  }

  private double computeJaccardIndex(int[] a, ArrayList<Integer> b, int tokenNum) {
    double JaccardIndex = 0.0;
    double Union = 0;
    double intersection = 0;
    for (int i = 0; i < a.length; i++) {
      if (b.get(i) > 0) {
        Union++;
      }
      if (b.get(i) > 0) {
        intersection++;
      }
    }
    Union += tokenNum - intersection;
    return intersection / Union;
  }

  private double computeJaccardIndex(IntegerArray a, IntegerArray b) {
    double JaccardIndex = 0.0;
    double Union = 0;
    double intersection = 0;
    for (int i = 0; i < a.size(); i++) {
      if (a.get(i) > 0 || b.get(i) > 0) {
        Union++;
      }
      if (a.get(i) > 0 && b.get(i) > 0) {
        intersection++;
      }
    }
    return intersection / Union;
  }
  private double computeJaccardIndex(IntegerArray a, ArrayList<Integer> b) {
    double JaccardIndex = 0.0;
    double Union = 0;
    double intersection = 0;
    for (int i = 0; i < a.size(); i++) {
      if (a.get(i) > 0 || b.get(i) > 0) {
        Union++;
      }
      if (a.get(i) > 0 && b.get(i) > 0) {
        intersection++;
      }
    }
    return intersection / Union;
  }

  /**
   * 
   * @return mrr
   */
  private double compute_mrr(ArrayList<ArrayList<Double>> similarity, int type) {
    double metric_mrr = 0.0;
    // System.out.println(qIdList.size());
    for (int i = 0; i < qIdList.size(); i++) {
      if (relList.get(i) == 1)
        metric_mrr += 1.0 / rank(similarity.get(qIdList.get(i) - 1), i, type);
    }
    // qIdList;

    /** query and text relevant values **/
    // public ArrayList<Integer> relList;
    // TODO :: compute Mean Reciprocal Rank (MRR) of the text collection
    System.out.println(metric_mrr);

    return metric_mrr / qIdList.get(qIdList.size() - 1);
  }

  private int rank(ArrayList<Double> similarity, int right, int type) {
    int rank = 1;
    ArrayList<Double> list = null;
    if (type == 1)
      list = simiList;
    else if (type == 2)
      list = JaccardsimiList;

    for (int i = 0; i < similarity.size(); i++) {
      if (similarity.get(i) > list.get(right))
        rank++;
    }
    System.out.println("rank = " + rank);
    return rank;
  }

}
