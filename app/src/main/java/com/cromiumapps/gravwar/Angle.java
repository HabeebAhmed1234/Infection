package com.cromiumapps.gravwar;

public class Angle {
	private float angle = 0;
	
	public Angle(float angle)
	{
		while(angle>(Math.PI*2)){angle-=(Math.PI*2);}
		while(angle<0){angle+=(Math.PI*2);}
		this.angle = angle;
	}
	
	private float fix(float angle)
	{
		while(angle>(Math.PI*2)){angle-=(Math.PI*2);}
		while(angle<0){angle+=(Math.PI*2);}
		return angle;
	}
	
	public void set(float angle)
	{
		this.angle = fix(angle);
	}
	
	public float get()
	{
		return fix(angle);
	}
	
	public float add(float otherAngle)
	{
		this.angle +=otherAngle;
		this.angle = fix(this.angle);
		return this.angle;
	}
	
	public float subtract(float otherAngle)
	{
		this.angle -=otherAngle;
		this.angle = fix(this.angle);
		return this.angle;
	}
}
