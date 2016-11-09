package jp.tnw.a18;

//◆弾幕のクラス◆//
public class NStgDanmaku extends NStgUnit {

	// 誰から撃つ◆true:自機◆false:敵
	boolean belongPlayer[];

	// 動くパタン処理用フラグ、タイプとアクション
	int flag[];
	int type[];
	int action[][];

	// 弾幕のステータス
	int damage[];

	// 生成からのタイマー
	int timerLife[];

	// リクェスト用
	double timerReq;
	int counterReq;

	// 初期化
	NStgDanmaku() {

		super(SYS.DANMAKU_LIMIT); // 弾数の上限
		komaImage = new KomaImage("Image/tama.png", 20, 10);
		belongPlayer = new boolean[MAX];
		flag = new int[MAX];
		type = new int[MAX];
		action = new int[MAX][10]; // アクションは10個まで
		damage = new int[MAX];
		timerLife = new int[MAX];

		for (int i = 0; i < MAX; i++) {

			belongPlayer[i] = false;
			flag[i] = 0;
			type[i] = 0;
			damage[i] = 1;
			timerReq = 0;
			counterReq = 0;
			timerLife[i] = 0;
			// アクション初期化 0 = 無効
			for (int j = 0; j < 10; j++) {
				action[i][j] = 0;
			}

		}

	}

	public void request(String danmakuType, int danmakuPattern, NStgUnit fromUnit, int index, double offsetX,
			double offsetY) {

		switch (danmakuType) {
		case "自機狙い弾いA":
			danmaku_JKN_A(fromUnit.dX[index] + offsetX, fromUnit.dY[index] + offsetY);
			break;
		case "花火A":
			danmaku_NOR_A(danmakuPattern, fromUnit.dX[index] + offsetX, fromUnit.dY[index] + offsetY);
			break;
		case "ビームA":
			// danmaku_BEAM_A(danmakuPattern, fromUnit, index, offsetX,
			// offsetY);
			break;

		// ここから演出用弾幕
		case "花火しっぽ":
			effect_NOR_A(fromUnit.dX[index], fromUnit.dY[index]);
			break;
		}

	}

	public void update() {

		// ここから主処理

		for (int i = 0; i < MAX; i++) {
			if (type[i] == 0 || flag[i] == 0) {
				continue;
			}

			// 様々な弾幕のアクション
			switch (type[i]) {
			case 1:// 花火A
				danmakuACT_NOR_A(i);
				break;
			case 1000:// 花火しっぽ
				effectACT_NOR_A(i);
				break;
			}

			timerLife[i]++;
		}

		// タイマー++
		timerReq++;
		counterReq++;

	}

	// リセット
	public void reset(int index) {

		super.reset(index);
		belongPlayer[index] = false;
		flag[index] = 0;
		type[index] = 0;
		damage[index] = 1;
		timerLife[index] = 0;
		for (int j = 0; j < 10; j++) {
			action[index][j] = 0;
		}
	}

	// AUTOリセット
	public void resetAuto(int index) {
		if (isOutBorder(this, index)) {
			reset(index);
		}
	}

	private void danmaku_JKN_A(double gX, double gY) {
//		int[][] KD = { //
//				//
//				{ 0, 999 }, //
//				{ 0, 0 + 30, 0 - 30, 999 }, //
//				{ 0, 0 + 15, 0 - 15, 0 + 30, 0 - 30, 0 + 45, 0 - 45, 999 } //
//		};//
//		// 自機狙い角度
//		double jikinerai;
//		jikinerai = Math.atan2((StgPlayer.dY + 24 - (y - 16)) , (StgPlayer.dX + 24 - (x - 16))) / Math.PI * 180 ;
//		int cnt = 0;
//
//		timerReq[0] = timerReq[0] - SYS.FRAME_TIME;
//		if (timerReq[0] < 0 && bulletCount < 10) {
//			timerReq[0] = 0.5;
//			for (int i = 0; i < UNIT_MAX; i++) {
//				if (flag[i] == 0 && bulletAction[i] == 0) {
//					imageIndex[i] = 235;
//					bulletType[i] = 2;
//					bulletAction[i] = 1;
//					dX[i] = x;
//					dY[i] = y;
//					spdX[i] = 0;
//					spdY[i] = 0;
//
//					isVisible[i] = true;
//					isHitable[i] = true;
//					flag[i] = 1;
//
//					angle[i] = KD[type][cnt] + jikinerai;
//					cnt++;
//					if (KD[type][cnt] == 999) {
//						break;
//					}
//
//
//				}
//			}
//			bulletCount++;
//		}
	}

	private void danmaku_BEAM_A(double gX, double gY) {

	}

	private void danmaku_NOR_A(int dmPattern, double gX, double gY) {
		int[][] motionAngle = { //
				//
				{ 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330, 360, 999 }, //
				{ 0, 90, 180, 270, 999 }, //
				{ 90, 90 + 30, 90 - 30, 999 }, //
				{ 0, 180, 999 } //
		};//
		int dmIndex = 0;

		for (int i = 0; i < MAX; i++) {
			if (type[i] != 0 || flag[i] != 0) {
				continue;
			}

			dX[i] = gX;
			dY[i] = gY;
			spdX[i] = -80;
			spdY[i] = -80;
			accX[i] = 50;
			accY[i] = 50;
			angle[i] = motionAngle[dmPattern][dmIndex] + timerReq * 2;

			isVisible[i] = true;
			imageIndex[i] = 37;

			isHitable[i] = true;
			hitCir[i] = 8;
			hitBoxW[i] = 16;
			hitBoxH[i] = 16;

			type[i] = 1;
			flag[i] = 1;

			dmIndex++;
			if (motionAngle[dmPattern][dmIndex] == 999) {
				break;
			}
		} // for(i) end

		VFX.request(gX + 8, gY + 8, 4);
	}// funtion end

	private void effect_NOR_A(double gX, double gY) {

		for (int i = 0; i < MAX; i++) {
			if (type[i] == 0 && flag[i] == 0) {
				dX[i] = gX;
				dY[i] = gY;
				spdX[i] = 0;
				spdY[i] = 0;
				accX[i] = 0;
				accY[i] = 0;

				isVisible[i] = true;
				opacity[i] = 0.5f;
				imageIndex[i] = 39;

				type[i] = 1000;
				flag[i] = 1000;
				break;
			}

		} // for(i) end

	}

	private void danmakuACT_NOR_A(int index) {
		if (timerReq % 4 == 0) {
			imageIndex[index] = imageIndex[index] < 37 || imageIndex[index] >= 40 ? 37 : imageIndex[index] + 1;
		}
		moveCir(index, (Math.random() - 0.5) * 15);
		resetAuto(index);
	}

	private void effectACT_NOR_A(int index) {
		opacity[index] -= 0.1f;
		if (opacity[index] <= 0.1f) {
			reset(index);
		}
	}

}
