package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TPageAnalyzeEHentai extends TDebug {
	URL url = null;
	HttpURLConnection urlcon = null;
	ArrayList<String> list = new ArrayList<String>();
	ArrayList<String> imageList = new ArrayList<String>();

	public TPageAnalyzeEHentai(String string) throws IOException {
		// TODO 自動生成されたコンストラクター・スタブ
		
		setDebugFlag(false);
		
		url = new URL(string);
		urlcon = (HttpURLConnection) url.openConnection();
		urlcon.setRequestMethod("GET");
		urlcon.setInstanceFollowRedirects(false);
		urlcon.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");

		urlcon.connect();

		Map<String, List<String>> headers = urlcon.getHeaderFields();
		Iterator it = headers.keySet().iterator();

		while (it.hasNext()) {
			String key = (String) it.next();
			dPrintln("  " + key + ": " + headers.get(key));

		}
		// System.out.println(
		// "レスポンスコード[" + urlcon.getResponseCode() + "] " + "レスポンスメッセージ[" +
				// urlcon.getResponseMessage() + "]");
		// System.out.println("\n---- ボディ ----");
		if (urlcon.getResponseCode() != HttpURLConnection.HTTP_OK) {
			urlcon.disconnect();
			urlcon = null;
			url = null;
		}

	}

	public void doAnalyze() throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		setDebugFlag(true);
		dPrintln("TPageAnalyze::doAnalyze() start.");
		setDebugFlag(false);
		
		if (null == url)
			return;

		BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
		while (true) {
			String line = br.readLine();
			if (null == line) {
				break;
			}
			// System.out.println(line);
			setDebugFlag(true);
			
			dprogress();
//			dPrintln(line);
			if ( line.indexOf("href")>0){
				String []hrefLines ;
				hrefLines = line.split(" ");
				for (int ss = 0 ; ss < hrefLines.length; ss++){
					if ( hrefLines[ss].indexOf("href")>=0){
//						dPrintln(hrefLines[ss]);
						String [] elem = hrefLines[ss].split("\"");
						for ( int mm =0; mm<elem.length; mm++){
							if ( elem[mm].indexOf("http://") >=0 ){
								imageList.add(elem[mm]);
							}
						}
						
					}
				}
			}
			list.add(line);
			
			setDebugFlag(false);
		}
		br.close();
		dPrintln("store date show.");
		
		setDebugFlag(false);
		
		for (int i = 0; i < list.size(); i++) {
			String line = list.get(i);
			if (line.indexOf("http://") >= 0) {
				dPrintln(line);
				String elem[] = line.split("\"");
				for (int jj = 0; jj < elem.length; jj++) {
					if (elem[jj].indexOf("http") > 0) {
						dPrintln("[" + elem[jj] + "] is add.");
						imageList.add(elem[jj]);
					}
				}

			} else {
				list.remove(i);
			}
		}
		
		setDebugFlag(false);
		
		setDebugFlag(false);
		dPrintln("<<http>>");
		for (int jj = 0; jj < imageList.size(); jj++) {
			dPrintln(imageList.get(jj));
		}
		
		setDebugFlag(false);
		
	}

	public String getURL() {
		// TODO 自動生成されたメソッド・スタブ
		setDebugFlag(false);
		dPrintln("TPageAnalyzeEHentai()::getURL() ->element number : " + imageList.size());
		setDebugFlag(false);
		for(int i = 0; i < imageList.size(); i++) {
//			System.out.println(imageList.get(i));
			dPrintln(imageList.get(i));
		}
		return null;
	}

}
