package sumomodownload;

public class DownloadDataLibrary {
	String myitem[][] = {
//			{ "http://erogazo-chan.com/blog-entry-623.html", "初川みなみ２" },
//			{ "http://erogazo-chan.com/blog-entry-807.html", "坂口みほの" },
//			{ "http://erogazo-chan.com/blog-entry-767.html#more", "立花はるみ お嬢様の綺麗なヌード画像" },
//			{ "http://erogazo-chan.com/blog-entry-633.html#more", "神咲詩織 ムッチリ色白ボディのヌード画像" },
//			{ "http://erogazo-chan.com/blog-entry-341.html#more", "金沢文子 ぷるぷるエロボディ画像" },
//			{ "http://erogazo-chan.com/blog-entry-323.html#more", "古都ひかる（コトリッチ）のかわエロボディ画像" },
//			{ "http://sumomo-ch.com/blog-entry-3023.html#more", "加藤リナ やっぱり美しいヌード画像 110枚" },
//			{"http://sumomo-ch.com/blog-entry-1753.html","加藤リナのメガネお姉さんなエロ画像 37枚"}
//			,{"http://sumomo-ch.com/blog-entry-1404.html", "加藤リナ ちっぱい水着画像 70枚"}
//			,{"http://sumomo-ch.com/blog-entry-1355.html","加藤リナの引退が惜しまれるヌード画像 50枚"},
//			{"http://sumomo-ch.com/blog-entry-4198.html#more","白石茉莉奈 癒やしとエロさのヌード画像"},
//			{	"http://sumomo-ch.com/blog-entry-3225.html", "白石茉莉奈 ムチムチなエロボディ画像"},
//			{"http://sumomo-ch.com/blog-entry-2420.html","白石茉莉奈 AV女優画像 170枚"},
//			{"http://sumomo-ch.com/blog-entry-1705.html","白石茉莉奈のAV作品画像 107枚"},
//			{"http://sumomo-ch.com/blog-entry-1192.html","【元芸能人】白石茉莉奈AVデビュー 画像50枚【ママドル】"},
//			{"http://sumomo-ch.com/blog-entry-4245.html","北野のぞみ 初の生中出しAVに出演"},
//			{"http://sumomo-ch.com/blog-entry-1954.html#more","麻倉憂の可愛すぎるヌード画像 78枚"},
//			{"http://sumomo-ch.com/blog-entry-1497.html#more","麻倉憂がカリビアンコムで復帰！画像62枚"},
//			{"http://sumomo-ch.com/blog-entry-1174.html#more","【引退】麻倉憂を若妻に貰ったら…妄想エッチ画像70枚【見納め】"},
//			{"http://g.e-hentai.org/g/869533/56bf16d0ec/", "[守矢ギア] 姉憑き"}
//			{"http://g.e-hentai.org/g/358526/9a2104d69b/","[新堂エル] TSF物語"}
//			{"http://g.e-hentai.org/g/884956/e64405aedb/","[由浦カズヤ] きざし 第1-6話"},		//	このあたりからセッションエラー
//			{"http://g.e-hentai.org/g/884909/5c7ba6b81c/","[蛇光院三郎] 膣内射精プラトニック"},
//			{"http://g.e-hentai.org/g/884845/e43fadb0e8/","[麻森ゆき洋] 柔肌お姉さんと恥辱交尾"},
//			{"http://g.e-hentai.org/g/884842/dacebae937/","[樺島あきら] 妊娠×5"},
//			{"http://g.e-hentai.org/g/884834/718af5b505/","[胡桃屋ましみん] あねかのガチハメはーれむ"},
//			{"http://g.e-hentai.org/g/884658/70870fa036/","[横槍メンゴ] もっかいしよ？"},
//			{"http://g.e-hentai.org/g/880392/beb0c81144/","[高岡基文] 学園登桜記"},
//			{"http://g.e-hentai.org/g/878340/74e36b3532/","[野晒惺] 喫茶・人妻肉奴隷 第1-2話"},
//			{"http://g.e-hentai.org/g/884605/3c0f226c67/","[アンソロジー] 二次元コミックマガジン 状態変化でバッドエンド! Vol.2 [DL版]"},
//			{"http://g.e-hentai.org/g/884579/b41f28b04c/","アクションピザッツ DX 2016年01月号 [DL版]"},
//			{"http://g.e-hentai.org/g/883514/3878bfc5a9/","[峠比呂] Hマンガの女神様"},
			{"http://g.e-hentai.org/g/883011/1beff6ec19/","[沢田ふろぺ] ニセ婚！"}

	};

	public String lastUrl() {
		// TODO 自動生成されたメソッド・スタブ
		int len = myitem.length;
		return myitem[len - 1][0];
	}

	public String lastName() {
		// TODO 自動生成されたメソッド・スタブ
		return myitem[myitem.length - 1][1];
	}

	public int length(){
		return myitem.length;
	}

	public String getUrl( int i){
		return myitem[i][0];
	}

	public String getName( int i ){
		return myitem[i][1]	;
	}

	// public String getName() {
	//
	// }

}

class Item {
	String urlName;
	String name;

}