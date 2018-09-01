package com.yzt.logic.mj.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.yzt.logic.mj.domain.Player;
import com.yzt.logic.mj.domain.RoomResp;
import com.yzt.logic.util.MahjongUtils;


public class Demo4 {
	
	public static void main(String[] args) {
		Player p = new Player();
		ArrayList<Integer> arrayList = new ArrayList<Integer>();
		RoomResp room  = new RoomResp();
		arrayList.add(1);
		arrayList.add(2);
		arrayList.add(3);
		arrayList.add(4);
		arrayList.add(5);
		arrayList.add(6);
		arrayList.add(7);
		arrayList.add(8);
		arrayList.add(9);
		arrayList.add(31);
		arrayList.add(31);
		arrayList.add(31);
		arrayList.add(32);
		arrayList.add(8);
		p.setCurrentMjList(arrayList);
		room.setHunPai(9);
		List<Integer> checkActionList = MahjongUtils.checkActionList(p, 32, room, 1,0);
		System.out.println(checkActionList);
	}
	
}
