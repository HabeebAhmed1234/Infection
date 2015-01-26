package com.cromiumapps.gravwar;

public class Position {
	private float m_x = 0;
	private float m_y = 0;
	
	public Position(float x, float y)
	{
		m_x = x;
		m_y = y;
	}
	
	public void setX(float x)
	{
		m_x = x;
	}
	
	public float getX()
	{
		return m_x;
	}
	
	public void setY(float y)
	{
		m_y = y;
	}
	
	public float getY()
	{
		return m_y;
	}
	
	public void changeX(float c_x)
	{
		m_x+=c_x;
	}
	
	public void changeY(float c_y)
	{
		m_y+=c_y;
	}
}
