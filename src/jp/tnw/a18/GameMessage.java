package jp.tnw.a18;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

//◆会話システム◆//
public class GameMessage {

	private Font f = new Font("Meiryo", Font.BOLD, 20);
	private Font bf = new Font("Meiryo", Font.BOLD, 60);

	boolean pause;
	boolean fastPlay;
	boolean pauseWhenFastplay;

	private final int M_MAX = 40;
	private int dataCharCnt = 0;
	private int dataLineCnt = 0;
	private int msgboxCharCnt = 0;
	private int msgboxLineCntTop = 0; // 0-1
	private int msgboxLineCntBot = 0; // 0-1
	private Color colorBot;
	private Color colorTop;
	private Color color1, color2, color3, color4, color5, color6, color7, color8, color9;
	private int usingWitchMsgbox = 0; // 0:bot 1:top

	int textTimer = 0;
	int startWait = 0;

	private char msgboxTop[][] = new char[2][40];
	private char msgboxBot[][] = new char[2][40];

	String requesting[];

	GameMessage() {

		//requesting = text01; // dbing
		color1 = Color.white;
		color2 = Color.gray;
		color3 = Color.black;
		color4 = Color.red;
		color5 = Color.green;
		color6 = Color.blue;
		color7 = new Color(160, 160, 160);
		color8 = new Color(160, 160, 160);
		color9 = new Color(160, 160, 160);
		colorBot = color1;
		colorTop = color1;
		pause = false;
		clear();

	}

	public void draw(Graphics2D g) {
		g.setFont(f);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		g.setColor(colorBot);
		g.drawString(String.valueOf(msgboxBot[0]), (int) (214), (int) (480));
		g.drawString(String.valueOf(msgboxBot[1]), (int) (214), (int) (510));

		g.setColor(colorTop);
		g.drawString(String.valueOf(msgboxTop[0]), (int) (50), (int) (40));
		g.drawString(String.valueOf(msgboxTop[1]), (int) (50), (int) (70));
		
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		// g.drawString(String.valueOf(pauseWhenFastplay), 300, 100);
	}

	public void request(int msg) {

		switch (msg){
		case 0:
			requesting = text01;
			startWait = 60;
			break;
		}
		

	}

	public void update() {

		// セリフリクエストあるとき
		if (requesting != null && startWait == 0) {

			textTimer++;

			// PAUSE処理（マウスボタンを離す と 早送りではない場合）
			if (pause && (Input.M_LCR || Input.K_ZR) == true && !pauseWhenFastplay) {
				// クリアするかしないかの判断（中途一旦PAUSE場合あるいは行が終わり）
				if (dataCharCnt == 0 || (requesting[dataLineCnt].charAt(dataCharCnt - 1) != 'P'
						&& requesting[dataLineCnt].charAt(dataCharCnt - 2) != '@')) {
					if (usingWitchMsgbox == 0) {
						clearBot();
					} else if (usingWitchMsgbox == 1) {
						clearTop();
					}
				}

				// 使ってない会話ボックスの文字を灰色に
				colorBot = usingWitchMsgbox == 0 ? color1 : color2;
				colorTop = usingWitchMsgbox == 1 ? color1 : color2;

				pause = false;
			}

			// 早送り処理
			if (Input.M_LC || Input.K_Z) {
				fastPlay = true;
			} else {
				fastPlay = false;
			}
			if (pauseWhenFastplay && (Input.M_LCR || Input.K_ZR)) {
				pauseWhenFastplay = false;
			}

			// マウスボタン状態リセットdbing
			Input.M_LCR = false;
			Input.K_ZR = false;

			// データの解析と会話ボックスに渡す
			if (textTimer % (fastPlay ? 1 : 3) == 0) {

				// PAUSEの場合なにもしない
				if (pause) {
					return;
				}

				char spCode = requesting[dataLineCnt].charAt(dataCharCnt);
				char spCode2 = requesting[dataLineCnt].charAt(dataCharCnt + 1);

				if (spCode == '@') {

					specialCode(spCode2);

				} else {

					// 上あるいは下の会話ボックスを使う
					if (usingWitchMsgbox == 0) {
						// データから何文字目をGET
						msgboxBot[msgboxLineCntBot][msgboxCharCnt] = requesting[dataLineCnt].charAt(dataCharCnt);
					} else if (usingWitchMsgbox == 1) {
						// データから何文字目をGET
						msgboxTop[msgboxLineCntTop][msgboxCharCnt] = requesting[dataLineCnt].charAt(dataCharCnt);
					}

					// データと会話ボックスカンター進む
					dataCharCnt++;
					msgboxCharCnt++;

				}
			} // if timer end
		} // if req end
		
		startWait = startWait > 0 ? startWait - 1 : 0;

	}

	private void specialCode(char chr) {

		// 行の終わり
		switch (chr) {
		case 'E':
			dataLineCnt++;
			dataCharCnt = 0;
			msgboxCharCnt = 0;
			// どっちの会話ボックスを使ってるか
			if (usingWitchMsgbox == 0) {
				// 会話ボックの何行目を判断と処理
				msgboxLineCntBot++;
				if (msgboxLineCntBot == 2) {
					msgboxLineCntBot = 0;
					// もし次の行の表示は他の会話ボックスに変換するとPAUSEしない
					if (dataLineCnt < requesting.length - 1) {
						pause = true;
						if (requesting[dataLineCnt + 1].charAt(0) == '@'
								&& requesting[dataLineCnt + 1].charAt(1) == 'U') {
							pause = false;
						}
					} else {
						pause = true;
					}
				}
				// 早送りstop
				if (fastPlay) {
					pauseWhenFastplay = true;
				}

			} else if (usingWitchMsgbox == 1) {
				// 会話ボックの何行目を判断と処理
				msgboxLineCntTop++;
				if (msgboxLineCntTop == 2) {
					msgboxLineCntTop = 0;
					// もし次の行の表示は他の会話ボックスに変換するとPAUSEしない
					if (dataLineCnt < requesting.length - 1) {
						pause = true;
						if (requesting[dataLineCnt + 1].charAt(0) == '@'
								&& requesting[dataLineCnt + 1].charAt(1) == 'U') {
							pause = false;
						}
					} else {
						pause = true;
					}
				}
				// 早送りstop
				if (fastPlay) {
					pauseWhenFastplay = true;
				}
			}
			break;

		// クリックしないと以降の文字を表示しない(
		case 'P':
			if (fastPlay) {
				pauseWhenFastplay = true;
			}
			pause = true;
			dataCharCnt += 2;
			break;

		// この会話シーンはここまで
		case 'B':
			if (fastPlay) {
				pauseWhenFastplay = true;
			}
			pause = true;
			requesting = null;
			dataCharCnt = 0;
			dataLineCnt = 0;
			msgboxCharCnt = 0;
			msgboxLineCntTop = 0;
			msgboxLineCntBot = 0;
			usingWitchMsgbox = 0;
			clear();
			break;

		// どっちの会話ボックスを使う 0:bot 1:top
		case 'U':
			char spCode3 = requesting[dataLineCnt].charAt(dataCharCnt + 2);
			usingWitchMsgbox = Integer.parseInt(String.valueOf(spCode3));
			if (usingWitchMsgbox == 0) {
				msgboxLineCntBot = 0;
			} else if (usingWitchMsgbox == 1) {
				msgboxLineCntTop = 0;
			}
			dataCharCnt += 3;
			pause = true;
			break;

		}

	}

	// 表示エリア初期化
	private void clear() {
		clearTop();
		clearBot();
	}

	private void clearTop() {
		for (int i = 0; i < M_MAX; i++) {
			msgboxTop[0][i] = ' ';
			msgboxTop[1][i] = ' ';
		}
	}

	private void clearBot() {
		for (int i = 0; i < M_MAX; i++) {
			msgboxBot[0][i] = ' ';
			msgboxBot[1][i] = ' ';
		}
	}
	
    private int getStringLength(String s)
    {
        int length = 0;
        for(int i = 0; i < s.length(); i++)
        {
            int ascii = Character.codePointAt(s, i);
			if (ascii >= 0 && ascii <= 255)
                length++;
            else
                length += 2;
        }
        return length;
        
    }

    private String text01[] = {

			"ふん。アタシのマネなんて十年早いわよ！@E", // takeo勝ち
			"@U1あらあら、@P随分弱いわねぇ～@P偽者さん。@E", "@U1このブス！　アタシはもっとプリチ～だわ！@E", "@U0なかなかやるじゃない……。@E", // 負け
			"同じキャラだもん、@Pどっちかは負けるでしょうが！@E", "@U0……アタシが偽者なの？@E", "@U1坊や、おうちで遊んでなさい。@E", // Kachan勝ち
			"弱いわねぇ～。仕方ないか、アタシが相手だもんね。@E", "ふん。このダンゴ虫め！@E", "な、なんでこんなダンゴみたいなヤツに……。@E", // 負け
			"可愛い顔して強いわね……。@E", "今度はアタシが勝つ番よ！@E", "@U0このガラクタめ！　アタシを誰だと思っているの？@E", // Kutara勝ち
			"@U0困るのよねぇ～時間の無駄だわ。@E", "@U0こんなロボット、アタシの敵じゃないわ。@E", "……イタタタ……卑怯よ、あんた鉄製でしょ！@E", // 負け
			"アタシにも遠距離攻撃が欲しいわぁ～！@E", "こんなジジィに負けるとは……アタシって……。@E", "誰あんた？　木みたいなヤツがアタシに勝てると思う？@P@B" // Ton-Gari勝ち

	};

}
