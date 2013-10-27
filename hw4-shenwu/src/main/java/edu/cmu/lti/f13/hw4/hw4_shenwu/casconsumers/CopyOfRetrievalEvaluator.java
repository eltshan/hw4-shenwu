package edu.cmu.lti.f13.hw4.hw4_shenwu.casconsumers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

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

public class CopyOfRetrievalEvaluator extends CasConsumer_ImplBase {

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


  public void initialize() throws ResourceInitializationException {

    qIdList = new ArrayList<Integer>();

    relList = new ArrayList<Integer>();
    
    simiList = new ArrayList<Double>();
    JaccardsimiList = new ArrayList<Double>();
    StandardVector = new ArrayList<Integer>();
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

    similarity = new ArrayList<ArrayList<Double>>();
    Jaccardsimilarity = new ArrayList<ArrayList<Double>>();
    double tmp = 0;
    double tmp2 = 0;
    // ArrayList<Double> currentSimilarity = new ArrayList<Double>();
    // LinkedList<Double> a = new LinkedList<Double> ();
   // while (it.hasNext()) {
    while(it.hasNext()) {
      Document doc = (Document) it.next();
      // Document doc2 = (Document) it.next();
      System.out.println( "currentID: " + currentQuerID);
      if (doc.getRelevanceValue() == 99)// this is the standard sentence
      {
        //StandardVector = doc.getVector();
        StandardVector = new ArrayList<Integer>();
        for(int i = 0; i < doc.getVector().size();i++)
          StandardVector.add(doc.getVector().get(i));
        currentQuerID = doc.getQueryID();
        
        similarity.add(new ArrayList<Double>());
        Jaccardsimilarity.add(new ArrayList<Double>());

        System.out.println("Standard text is:  " + doc.getText());
        tmp = 0;

      } else if (doc.getQueryID() == currentQuerID)//
      {
        //System.out.println(similarity.toString());
        tmp = computeCosineSimilarity(doc.getVector(), StandardVector);
        tmp2 = computeJaccardIndex(doc.getVector(), StandardVector);
        similarity.get(doc.getQueryID() - 1).add(tmp);

        Jaccardsimilarity.get(currentQuerID - 1).add(tmp2);

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

    
    //double metric_mrr = compute_mrr(similarity);
    //System.out.println(" (MRR) Mean Reciprocal Rank ::" + metric_mrr);
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
    //System.out.println(similarity.toString());

    double metric_mrr = compute_mrr(similarity,1);
    double jmetric_mrr = compute_mrr(Jaccardsimilarity,2);
    System.out.println(" (MRR) Mean Reciprocal Rank ::" + metric_mrr);
    System.out.println(" (MRR) Mean Reciprocal Rank ::" + jmetric_mrr);
    ///System.out.println(" (MRR) Mean Reciprocal Rank ::");
  }

  /**
   * 
   * @return cosine_similarity
   */
  private double computeCosineSimilarity(Map<String, Integer> queryVector,
          Map<String, Integer> docVector) {
    double cosine_similarity = 0.0;

    // TODO :: compute cosine similarity between two sentences

    return cosine_similarity;
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

  private double computeJaccardIndex(IntegerArray a, IntegerArray b)
  {
    double JaccardIndex = 0.0;
    double Union = 0;
    double intersection = 0;
    for (int i = 0; i < a.size(); i++) {
      if(a.get(i)>0 || b.get(i)>0)
      {
        Union++;
      }
      if(a.get(i)>0 && b.get(i)>0){
        intersection++;
      }
    }
    return intersection/Union;
  }
  
  private double computeJaccardIndex(IntegerArray a, ArrayList<Integer> b)
  {
    double JaccardIndex = 0.0;
    double Union = 0;
    double intersection = 0;
    for (int i = 0; i < a.size(); i++) {
      if(a.get(i)>0 || b.get(i)>0)
      {
        Union++;
      }
      if(a.get(i)>0 && b.get(i)>0){
        intersection++;
      }
    }
    return intersection/Union;
  }
  /**
   * 
   * @return mrr
   */
  private double compute_mrr(ArrayList<ArrayList<Double>> similarity, int type) {
    double metric_mrr = 0.0;
    System.out.println(qIdList.size());
    for (int i = 0; i < qIdList.size(); i++) {
       if(relList.get(i) == 1)
         metric_mrr += 1.0/rank(similarity.get( qIdList.get(i)-1) , i, type);
    }
    // qIdList;

    /** query and text relevant values **/
    // public ArrayList<Integer> relList;
    // TODO :: compute Mean Reciprocal Rank (MRR) of the text collection
    System.out.println(metric_mrr);

    return metric_mrr/qIdList.get(qIdList.size()-1);
  }

   private int rank(ArrayList<Double> similarity, int right, int type)
   {
     int rank = 1;
     ArrayList<Double> list = null;
     if(type == 1)
       list = simiList;
     else if(type == 2)
       list = JaccardsimiList;


     for(int i = 0; i < similarity.size(); i++)
     {
       if(similarity.get(i) > list.get(right))
         rank++;
     }
     System.out.println("rank = " + rank);
     return rank;
   }

}
