package sumomodownload;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TGetHtmlSource extends TDebug{
	ArrayList<String> list = null;
	ArrayList<String> imageList = null;
boolean ehentai=false;

	public TGetHtmlSource(String string) throws IOException {
		// TODO 自動生成されたコンストラクター・スタブ
		System.out.println("TGetHtmlSource() constructer start.");
		System.out.println("URL: " + string);
		list = new ArrayList<String>();
		URL url = null;
		url = new URL(string);
		HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
		urlcon.setRequestMethod("GET");
		urlcon.setInstanceFollowRedirects(false);
		urlcon.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");

		urlcon.connect();

		Map<String, List<String>> headers = urlcon.getHeaderFields();
		Iterator it = headers.keySet().iterator();

		setDebugFlag(false);
		while (it.hasNext()) {
			String key = (String) it.next();
			dPrintln("  " + key + ": " + headers.get(key));

		}
		dPrintln(
				"レスポンスコード[" + urlcon.getResponseCode() + "] " + "レスポンスメッセージ[" + urlcon.getResponseMessage() + "]");
		dPrintln("\n---- ボディ ----");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
		while (true) {
			String line = br.readLine();
			if (null == line) {
				break;
			}
			dPrintln(line);
			list.add(line);
		}
		br.close();
		urlcon.disconnect();
		if (string.indexOf("e-hentai.org") > 0) {
			dPrintln("e-hentai.org site.");
			makeURLListForHtmlList();	//	ここで、詳細ページのリスト作成が終わる。
			ehentai=true;
			
		} else {
			makeURLList();
		}
	}

	private void makeURLListForHtmlList() {
		// TODO 自動生成されたメソッド・スタブ
		setDebugFlag(false);
		for (int i = 0; i < list.size(); i++) {
			String line = list.get(i);
			String elem[] = line.split("\"");
			// System.out.println("element number: " + elem.length);
			for (int kk = 0; kk < elem.length; kk++) {
				// if (kk >0 && elem[kk].indexOf("http://g.e-hentai.org/g/")>=0
				// && elem[kk-1].indexOf("a href=")>=0){
				// System.out.println(elem[kk]);
				// }
				if (elem[kk].indexOf("http://g.e-hentai.org/s") >= 0 && elem[kk].indexOf("margin") < 0
						&& elem[kk].indexOf("http://ehgt.org") < 0) {
					// System.out.println(elem[kk]);
					try {
						TPageAnalyzeEHentai obj = new TPageAnalyzeEHentai(elem[kk]);
						obj.doAnalyze();
						String lineUrl = obj.getURL();
						if (null != lineUrl) {
							imageList.add(lineUrl);
						}
					} catch (IOException e) {
						// TODO 自動生成された catch ブロック
						e.printStackTrace();
						System.out.println("ネットワークのエラー");
						System.exit(-1);
					}

				}
			}
		}

	}
	public boolean isEHentai(){
		return ehentai;
	}
	
	public void getList() {

	}

	public ArrayList<String> getURLs() {
		return imageList;
	}

	private void makeURLList() {
		int i;

		String strLinkKeyword = "http://";
		String strImageKeyword = "img";
		String strExtKeyword = ".jpg";

		imageList = new ArrayList<String>();

		for (i = 0; i < list.size(); i++) {
			String line = list.get(i);
			String words[] = line.split("\"", 0);
			for (int j = 0; j < words.length; j++) {
				if (words[j].indexOf("img") >= 0 && words[j].indexOf(strLinkKeyword) >= 0
						&& words[j].indexOf(strExtKeyword) >= 0) {
					imageList.add(words[j]);
				}
			}
		}
	}
}
