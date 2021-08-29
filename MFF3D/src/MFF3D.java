import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import img.math.Vec3;

public class MFF3D{
  final static int MAXCOUNT=10;
  final static float ALPHA = 0.5f; //Section 4.3
  final HashMap<Vec3, Vec3> pairs;
  final int GRIDX;
  final int GRIDY;
  final int GRIDZ;
  final ArrayList<Function3W> ws;

  public MFF3D(int gridx, int gridy, int gridz){
    this(gridx, gridy, gridz, new HashMap<Vec3, Vec3>());
  }

  public MFF3D(int gridx, int gridy, int gridz, HashMap<Vec3, Vec3> pairs){
    GRIDX = gridx;
    GRIDY = gridy;
    GRIDZ = gridz;
    this.pairs = pairs;
    ws = new ArrayList<Function3W>();
  }
  
  public void putPair(Vec3 to, Vec3 from){
    pairs.put(to,from);
  }

  public void putPairs(HashMap<Vec3, Vec3> newpairs){
    pairs.putAll(newpairs);
  }

  public void clearPairs(){
    pairs.clear();
  }
  
  public void derive(){
    ws.clear();
    HashMap<Vec3, Vec3> tmppairs = new HashMap<Vec3, Vec3>(pairs);
    int level=0;
    for(int index=0;index<MAXCOUNT; index++){
      Function3W funcw = new Function3W(level,this);
      boolean zerofoureight = funcw.derive(tmppairs);
      Set<Vec3> tos = tmppairs.keySet();
      for(Vec3 to : tos){
        tmppairs.put(to, funcw.deform(tmppairs.get(to)));//replace "from point"
      }
      ws.add(funcw);
      if(calcError(tmppairs, level)){
        level++;
        // This implementation, there is no iteration limit in one grid level, 
        // although the manipulations are performed no more than twice in the
        // same grid coarse level in the paper. (Secdtion4.3 in the right column)
      }
    }
  }

  private boolean calcError(HashMap<Vec3, Vec3> pairs, int level){
    Set<Vec3> tos = pairs.keySet();
    float maxv=0;
    for(Vec3 to: tos){
      Vec3 tmp = Vec3.minus(to,pairs.get(to));
      float dotv = Vec3.dot(tmp,tmp); //Section 4.3
      if(maxv < dotv){
        maxv = dotv;
      }
    }
    double threshold = ALPHA*(0.48/(1<<level))*(0.48/(1<<level));
    if(threshold < maxv){
      return false;
    }else{
      return true;
    }
  }
  
  public Vec3 deform(Vec3 point){
    Vec3 ret = point;
    for(Function3W funcw : ws){
      ret = funcw.deform(ret);
    }
    return ret;
  }
}