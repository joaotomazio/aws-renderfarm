package pt.ulisboa.tecnico.cnv.renderfarm.loadbalancer.regression;

public class MultiLinear {

	private Matrix X;
	final private Matrix Y;
	final private boolean bias;

	public MultiLinear(final Matrix x, final Matrix y) {
		this(x,y,true);
	}

	public MultiLinear(final Matrix x, final Matrix y, final boolean bias) {
		super();
		this.X = x;
		this.Y = y;
		this.bias = bias;
	}

	public Matrix calculate(){
		if (bias) this.X = X.insertColumnWithValue1();
		final Matrix Xtr = MatrixMathematics.transpose(X); //X'
		final Matrix XXtr = MatrixMathematics.multiply(Xtr,X); //XX'
		final Matrix inverse_of_XXtr = MatrixMathematics.inverse(XXtr); //(XX')^-1
		final Matrix XtrY = MatrixMathematics.multiply(Xtr,Y); //X'Y
		Matrix multiplied = MatrixMathematics.multiply(inverse_of_XXtr,XtrY); //(XX')^-1 X'Y
		return multiplied;
	}
}
