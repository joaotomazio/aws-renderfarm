package pt.ulisboa.tecnico.cnv.renderfarm.loadbalancer.regression;

import java.util.ArrayList;
import pt.ulisboa.tecnico.cnv.renderfarm.mss.models.DataMetrics;
import pt.ulisboa.tecnico.cnv.renderfarm.mss.models.TimeMetrics;

public class Regression{

    public static Long computeInstructions(ArrayList<DataMetrics> history, DataMetrics request){
        //for(DataMetrics entry : history) System.out.println(entry);
        //System.out.println(request);
        Matrix X = Matrix.XMatrixData(history);
        Matrix Y = Matrix.YMatrixData(history);
        MultiLinear ml = new MultiLinear(X, Y);
        Matrix res = ml.calculate();
        Double ans = res.getValueAt(0, 0) +
                     res.getValueAt(1, 0) * request.getSc() +
                     res.getValueAt(2, 0) * request.getSr() +
                     res.getValueAt(3, 0) * request.getWc() +
                     res.getValueAt(4, 0) * request.getWr() +
                     res.getValueAt(5, 0) * request.getCoff() +
                     res.getValueAt(6, 0) * request.getRoff();
        return ans.longValue();
    }

    public static Long computeTime(ArrayList<TimeMetrics> history, TimeMetrics request){
        Matrix X = Matrix.XMatrixTime(history);
        Matrix Y = Matrix.YMatrixTime(history);
        MultiLinear ml = new MultiLinear(X, Y);
        Matrix res = ml.calculate();
        Double ans = res.getValueAt(0, 0) +
                     res.getValueAt(1, 0) * request.getInstructions();
        return ans.longValue();
    }
}
