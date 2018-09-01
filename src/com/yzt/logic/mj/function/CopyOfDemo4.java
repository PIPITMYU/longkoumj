package com.yzt.logic.mj.function;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.tomcat.jni.LibraryNotFoundError;

import com.yzt.logic.mj.domain.Player;
import com.yzt.logic.mj.domain.RoomResp;
import com.yzt.logic.util.MahjongUtils;


public class CopyOfDemo4 {

	public static void main(String[] args) {
		ArrayList<Integer>  arrayList = new ArrayList<Integer>();
		arrayList.add(1);
		arrayList.add(0);
		Integer same = 0;
		for (int i = 0; i < arrayList.size(); i++) {
			if(arrayList.get(i) == 1){
				same++;
			}
		}
		if(same==0){
			System.out.println("false");
		}else{
			System.out.println("true");
		}
	}
	
}
