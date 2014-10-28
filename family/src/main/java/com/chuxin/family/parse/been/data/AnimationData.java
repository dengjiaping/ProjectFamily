package com.chuxin.family.parse.been.data;

public class AnimationData {
	
		private int animation_id;		// 动画ID[0-2] :          0：抽鞭子，1：弹脑壳
		private int effect;				   	//	动画效果    [0-9] 0：抽鞭子 2，3，4，5 回复抽鞭子
													  	//                              1：弹脑壳 6，7，8，9 回复弹脑壳
		private int shake_weight;	 	// 摇晃力度  [0-4].   暂时没用
		private boolean is_support;	// 对方是否支持该版本
		
		public boolean isIs_support() {
			return is_support;
		}
		public void setIs_support(boolean is_support) {
			this.is_support = is_support;
		}
		public int getAnimation_id() {
			return animation_id;
		}
		public void setAnimation_id(int animation_id) {
			this.animation_id = animation_id;
		}
		public int getEffect() {
			return effect;
		}
		public void setEffect(int effect) {
			this.effect = effect;
		}
		public int getShake_weight() {
			return shake_weight;
		}
		public void setShake_weight(int shake_weight) {
			this.shake_weight = shake_weight;
		}
}
