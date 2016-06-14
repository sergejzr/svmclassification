/*
 * JNI_SVM-light - A Java Native Interface for SVM-light
 * 
 * Copyright (C) 2005 
 * Tom Crecelius & Martin Theobald 
 * Max-Planck Institute for Computer Science
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package jnisvmlight;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The main interface class that transfers the training data to the SVM-light
 * library by a native call. Optionally takes as input an individually modified
 * set of training parameters or an array of string paramaters that exactly
 * simulate the command line input parameters used by the SVM-light binaries.
 * This class can also be used for native classification calls.
 * 
 * @author Tom Crecelius & Martin Theobald
 */
public class SVMLightInterface {

  /**
   * Apply an in-place quicksort prior to each native training call to
   * SVM-light. SVM-light requires each input feature vector to be sorted in
   * ascending order of dimensions. Disable this option if you are sure to
   * provide sorted vectors already.
   */
  public static boolean SORT_INPUT_VECTORS = true;

  static {
   // System.loadLibrary("svmlight");
    //System.loadLibrary("C:/Test2/svmlight.dll");
  }

  /**
   * Reads a set of labeled training vectors from a URL. The format is
   * compatible to the SVM-light training files.
   */
  public static LabeledFeatureVector[] getLabeledFeatureVectorsFromURL(
      URL file, int numOfLinesToSkip) throws ParseException {

    ArrayList<LabeledFeatureVector> data = new ArrayList<LabeledFeatureVector>();
    LabeledFeatureVector[] traindata = null;
    BufferedReader bi = null;
    
    try {
      
      bi = new BufferedReader(new InputStreamReader(file
          .openStream()));
      
      String line = null;
      ArrayList<String> dimlist, vallist;
      String label, dimval, dim, val;
      String[] tokens;
      
      int idx, cnt = 0;
      while ((line = bi.readLine()) != null) {
        cnt++;
        if (cnt <= numOfLinesToSkip) {
          continue;
        }
        label = null;
        tokens = line.trim().split("[ \\t\\n\\x0B\\f\\r]");
        if (tokens.length > 1) {
          label = tokens[0];
          dimlist = new ArrayList<String>();
          vallist = new ArrayList<String>();
          for (int tokencnt = 1; tokencnt < tokens.length; tokencnt++) {
            dimval = tokens[tokencnt];
            if (dimval.trim().startsWith("#"))
              break;
            
            idx = dimval.indexOf(':');
            if (idx >= 0) {
              dim = dimval.substring(0, idx);
              val = dimval.substring(idx + 1, dimval.length());
              dimlist.add(dim);
              vallist.add(val);
            } else {
              throw new ParseException("Parse error in FeatureVector of file '"
                  + file.toString() + "' at line: " + cnt + ", token: "
                  + tokencnt + ". Could not estimate a \"int:double\" pair ?! "
                  + file.toString()
                  + " contains a wrongly defined feature vector!", 0);
            }
          }
          if (dimlist.size() > 0) {
            double labelvalue = new Double(label).doubleValue();
            int[] dimarray = new int[dimlist.size()];
            double[] valarray = new double[vallist.size()];
            for (int i = 0; i < dimlist.size(); i++) {
              dimarray[i] = new Integer((String) dimlist.get(i)).intValue();
            }
            for (int i = 0; i < vallist.size(); i++) {
              valarray[i] = new Double((String) vallist.get(i)).doubleValue();
            }
            LabeledFeatureVector lfv = new LabeledFeatureVector(labelvalue, dimarray, valarray);
            data.add(lfv);
          }
        } else {
          throw new ParseException("Parse error in FeatureVector of file '"
              + file.toString() + "' at line: " + cnt + ". "
              + " Wrong format of the labeled feature vector?", 0);
        }
      }
      if (data.size() > 0) {
        traindata = new LabeledFeatureVector[data.size()];
        for (int i = 0; i < data.size(); i++) {
          traindata[i] = (LabeledFeatureVector) data.get(i);
        }
      } else {
        throw new ParseException("No labeled features found within " + cnt
            + "lines of file '" + file.toString() + "'.", 0);
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    } finally {
      if (bi != null) {
        try {
          bi.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return traindata;
  }

  protected TrainingParameters m_tp;

  /**
   * Performs a classifcation step as a native call to SVM-light. If this method
   * is used exlusively, no additional SVMLightModel object has to be kept in
   * the Java runtime process.
   */
  //public native double classifyNative(FeatureVector doc);

  public TrainingParameters getTrainingParameters() {
    return m_tp;
  }

  private void sort(FeatureVector[] trainingData) {
	    for (int i = 0; i < trainingData.length; i++) {
	      if (trainingData[i] != null) {
	    	  ArrayList<ObjectScore> al = new ArrayList<ObjectScore>(trainingData[i].m_dims.length);
	    	  for (int cnt=0;cnt<trainingData[i].m_dims.length;cnt++) {
	    		  al.add(new ObjectScore(trainingData[i].m_dims[cnt], trainingData[i].m_vals[cnt]));
	    	  }
	    	  Collections.sort(al);
	    	  int cnt=0;
	    	  for (ObjectScore obsc:al) {
	    		  trainingData[i].m_dims[cnt] = obsc.compareWith;
	    		  trainingData[i].m_vals[cnt] = obsc.key;
	    		  cnt++;
	    	  }
	      }
	    }
	  }

  private native SVMLightModel trainmodel(LabeledFeatureVector[] traindata,
      TrainingParameters p);

  public SVMLightModel trainModel(LabeledFeatureVector[] trainingData) {
    this.m_tp = new TrainingParameters();
    if (SORT_INPUT_VECTORS) {
      //System.out.println("sortieren ..."); 
      sort(trainingData);
      //System.out.println("sortiert"); 
    }
    return trainmodel(trainingData, m_tp);
  }

  public SVMLightModel trainModel(LabeledFeatureVector[] trainingData,
      String[] argv) {
    this.m_tp = new TrainingParameters(argv);
    if (SORT_INPUT_VECTORS) {
      sort(trainingData);
    }
    return trainmodel(trainingData, m_tp);
  }

  public SVMLightModel trainModel(LabeledFeatureVector[] trainingData,
      TrainingParameters tp) {
    //System.out.println("drin"); 
    this.m_tp = tp;
    if (SORT_INPUT_VECTORS) {
      //System.out.println("sortieren ..."); 
      sort(trainingData);
      //System.out.println("sortiert"); 
    }
    return trainmodel(trainingData, m_tp);
  }
}
