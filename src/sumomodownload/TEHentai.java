package sumomodownload;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TEHentai extends TDebug {
	URL url = null;
	HttpURLConnection urlcon = null;
	ArrayList<String> list = new ArrayList<String>();
	String strSaveFoldername = null;
	String strSavefilename = null;
	ArrayList<String> imageCollection = null;

	public TEHentai(String strURL, String saveFolderName, String name) {
		// TODO 自動生成されたコンストラクター・スタブ
		setDebugFlag(true);
		dPrintln("TEHentai()");
		setDebugFlag(true);

		dPrintln("download url: " + strURL);
		dPrintln("save foldername:" + saveFolderName);
		dPrintln("save filename:" + name);

		strSaveFoldername = saveFolderName;
		strSavefilename = name;

		setDebugFlag(false);

		try {
			url = new URL(strURL);
			urlcon = (HttpURLConnection) url.openConnection();
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
			dPrintln("レスポンスコード[" + urlcon.getResponseCode() + "] " + "レスポンスメッセージ[" + urlcon.getResponseMessage() + "]");
			dPrintln("\n---- ボディ ----");

			BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));

			setDebugFlag(true);
			while (true) {
				String line = br.readLine();
				if (null == line) {
					break;
				}
				dPrintln(line);
				list.add(line);
				dPrint("*");
			}
			setDebugFlag(false);
			br.close();
			urlcon.disconnect();

		} catch (MalformedURLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		setDebugFlag(false);

	}

	public void doDownload() {
		setDebugAll(false);

		setDebugFlag(true);
		dPrintln("TEHentai::doDownload()");
		setDebugFlag(true);

		getFirstPageURL();
		try {
			getSecondPageContent();
			for(int i =0; i<imageCollection.size(); i++){
				String filename = "file" + i+".jpg";
				TJpegFileDownloader obj = new TJpegFileDownloader(imageCollection.get(i), strSaveFoldername, filename);
				obj.dprogress();

			}
		} catch (MalformedURLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unused")
	private void getSecondPageContent() throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		final String strDownloadPrefix = "http://g.e-hentai.org/s/";

		String strFunction = "getSecoundPageContent()";
		setDebugFlag(true);
		dPrintln("getSecoundPageContent() is starting...");
		setDebugFlag(false);

		URL url2 = null;
		HttpURLConnection urlcon2 = null;

		for (int kk = 0; kk < list.size(); kk++) {
			String line = list.get(kk);

			setDebugFlag(true);
			dPrintln("getSecoundPageContent()->download filename: " + line);
			setDebugFlag(false);

			url2 = new URL(line);
			urlcon2 = (HttpURLConnection) url2.openConnection();
			urlcon2.setRequestMethod("GET");
			urlcon2.setInstanceFollowRedirects(false);
			urlcon2.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");
			urlcon2.connect();

			Map<String, List<String>> headers = urlcon.getHeaderFields();
			Iterator it = headers.keySet().iterator();

			setDebugFlag(false);
			while (it.hasNext()) {
				String key = (String) it.next();
				dPrintln("  " + key + ": " + headers.get(key));

			}
			dPrintln("レスポンスコード[" + urlcon2.getResponseCode() + "] " + "レスポンスメッセージ[" + urlcon2.getResponseMessage()
					+ "]");
			dPrintln("\n---- ボディ ----");
			ArrayList<String> list2page = new ArrayList<String>();

			setDebugFlag(false);
			dPrintln(strFunction + " is analyzing...	");
			setDebugFlag(false);

			BufferedReader br = new BufferedReader(new InputStreamReader(urlcon2.getInputStream()));
			String buffer;
			ArrayList<String> inputList = new ArrayList<String>();

			try {
				while (null != (buffer = br.readLine())) {
					inputList.add(buffer);
					// getURLList(strDownloadPrefix, strFunction, list2page,
					// buffer);
				}

				br.close();
				urlcon2.disconnect();

				setDebugFlag(true);
				dPrintln(" network is disconnet.)");
				setDebugFlag(false);

				setDebugFlag(false);
				dPrintln("analysing section.");
				dPrintln("inputList.size():" + inputList.size());
				setDebugFlag(false);

				ArrayList<String> imageList = new ArrayList<String>();
				for (int ix = 1; ix < inputList.size(); ix++) {
					String words[] = inputList.get(ix).split("[\"']");
					for (int ll = 0; ll < words.length; ll++) {
						String searchWord = ".jpg";
						int startPos = words[ll].indexOf(searchWord);
						int len = searchWord.length();
						int targetLength = words[ll].length();

						if (startPos + len == targetLength) {

							if (words[ll].indexOf("http://") >= 0 && words[ll].indexOf(".jpg") >= 0) {
								imageList.add(words[ll]);
							}
						}
					}
				}

				if (null == imageCollection) {
					imageCollection = imageList;
				} else {
					for (int q = 0; q < imageList.size(); q++) {
						imageCollection.add(imageList.get(q));
					}
				}

			} catch (IOException e) {
				if (null == br) {
					dPrintln("br is null.");
				}
			}

			getPictureURL(list2page);
		}

	}

	private void getPictureURL(ArrayList<String> list2page) {
		String strFunctionName = "getPictureURL()";

		setDebugFlag(false);
		dPrintln("download secution.");
		setDebugFlag(false);

		for (int bx = 0; bx < list2page.size(); bx++) {
			String fname = list2page.get(bx);
			String parts[] = fname.split("/");
			String saveFileName = parts[parts.length - 1];
			File f = new File(saveFileName);
			if (f.exists()) {
				dPrintln("ファイル名が重複しているのでダウンロードしません。");
			} else {

				setDebugFlag(true);
				dPrintln(strFunctionName + "filename:" + list2page.get(bx));
				setDebugFlag(false);

				TJpegFileDownloader obj = new TJpegFileDownloader(list2page.get(bx), strSaveFoldername, saveFileName);
			}
		}
	}

	private void getFirstPageURL() {
		ArrayList<String> pageList = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {

			String line = list.get(i);
			// dPrintln(i + " : " + line);
			String parts[] = line.split("[\"]");
			for (int kk = 0; kk < parts.length; kk++) {
				if (parts[kk].indexOf("http://g.e-hentai.org") >= 0 && parts[kk + 1].indexOf("img") >= 0 && kk > 0
						&& parts[kk - 1].indexOf("href") >= 0) {
					setDebugFlag(false);
					dPrintln("[" + kk + "]->" + parts[kk]);
					setDebugFlag(false);
					pageList.add(parts[kk]);
				}
			}
			// String parts[] = line.split("<");
			// for ( int k = 0; k<parts.length;k++){
			// dPrintln("["+parts[k]+"]");
			//// if( parts[k].indexOf(".jpg")>=0){
			//// dPrintln(parts[k]);
			//// }
			// }
		}
		setDebugFlag(false);
		list = pageList;
	}

}
