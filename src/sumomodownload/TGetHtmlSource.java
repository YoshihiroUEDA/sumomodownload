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

public class TGetHtmlSource {
	ArrayList<String> list = null;
	ArrayList<String> imageList = null;

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

		while (it.hasNext()) {
			String key = (String) it.next();
			System.out.println("  " + key + ": " + headers.get(key));

		}
		System.out.println(
				"レスポンスコード[" + urlcon.getResponseCode() + "] " + "レスポンスメッセージ[" + urlcon.getResponseMessage() + "]");
		System.out.println("\n---- ボディ ----");
		BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
		while (true) {
			String line = br.readLine();
			if (null == line) {
				break;
			}
			System.out.println(line);
			list.add(line);
		}
		br.close();
		urlcon.disconnect();

		makeURLList();
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
