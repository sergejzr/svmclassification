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

import java.io.IOException;
import java.io.Writer;

/**
 * A labeled feature vector.
 * 
 * @author Tom Crecelius & Martin Theobald
 */
public class LabeledFeatureVector extends FeatureVector {
    private static final long serialVersionUID = 1L;

    protected double m_label;

	

    protected LabeledFeatureVector() {
	this("",0, null, null);
    }

    public LabeledFeatureVector(double label, int size) {
	super(size);
	this.m_label = label;
    }
    public LabeledFeatureVector(String id, double label, int size) {
    	super(id,size);
    	this.m_label = label;
        }
    public LabeledFeatureVector(String id, double label, int[] dims, double[] vals) {
	super(id,dims, vals);
	
	this.m_label = label;
    }



	public LabeledFeatureVector(double labelvalue, int[] dimarray,
			double[] valarray) {
		this("",labelvalue,dimarray,valarray);
	}

	public double getLabel() {
	return m_label;
    }

    public void setFeatures(double label, int[] dims, double[] vals) {
	this.m_label = label;
	setFeatures(dims, vals);
    }

    public void setLabel(double label) {
	this.m_label = label;
    }

    public void writeout(Writer w) throws IOException
    {
    	w.write(Double.toString(m_label * m_factor));
    	w.write(" ");
    	super.writeout(w);
    	w.write("\n");
    }
    public String toString() {
	return String.valueOf(m_label * m_factor) + " " + super.toString()
		+ "\n";
    }
}
