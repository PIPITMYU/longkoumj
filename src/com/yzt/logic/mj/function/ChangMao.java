package com.yzt.logic.mj.function;

/**
 * 长毛组合枚举
 * @author wsw_007
 *
 */
public enum ChangMao {
	ZHONG_FA_BAI("32-33-34");
//	YI_ZU,
//	JIU_ZU,
//	YAO_YI_ZU,
//	YAO_JIU_ZU,
//
//	YAO_ZHONG_FA,
//	YAO_FA_BAI,
//	YAO_ZHONG_BAI,
//	
//	YAO_NAN_XI,
//	YAO_NAN_BEI,
//	YAO_NAN_DONG,
//	YAO_XI_BEI,
//	YAO_XI_DONG,
//	YAO_DONG_BEI,
//	
//	DONG_NAN_XI,
//	DONG_NAN_BEI,
//	DONG_BEI_XI,
//	NAN_XI_BEI
	private String name;
	private ChangMao(String name){
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
}
