package com.yzt.logic.mj.function;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.yzt.logic.mj.domain.Player;
import com.yzt.logic.util.MahjongUtils;

public class Demo {

public static void main(String[] args) {
	Player p = new Player();
	ArrayList<Integer> arrayList = new ArrayList<Integer>();
	arrayList.add(1);
	arrayList.add(19);
	arrayList.add(10);
	arrayList.add(9);
	arrayList.add(18);
	arrayList.add(27);
	p.setCurrentMjList(arrayList);
	List<Integer> actionList = new ArrayList<Integer>();
	List<Integer> zhangMao = new ArrayList<Integer>();
	zhangMao.add(1);
	zhangMao.add(10);
	zhangMao.add(19);
	List<Integer> zhangMao1 = new ArrayList<Integer>();
	zhangMao1.add(9);
	zhangMao1.add(18);
	zhangMao1.add(27);
	List<List<Integer>> z = new ArrayList<List<Integer>>();
	z.add(zhangMao);
	z.add(zhangMao1);
	
	HashMap<Integer, List<Integer>> hashMap = new HashMap<Integer, List<Integer>>();
	hashMap.put(140, zhangMao);
	hashMap.put(141, zhangMao1);
	
	//只有开局才可以长毛
		for (int i = 0; i < z.size(); i++) {
			if(p.getCurrentMjList().containsAll(z.get(i))){
				for (Integer key : hashMap.keySet()) {
				      if(z.get(i).containsAll(hashMap.get(key))){
				    	  actionList.add(key);
				      }
				}
			}
		}

		System.out.println(actionList);
	}

private static void test() {
	ChangMao changMao = ChangMao.ZHONG_FA_BAI;
	System.out.println(changMao.getName());
	switch(changMao.getName()){
	case "32-33-34":
		System.out.println("d");
		break;
	default:
		break;
	}
	
	ChangMao[] values = ChangMao.values();
	for (ChangMao string : values) {
		if(string.getName().equals("32-33-34")){//当指定对象等于此枚举常量时.
			System.out.println("dddd");
		}
	}
		
	ArrayList<Integer> arrayList = new ArrayList<Integer>();
	arrayList.add(1);
	List<Integer> faPai = MahjongUtils.faPai(arrayList,2);
	System.out.println(faPai);
}

private static void asd() {
	try {
		OutputStream out = new FileOutputStream("shuChuWenJian.txt",true);
	
		Integer clubId=882745;
		Integer userId=377363;
		Integer status=1;
		Long createTime=1523439875318l;
		String x="INSERT INTO CLUB_USER(CLUB_ID,USER_ID,STATUS,CREATE_TIME ) VALUES ("+clubId+","+userId+","+createTime+");";
	    for (int i = 0; i < 60; i++) {
//            	userId++;
	    	x="INSERT INTO CLUB_USER(CLUB_ID,USER_ID,STATUS,CREATE_TIME ) VALUES ("+clubId+","+userId+","+status+","+createTime+");\r\n";
	    	out.write(x.getBytes());
		}
	    out.close();
	}
	catch (Exception e) {
	    System.out.println(e.getMessage());
	}
}

}
