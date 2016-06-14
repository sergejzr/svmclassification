package jnisvmlight; 
import java.util.ArrayList;
import java.util.Collections;
 


public class DimWeight implements Comparable<DimWeight>
{
  public int dim; 
  public double weight; 
  
  public DimWeight(int dim, double weight)
  {
    this.dim = dim; 
    this.weight = weight; 
  }
  
   public int compareTo(DimWeight o) 
   {
     DimWeight inf = (DimWeight)o ; 
     if (Math.abs(this.weight) < Math.abs(inf.weight) ) 
     {
       return 1 ; 
     }
     else if (Math.abs(this.weight) > Math.abs(inf.weight) ) 
     {
       return -1 ; 
     }
     else 
     {
       return 0 ; 
     }
   } 
   
  public String toString()
   {
     return "(" + dim + "," + weight + ")"; 
   }
     
   
   
   public static void main(String[] args)
   {
     ArrayList<DimWeight> list = new ArrayList<DimWeight>(); 
     list.add(new DimWeight(1,-1)); 
     list.add(new DimWeight(2,-7)); 
     list.add(new DimWeight(3,4)); 
     list.add(new DimWeight(4,2)); 
     list.add(new DimWeight(5,-3)); 
     Collections.sort(list); 
     System.out.println(list); 
   }
   
}
   
