import img.math.spline.CubicBSpline;

public class CubicBSpline3Dimension implements Function3Dimension{

	  final CubicBSpline bs;
	  
	  public CubicBSpline3Dimension(){
		    bs = new CubicBSpline();
		  }
	  
	  public float function(float x, float y,float z){
	    return bs.function(x)*bs.function(y)*bs.function(z);
	  }
	}