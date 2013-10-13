package edu.cmu.lti.f13.hw4.hw4_shenwu.collectionreaders;

import java.util.ArrayList;
import java.util.Scanner;

import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import edu.cmu.lti.f13.hw4.hw4_shenwu.typesystems.Document;

public class DocumentReader extends JCasAnnotator_ImplBase {

  @Override
  public void process(JCas jcas) throws AnalysisEngineProcessException {
    Scanner scanner = new Scanner(jcas.getDocumentText());
    scanner.useDelimiter("\n");
    int startOffset = 0;
    while (scanner.hasNext()) {
      // reading sentence from the CAS

      String sLine = scanner.next();
      // TODO: make sure information from text collection are extracted correctly
      //System.out.println(sLine);
      ArrayList<String> docInfo = parseDataLine(sLine);
      
      // This is to make sure that parsing done properly and
      // minimal data for rel,qid,text are available to proceed
      if (docInfo.size() < 3) {
        System.err.println("Not enough information in the line");
        return;
      }
      int rel = Integer.parseInt(docInfo.get(0));
      int qid = Integer.parseInt(docInfo.get(1));
      String txt = docInfo.get(2);

      Document doc = new Document(jcas);
      doc.setText(txt);
      doc.setQueryID(qid);
      // Setting relevance value
      doc.setRelevanceValue(rel);
      if(rel > 10)
      doc.setBegin(13 + startOffset);
      else
      doc.setBegin(12 + startOffset);
 
      doc.setEnd(startOffset + sLine.length());
      startOffset += sLine.length()+1;
      //doc.addToIndexes();
      
      // Adding populated FeatureStructure to CAS
      jcas.addFsToIndexes(doc);
    }
  }

  public static ArrayList<String> parseDataLine(String line) {
    ArrayList<String> docInfo;

    String[] rec = line.split("[\\t]");
    String sResQid = (rec[0]).replace("qid=", "");
    String sResRel = (rec[1]).replace("rel=", "");

    StringBuffer sResTxt = new StringBuffer();
    for (int i = 2; i < rec.length; i++) {
      sResTxt.append(rec[i]).append(" ");
    }

    docInfo = new ArrayList<String>();
    docInfo.add(sResRel);
    docInfo.add(sResQid);
    docInfo.add(sResTxt.toString());
     //System.out.println(docInfo.get(0) + " ++++++++++++" + docInfo.get(1) + " ++++++++" +
     //        docInfo.get(2));
    return docInfo;
  }

}