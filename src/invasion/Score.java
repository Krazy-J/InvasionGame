package invasion;

import java.io.PrintWriter;

public class Score implements Comparable<Score>
{
	
	private String sName;
	private int nLevel;
	
	public Score(String sName, int nLevel)
	{
		this.sName = sName;
		this.nLevel = nLevel;
	}
	
	public Score()
	{
		
	}
	
	public Score setName(String sName)
	{
		this.sName = sName;
		return this;
	}
	
	public Score setScore(int nLevel)
	{
		this.nLevel = nLevel;
		return this;
	}
	
	public String getName()
	{
		return this.sName;
	}

	public int getScore()
	{
		return this.nLevel;
	}
	
	

	@Override
	public int compareTo(Score obOther)
	{
		return obOther.getScore() - this.getScore();
	}
	
	@Override
	public String toString()
	{
		return String.format("Player: %s Level Completed: %d", this.getName(), this.getScore());
	}
	
	public void writeToCSVFile(PrintWriter obWriter)
	{
		obWriter.printf("%s,%d", this.sName, this.nLevel);
	}
	
	
	
}
