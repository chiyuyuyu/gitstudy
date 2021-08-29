import java.util.HashMap;
import java.io.PrintStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import img.math.Vec3;

public class TestMFF3D{
  public static void main(String[] args){
    HashMap<Vec3,Vec3> pairs = new HashMap<Vec3, Vec3>();
    final int SIZE = 9 ;
    
    
    //regular mesh cube
    try(PrintStream st= new PrintStream("3DOrder.data")){
        Vec3 order = new Vec3();
        
        for(int p=0;p<SIZE;p++) {
      	  for(int j=0;j<SIZE;j++){
      		  for(int i=0;i<SIZE;i++){
      			 order.data[0] = i/1.0f;
      			 order.data[1] = j/1.0f;
      	         order.data[2] = p/1.0f;
      	         st.println(order.data[0]+" "+order.data[1]+" "+order.data[2]);
      	      }
      		  st.println();
            }
      	  st.println();
         }
         st.println();
         
         for(int j=0;j<SIZE;j++) {
       	  for(int i=0;i<SIZE;i++){
       		  for(int p=0;p<SIZE;p++){
       			 order.data[0] = i/1.0f;
       			 order.data[1] = j/1.0f;
       	         order.data[2] = p/1.0f;
       	         st.println(order.data[0]+" "+order.data[1]+" "+order.data[2]);
       	      }
       		  st.println();
             }
       	  st.println();
          }
          st.println();
          
        for(int i=0;i<SIZE;i++) {
      	  for(int j=0;j<SIZE;j++){
      		  for(int p=0;p<SIZE-1;p++){
      			order.data[0] = i/1.0f;
      			order.data[1] = j/1.0f;
      			order.data[2] = p/1.0f;
      	         st.println(order.data[0]+" "+order.data[1]+" "+order.data[2]);
      	      }
      		  st.println();
      		  }
      	  st.println();
         }
      }catch(FileNotFoundException ex){
        System.err.print(ex);
        System.exit(1);
      }
    
    
    
    //deformed mesh cube
    try(PrintStream st= new PrintStream("3DDeformed.data")){
        Scanner scan = new Scanner(System.in);
    	final MFF3D mffd;
    	boolean flag = true;
    	while( flag) {
    		System.out.println("input from");
    		float[] from = new float[3];
    		for(int i=0;i<3;i++) {
    			from[i] = scan.nextFloat();
    		}
	        System.out.println("input to");
    		float[] to = new float[3];
    		for(int i=0;i<3;i++) {
    			to[i] = scan.nextFloat();
    		}
	        Vec3 pairf = new Vec3(from);
	        Vec3 pairt = new Vec3(to);
	        pairs.put(pairt, pairf);

	    	System.out.print("input anoter vector?");
	        flag = scan.nextBoolean();
    	}
    	scan.close();
    	
    	 mffd = new MFF3D(SIZE,SIZE,SIZE); 
    	 mffd.clearPairs();
         mffd.putPairs(pairs);
         mffd.derive();
         
    	 Vec3 from =new Vec3();
        for(int p=0;p<SIZE;p++) {
      	  for(int j=0;j<SIZE;j++){
      		  for(int i=0;i<SIZE;i++){
      			from.data[0] = i/1.0f;
                from.data[1] = j/1.0f;
                from.data[2] = p/1.0f;
       			 Vec3 to = mffd.deform(from);
      	         st.println(to.data[0]+" "+to.data[1]+" "+to.data[2]);
      	      }
      		  st.println();
            }
      	  st.println();
         }
         st.println();
         
         for(int j=0;j<SIZE;j++) {
          	  for(int i=0;i<SIZE;i++){
          		  for(int p=0;p<SIZE;p++){
            			from.data[0] = i/1.0f;
                        from.data[1] = j/1.0f;
                        from.data[2] = p/1.0f;
               			 Vec3 to = mffd.deform(from);
              	         st.println(to.data[0]+" "+to.data[1]+" "+to.data[2]);
              	      }
              		  st.println();
                    }
              	  st.println();
                 }
            st.println();
         
        for(int i=0;i<SIZE;i++) {
      	  for(int j=0;j<SIZE;j++){
      		  for(int p=0;p<SIZE-1;p++){
        		from.data[0] = i/1.0f;
                from.data[1] = j/1.0f;
                from.data[2] = p/1.0f;
           	   	Vec3 to = mffd.deform(from);
     	         st.println(to.data[0]+" "+to.data[1]+" "+to.data[2]);
      	      }
      		  st.println();
      		  }
      	  st.println();
         }
        
      }catch(FileNotFoundException ex){
        System.err.print(ex);
        System.exit(1);
      }
    
   
    System.out.println("\n  MFFD deform order points are  in \"3Dorder.data\"\n");
    System.out.println("\n  MFFD deformed grids are  in \"3Ddeformedmesh.data\"\n");
    System.out.println("  plot \"3Dorder.data\" with line, \"3Ddeformedmesh.data\" with line   with gnuplot command");
    System.out.println("  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");    
  }
}