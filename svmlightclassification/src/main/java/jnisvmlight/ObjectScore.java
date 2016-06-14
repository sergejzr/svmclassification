package jnisvmlight;

public class ObjectScore implements Comparable<ObjectScore> {
	public final int compareWith;
	public final double key;

	public ObjectScore(int  obj, double key) {
		this.compareWith = obj;
		this.key = key;
	}

	public int compareTo(ObjectScore obj) {
		return this.compareWith-obj.compareWith;
	}

}