package invasion;

public class Coors
{
	
	private double nX;
	private double  nY;
	
	public Coors(double nXPos, double nYPos)
	{
		this.nX = nXPos;
		this.nY = nYPos;
		
	}
	
	public void incX(int nVal)
	{
		this.nX +=nVal;
		
	}
	
	public void incY(int nVal)
	{
		this.nY+= nVal;
		
		
	}
	
	public double getX()
	{
		return  this.nX;
	}
	
	public double getY()
	{
		return  this.nY;
		
	}
	
	public void setX(double dVal)
	{
		this.nX = dVal;
	}
	
	public void setY(double dVal)
	{
		this.nY = dVal;
		
	}
	
	public Coors getCor()
	{
		return this;
		
	}
	

}
