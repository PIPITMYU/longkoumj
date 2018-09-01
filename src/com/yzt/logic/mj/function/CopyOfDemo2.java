package com.yzt.logic.mj.function;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import com.yzt.logic.mj.domain.Action;
import com.yzt.logic.mj.domain.Player;
import com.yzt.logic.mj.domain.RoomResp;
import com.yzt.logic.util.Cnst;
import com.yzt.logic.util.MahjongUtils;

public class CopyOfDemo2 {
	public static void main(String[] args) {
		RoomResp room = new RoomResp();
		List<Integer> pais = MahjongUtils.getPais();
		room.setCurrentMjList(pais);
		room.setLastAction(1);
		Player p1 = new Player();
		p1.initPlayer(500000, 2, 0);
		ArrayList<Integer> currentMjList = new ArrayList<Integer>();
		currentMjList.add(1);
		currentMjList.add(1);
		currentMjList.add(2);
		currentMjList.add(2);
		//currentMjList.add(9);
		room.setHunPai(9);
		ArrayList<Action> arrayList = new ArrayList<Action>();
		Action action = new Action(2,64,1l,2l,8);
		arrayList.add(action);
//		p1.setActionList(arrayList);
		p1.setCurrentMjList(currentMjList);
		MahjongUtils.isQingYiSe(p1, room, p1.getCurrentMjList());
	}
}
