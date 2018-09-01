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

public class Demo2 {
	public static void main(String[] args) {
		RoomResp room = new RoomResp();
		List<Integer> pais = MahjongUtils.getPais();
		room.setCurrentMjList(pais);
		room.setLastAction(1);
		Player p1 = new Player();
		p1.initPlayer(500000, 2, 0);
		ArrayList<Integer> currentMjList = new ArrayList<Integer>();
		currentMjList.add(20);
		currentMjList.add(20);
		currentMjList.add(20);
		currentMjList.add(12);
		currentMjList.add(13);
		currentMjList.add(14);
		currentMjList.add(15);
		currentMjList.add(7);
		currentMjList.add(8);
		currentMjList.add(9);
		
		HashMap<Integer, List<Integer>> zhangMaoMap = new HashMap<Integer, List<Integer>>();
		zhangMaoMap.put(140, currentMjList);
		p1.setZhangMaoMap(zhangMaoMap);
		ArrayList<Action> actionList1 = new ArrayList<Action>();
		Action action = new Action(11,140,123l,123l,19);
		actionList1.add(action);
		p1.setActionList(actionList1);
		p1.setCurrentMjList(currentMjList);
//		p1.setCurrentMjList(MahjongUtils.paiXu(MahjongUtils.faPai(room.getCurrentMjList(), 14)));
		Integer hunPai = MahjongUtils.dingHunPai(room.getCurrentMjList(),room);
		System.out.println("混牌:"+hunPai);
		room.setHunPai(hunPai);
		//3.1看庄家有没有暗杠.带混检测, 有没有胡牌等.
		List<Integer> actionList = MahjongUtils.checkActionList(p1,15,room,Cnst.CHECK_TYPE_ZIJIMO,0);
		System.out.println(actionList);
		Scanner sc = new Scanner(System.in);
		System.out.println("请输入要测试的方法");
		while(true){
			String next = sc.next();
			if(next.equals("exit")){
				break;
			}
			switch (next) {
			case "gang":
				System.out.println(p1.getCurrentMjList());
				break;
			case "angang":
				System.out.println(p1.getCurrentMjList());
				break;
			case "hu":
				System.out.println(p1.getCurrentMjList());
				break;
			case "zhangmao":
				System.out.println(p1.getZhangMaoMap());
				System.out.println(p1.getCurrentMjList());
				break;
 			default:
				break;
			}
		}
		
		
	}
}
