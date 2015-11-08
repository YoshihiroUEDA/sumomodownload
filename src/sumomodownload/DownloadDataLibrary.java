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
			{"http://sumomo-ch.com/blog-entry-4198.html#more","白石茉莉奈 癒やしとエロさのヌード画像"},
			{	"http://sumomo-ch.com/blog-entry-3225.html", "白石茉莉奈 ムチムチなエロボディ画像"},
			{"http://sumomo-ch.com/blog-entry-2420.html","白石茉莉奈 AV女優画像 170枚"},
			{"http://sumomo-ch.com/blog-entry-1705.html","白石茉莉奈のAV作品画像 107枚"},
			{"http://sumomo-ch.com/blog-entry-1192.html","【元芸能人】白石茉莉奈AVデビュー 画像50枚【ママドル】"},
			{"http://sumomo-ch.com/blog-entry-4245.html","北野のぞみ 初の生中出しAVに出演"},
			{"http://sumomo-ch.com/blog-entry-1954.html#more","麻倉憂の可愛すぎるヌード画像 78枚"},
			{"http://sumomo-ch.com/blog-entry-1497.html#more","麻倉憂がカリビアンコムで復帰！画像62枚"},
			{"http://sumomo-ch.com/blog-entry-1174.html#more","【引退】麻倉憂を若妻に貰ったら…妄想エッチ画像70枚【見納め】"}

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