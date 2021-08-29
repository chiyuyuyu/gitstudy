public class CubicBSpline3DimensionComposition implements Function3Dimension{
  final CubicBSpline3Dimension bs;
  public final int gridx;
  public final int gridy;
  public final int gridz;
 
  protected final float[] ctrls;

  public CubicBSpline3DimensionComposition(int gridx, int gridy, int gridz){
    this.gridx = gridx;
    this.gridy = gridy;
    this.gridz = gridz;
    ctrls = new float[(1+gridx+2)*(1+gridy+2)*(1+gridz+2)];
    bs = new CubicBSpline3Dimension();
  }
  
  protected int address(int x, int y,int z){
    return (z+1)*(1+gridx+2)*(1+gridy+2)+(y+1)*(1+gridx+2)+(x+1); 
  }

  private boolean isInside(float x, float y, float z){
    return (0 <= x) && (x <= gridx-1) && (0 <= y) && (y <= gridy-1)  && (0 <= z) && (z <= gridz-1);
  }
  
  public void setCtrlPoint(int x, int y, int z, float value){
    ctrls[address(x,y,z)] = value;
  }

  public void setCtrlPoints(float[] values){
	  for(int j=0;j<gridz;j++) {
		  for(int i=0;i<gridy;i++){
			System.arraycopy(values, gridx*i*j, ctrls, (j+1)*(1+gridx+2)*(1+gridy+2)+
					(i+1)*(1+gridx+2)+1, gridx);
		  }  
	  }
  }
  
  public float function(float x, float y,float z){
    int floorx = (int)Math.floor(x);
    int floory = (int)Math.floor(y);
    int floorz = (int)Math.floor(z);
    double ret=0;
    for(int p=0;p<4;p++) {
	    for(int j=0;j<4;j++){
	      for(int i=0;i<4;i++){
	        if(isInside(x,y,z)){
	          double tmp = bs.function(x-floorx+3-i, y-floory+3-j,z-floorz+3-p);
	          ret += tmp*ctrls[address(floorx-1+i, floory-1+j, floorz-1+p)];
	        }
	      }
	    }
    }
    return (float)ret;
  }
}

