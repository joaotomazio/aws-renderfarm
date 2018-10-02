package pt.ulisboa.tecnico.cnv.renderfarm.loadbalancer.regression;

import java.util.ArrayList;
import java.util.Random;
import pt.ulisboa.tecnico.cnv.renderfarm.mss.models.DataMetrics;
import pt.ulisboa.tecnico.cnv.renderfarm.mss.models.TimeMetrics;

public class Matrix {

	private int nrows;
	private int ncols;
	private double[][] data;

	public Matrix(double[][] dat) {
		this.data = dat;
		this.nrows = dat.length;
		this.ncols = dat[0].length;
	}

	public Matrix(int nrow, int ncol) {
		this.nrows = nrow;
		this.ncols = ncol;
		data = new double[nrow][ncol];
	}

	public static Matrix XMatrixData(ArrayList<DataMetrics> history){
		Matrix m = new Matrix(history.size(), 6);
		Random rand = new Random();
		Double det = null;
		do{
			for(int i = 0; i < history.size(); i++){
				Double sc = history.get(i).getSc() + rand.nextDouble();
				Double sr = history.get(i).getSr() + rand.nextDouble();
				Double wc = history.get(i).getWc() + rand.nextDouble();
				Double wr = history.get(i).getWr() + rand.nextDouble();
				Double coff = history.get(i).getCoff() + rand.nextDouble();
				Double roff = history.get(i).getRoff() + rand.nextDouble();
				m.setValueAt(i, 0, sc);
				m.setValueAt(i, 1, sr);
				m.setValueAt(i, 2, wc);
				m.setValueAt(i, 3, wr);
				m.setValueAt(i, 4, coff);
				m.setValueAt(i, 5, roff);
			}
			Matrix transposed = MatrixMathematics.transpose(m);
			Matrix multiplied = MatrixMathematics.multiply(transposed, m);
			det = MatrixMathematics.determinant(multiplied);
		} while(det == 0);
		return m;
	}

	public static Matrix YMatrixData(ArrayList<DataMetrics> history){
		Matrix m = new Matrix(history.size(), 1);
		Random rand = new Random();
		for(int i = 0; i < history.size(); i++){
			Double instructions = (history.get(i).getInstructions()) + rand.nextDouble();
			m.setValueAt(i, 0, instructions);
		}
		return m;
	}

	public static Matrix XMatrixTime(ArrayList<TimeMetrics> history){

		Matrix m = new Matrix(history.size(), 1);
		for(int i = 0; i < history.size(); i++){
			m.setValueAt(i, 0, new Long(history.get(i).getInstructions()).doubleValue());
		}
		//System.out.println(m);
		return m;
	}

	public static Matrix YMatrixTime(ArrayList<TimeMetrics> history){
		Matrix m = new Matrix(history.size(), 1);
		for(int i = 0; i < history.size(); i++){
			m.setValueAt(i, 0, new Long(history.get(i).getTime()).doubleValue());
			//System.out.println(history.get(i).getTime());
		}
		//System.out.println(m);
		return m;
	}

	public int getNrows() {
		return nrows;
	}

	public void setNrows(int nrows) {
		this.nrows = nrows;
	}

	public int getNcols() {
		return ncols;
	}

	public void setNcols(int ncols) {
		this.ncols = ncols;
	}

	public double[][] getValues() {
		return data;
	}

	public void setValues(double[][] values) {
		this.data = values;
	}

	public void setValueAt(int row, int col, double value) {
		data[row][col] = value;
	}

	public double getValueAt(int row, int col) {
		return data[row][col];
	}

	public boolean isSquare() {
		return nrows == ncols;
	}

	public int size() {
		if (isSquare())
			return nrows;
		return -1;
	}

	public Matrix multiplyByConstant(double constant) {
		Matrix mat = new Matrix(nrows, ncols);
		for (int i = 0; i < nrows; i++) {
			for (int j = 0; j < ncols; j++) {
				mat.setValueAt(i, j, data[i][j] * constant);
			}
		}
		return mat;
	}
	public Matrix insertColumnWithValue1() {
		Matrix X_ = new Matrix(this.getNrows(), this.getNcols()+1);
		for (int i=0;i<X_.getNrows();i++) {
			for (int j=0;j<X_.getNcols();j++) {
				if (j==0)
					X_.setValueAt(i, j, 1);
				else
					X_.setValueAt(i, j, this.getValueAt(i, j-1));

			}
		}
		return X_;
	}

	@Override
	public String toString(){
		String ans = "";
		for(int i = 0; i < this.getNrows(); i++){
			for(int j = 0; j < this.getNcols(); j++){
				ans += this.getValueAt(i, j) + " ";
			}
			ans += "\n";
		}
		return ans;
	}
}
