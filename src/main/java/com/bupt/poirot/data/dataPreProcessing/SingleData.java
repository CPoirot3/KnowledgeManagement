package com.bupt.poirot.data.dataPreProcessing;

public class SingleData {
	String carName; // 车名
	long time;
	
	String domain;

//	String time; // 时间
	float x; // 纬度
	float y; // 经度
	boolean status; // 状态 0: 空车, 1: 载人
	float speed; // 速度
	byte direction; // 方向 0, 1, 2, 3, 4, 5, 6, 7

	
	public SingleData(String carName, long time, String domain, float x, float y, boolean status,
			float speed, byte direction) {
		this.carName = carName;
		this.time = time;
		this.domain = domain;
		this.x = x;
		this.y = y;
		this.status = status;
		this.speed = speed;
		this.direction = direction;
	}
}
