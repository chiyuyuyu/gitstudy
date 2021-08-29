import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import img.math.Vec3;

public class Function3W {
	  final int level;
	  final float scale;
	  int invscale;
	  final Vec3[] ctrls;
	  final int GRIDX;
	  final int GRIDY;
	  final int GRIDZ;
	  final static float threshold = 0.48f;
	  final CubicBSpline3DimensionComposition bscx;
	  final CubicBSpline3DimensionComposition bscy;
	  final CubicBSpline3DimensionComposition bscz;
	  final CubicBSpline3Dimension bs;
	  ArrayList<Vec3>[] deltaPhaics;

	  @SuppressWarnings("unchecked")
	  public Function3W(int level,MFF3D mffd){
	    this.level= 0;
	    invscale = 1+level;
	    scale = (float)(1.0/invscale); 
	    GRIDX = mffd.GRIDX*invscale+2;//+1 boundary
	    GRIDY = mffd.GRIDY*invscale+2;//+1 boundary
	    GRIDZ = mffd.GRIDZ*invscale+2;//+1 boundary
	    ctrls = new Vec3[GRIDX*GRIDY*GRIDZ];
	    bs = new CubicBSpline3Dimension();
	    bscx = new CubicBSpline3DimensionComposition(GRIDX,GRIDY,GRIDZ);
	    bscy = new CubicBSpline3DimensionComposition(GRIDX,GRIDY,GRIDZ);
	    bscz = new CubicBSpline3DimensionComposition(GRIDX,GRIDY,GRIDZ);
	    deltaPhaics =
	      (ArrayList<Vec3>[])new ArrayList<?>[GRIDX*GRIDY*GRIDZ];
	  }

	  @SuppressWarnings("unchecked")
	  public boolean derive(HashMap<Vec3,Vec3> pairs){
	    Set<Vec3> tos = pairs.keySet();
	    ArrayList<Float>[] wcPows =
	      (ArrayList<Float>[])new ArrayList<?>[GRIDX*GRIDY*GRIDZ];
	    for(int i=0; i<GRIDX*GRIDY*GRIDZ; i++){
	      deltaPhaics[i] = new ArrayList<Vec3>();
	      wcPows[i] = new ArrayList<Float>();
	    }
	    
////////////////////////////////not sure about e4and5
	    /**
	     * Equation (4)
	     */
	    for(Vec3 to : tos){
	      Vec3 from = pairs.get(to);
	      Vec3 DeltaQ = Vec3.minus(to,from);
	      DeltaQ.mul(invscale);
	      //System.out.println("DeltaQ:"+DeltaQ);
	      float x = from.data[0]*invscale+1; // +1 shift is for boundary offset width
	      int left = (int)Math.floor(x);
	      float y = from.data[1]*invscale+1; // +1 shift is for boundary offset width
	      int bottom = (int)Math.floor(y);
	      float z = from.data[2]*invscale+1; // +1 shift is for boundary offset width
	      int emm = (int)Math.floor(z);	      
	      float sumOfwabcPow = 0;
	      for(int p=-1;p<10;p++) {
		      for(int j=-1;j<10;j++){
		        for(int i=-1;i<10;i++){
		          float t = bs.function(x-left+2-i, y-bottom+2-j,z-emm+2-p);
		          sumOfwabcPow += t*t*t;
		        }
		      }
	      }
	      for(int p=-1;p<10;p++) {
		      for(int j=-1;j<10;j++){
		        for(int i=-1;i<10;i++){
		          if(!isInside(left+i, bottom+j,emm+p)){
		            break;
		          }
		          float wklg = bs.function(x-left+2-i, y-bottom+2-j, z-emm+2-p);
		          deltaPhaics[address(left+i,bottom+j,emm+p)].add(Vec3.times(DeltaQ,wklg/sumOfwabcPow));
		          //Equ(4), which is $ \Delta \phai_c $ in Equ(5)
		          wcPows[address(left+i,bottom+j,emm+p)].add(wklg*wklg);
	
		          //$ w_c^2 $ in Equ(5)
		        }
		      }
	      }
	    }

	    /**
	     * Equation (5)
	     */
	    boolean isClamp = false;
	    for(int i=0; i<GRIDX*GRIDY*GRIDZ; i++){
	      Vec3 numerator = new Vec3(0,0,0);
	      float denominator =0;
	      for(int j=0; j<wcPows[i].size() ;j++){
	        numerator.add(Vec3.times(deltaPhaics[i].get(j),wcPows[i].get(j)));
	        denominator += wcPows[i].get(j);
	        //System.out.println("deltaPhaics"+deltaPhaics[i].get(j)+ wcPows[i].get(j));
	      }
	      if(10E-8 < Math.abs(denominator)){
	        numerator.mul(1.0f/denominator);
	      }
	      isClamp = clamp(numerator) || isClamp;
	                // $-0.48 < \Delta\phai_x,\Delta\phai_y < 0.48$
	      numerator.mul(scale); // $ \Delta\phai/2^(level) $
	      //numerator.add(originalctrls[i]);//$ \Delta\phai+\phai $ 
	      ctrls[i] = numerator;
	    }

	    /**
	     * Set control points
	     */
	    for(int x=0;x<GRIDZ;x++) {
		    for(int y=0;y<GRIDX;y++){
		      for(int z=0;z<GRIDY;z++){
		        bscx.setCtrlPoint(x,y,z,ctrls[z*GRIDX*GRIDY+y*GRIDX+x].data[0]);
		        bscy.setCtrlPoint(x,y,z,ctrls[z*GRIDX*GRIDY+y*GRIDX+x].data[1]);
		        bscz.setCtrlPoint(x,y,z,ctrls[z*GRIDX*GRIDY+y*GRIDX+x].data[2]);
		      }
		    }
	    }
	    return isClamp;
	  }

	  private boolean clamp(Vec3 vec){
	    boolean ret = false;
	    if(vec.data[0]<-threshold){
	      vec.data[0]=-threshold;
	      ret = true;
	    }else if(threshold < vec.data[0]){
	      vec.data[0]=threshold;
	      ret = true;
	    }
	    if(vec.data[1]<-threshold){
	      vec.data[1]=-threshold;
	      ret = true;
	    }else if(threshold < vec.data[1]){
	      vec.data[1]=threshold;
	      ret = true;
	    }
	    if(vec.data[2]<-threshold){
		      vec.data[2]=-threshold;
		      ret = true;
		    }else if(threshold < vec.data[2]){
		      vec.data[2]=threshold;
		      ret = true;
		    }
	    return ret;
	  }

	  private boolean isInside(int x, int y, int z){
	    if (x < 0 || GRIDX <= x || y < 0 || GRIDY <= y || z < 0 || GRIDZ <= z){
	      return false;
	    }
	    return true;
	  } 

	  private int address(final int x, final int y, final int z){
	    return z*GRIDX*GRIDY+y*GRIDX+x;
	  }
	  
	  public Vec3 deform(Vec3 from){
	    float x = from.data[0]+
	      bscx.function(from.data[0]*invscale+1, from.data[1]*invscale+1, 
	    		  from.data[2]*invscale+1)*scale;
	                     // +1 shift is the adjustment to the boundary offset width
	    float y = from.data[1]+
	      bscy.function(from.data[0]*invscale+1, from.data[1]*invscale+1,
	    		  from.data[2]*invscale+1)*scale;
	                     // +1 shift is the adjustment to the boundary offset width
	    float z = from.data[2]+
	  	  bscz.function(from.data[0]*invscale+1, from.data[1]*invscale+1, 
	  			  from.data[2]*invscale+1)*scale;
	                    // +1 shift is the adjustment to the boundary offset width
	    return new Vec3(x,y,z);
	  }
	
}
