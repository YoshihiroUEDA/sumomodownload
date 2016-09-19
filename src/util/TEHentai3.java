package util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TEHentai3 extends TDebug {
	private static final String PREFIX_E_HENTAI_URL = "http://g.e-hentai.org/s/";
	private static final String PREFIX_E_HENTAI_URL_PIC = "http://g.e-hentai.org/r/";
	private static final String PREFIX_HTTP = "http://";
	String _saveFolder, _strURL;

	public TEHentai3(String line, String saveFolderName) {
		// TODO 自動生成されたコンストラクター・スタブ
		coutln("line=" + line);
		coutln("saveFolderName = " + saveFolderName);
		//
		_saveFolder = saveFolderName;
		_strURL = line;
		//
		File f = new File(saveFolderName);
		if (!f.exists()) {
			f.mkdir();

		} else {
			String strRename = saveFolderName + ".bak";
			File fren = new File(strRename);
			if (fren.exists()) {
				coutln("folder[" + strRename + "] is found.");
				deleteFolder(strRename);
				f.renameTo(fren);
				f.mkdir();
			}
		}

		ArrayList<String> items = analyzeHTML(line);
		for (int j = 0; j < items.size(); j++) {
			ArrayList<String> urls = getItemsFromPage(items.get(j));
			for (int k = 0; k < urls.size(); k++) {
				pageDownload(urls.get(k), saveFolderName);
			}
		}
	}

	/**
	 * 最終的な1ページから画像を取得する
	 *
	 * @param urlString
	 * @param saveFolderName
	 */
	private void pageDownload(String urlString, String saveFolderName) {
		// 出力フォルダンチェック
		File f = new File(saveFolderName);
		if (!f.exists()) {
			coutln("フォルダが存在しない。ダウンロードをスキップする");
			return;
		}
		// ページのダウンロード処理
		URL url = null;
		HttpURLConnection urlcon = null;
		ArrayList<String> sourceData = new ArrayList<>();
		try {
			url = new URL(urlString);
			urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setRequestMethod("GET");
			urlcon.setInstanceFollowRedirects(false);
			urlcon.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");
			urlcon.connect();
			Map<String, List<String>> headers = urlcon.getHeaderFields();
			Iterator it = headers.keySet().iterator();

			// while (it.hasNext()) {
			// String key = (String) it.next();
			// dPrintln(" " + key + ": " + headers.get(key));
			// }

			BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));

			while (true) {
				String line1 = br.readLine();
				if (null == line1) {
					break;
				}
				sourceData.add(line1);
			}
			br.close();
			urlcon.disconnect();
		} catch (MalformedURLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		ArrayList<String> imgList = new ArrayList<>();

		for (int i = 0; i < sourceData.size(); i++) {
			String items[] = sourceData.get(i).split("\"");
			for (int j = 0; j < items.length; j++) {
//				coutln("**->"+items[j]);
				if (items[j].indexOf(PREFIX_HTTP) >= 0 && items[j].indexOf(".jpg") >= 0 && items[j].indexOf("http://g.e-hentai.org/?")<0) {
					String pics[] = items[j].split("'");
					for (int k = 0; k < pics.length; k++) {
						if ( pics[k].indexOf(PREFIX_HTTP)>=0){
							imgList.add(pics[k]);
						}
					}
				}
			}
		}


		// 画像のダウンロード処理
		for ( int i=0; i<imgList.size(); i++){
			if ( imgList.get(i).indexOf("http://g.e-hentai.org/?")>=0)	continue;
			if(imgList.get(i).indexOf("http://g.e-hentai.org/r")>=0)continue;

			try {
				String words[] = imgList.get(i).split("/");
				String filename = words[words.length - 1];
				String saveFilename = _saveFolder
						+ System.getProperty("file.separator") + filename;
				coutln("save filename:" + saveFilename+", src="+imgList.get(i));

				url = new URL(imgList.get(i));
				urlcon = (HttpURLConnection) url.openConnection();
				urlcon = (HttpURLConnection) url.openConnection();
				urlcon.setRequestMethod("POST");
				urlcon.setAllowUserInteraction(false);
				urlcon.setInstanceFollowRedirects(true);
				urlcon.setRequestMethod("GET");
				urlcon.connect();

				int httpStatusCode = urlcon.getResponseCode();
				if (HttpURLConnection.HTTP_OK != httpStatusCode) {
					System.out.println("session error.");
					System.exit(-1);
				}
				f= new File(saveFilename);
				DataInputStream dis = new DataInputStream(urlcon.getInputStream());
				DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));

				byte[] b = new byte[4096];
				int readByte = 0;

				while (-1 != (readByte = dis.read(b))) {
					dos.write(b, 0, readByte);
				}

				dos.flush();

				dis.close();
				dis = null;
				dos.close();
				dos = null;
				urlcon.disconnect();
				urlcon = null;

			} catch (MalformedURLException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}


		}
	}

	/**
	 * 1ページの中から画像のページへのリンクを取り出す。
	 *
	 * @param urlString
	 * @return
	 */
	private ArrayList<String> getItemsFromPage(String urlString) {
		// TODO 自動生成されたメソッド・スタブ
		URL url = null;
		HttpURLConnection urlcon = null;
		ArrayList<String> urlList = new ArrayList<>();
		ArrayList<String> sourceData = new ArrayList<>();
		try {
			url = new URL(urlString);
			urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setRequestMethod("GET");
			urlcon.setInstanceFollowRedirects(false);
			urlcon.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");
			urlcon.connect();
			Map<String, List<String>> headers = urlcon.getHeaderFields();
			Iterator it = headers.keySet().iterator();

			// while (it.hasNext()) {
			// String key = (String) it.next();
			// dPrintln(" " + key + ": " + headers.get(key));
			// }

			BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));

			while (true) {
				String line1 = br.readLine();
				if (null == line1) {
					break;
				}
				sourceData.add(line1);
			}
			br.close();
			urlcon.disconnect();

		} catch (MalformedURLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		for (int i = 0; i < sourceData.size(); i++) {
			String line = sourceData.get(i);
			String items[] = line.split("\"");
			for (int j = 0; j < items.length; j++) {
				if (items[j].indexOf(PREFIX_HTTP) == 0 && items[j].indexOf(PREFIX_E_HENTAI_URL) >= 0) {
					urlList.add(items[j]);
				}
			}
		}
		return urlList;
	}

	/**
	 * 複数ページにまたがるときのページリストを取得する
	 *
	 * @param line
	 * @return
	 */
	private ArrayList<String> analyzeHTML(String line) {
		// TODO 自動生成されたメソッド・スタブ
		coutln("analyzeHTML( " + line + ") called.");
		URL url = null;
		HttpURLConnection urlcon = null;
		ArrayList<String> list = new ArrayList<String>();

		try {
			url = new URL(line);
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

			BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));

			while (true) {
				String line1 = br.readLine();
				if (null == line1) {
					break;
				}
				list.add(line1);
				dPrint("*");
			}
			setDebugFlag(false);
			br.close();
			urlcon.disconnect();

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		ArrayList<String> strDownloadURL = new ArrayList<>();
		strDownloadURL.add(line);

		for (int i = 0; i < list.size(); i++) {
			String buff = list.get(i);
			String strItems[] = buff.split("<.td");
			for (int j = 1; j < strItems.length; j++) {
				int pos = strItems[j].indexOf("onclick");
				if (pos >= 0 && pos <= 10) {
					String s2[] = strItems[j].split("\"");
					for (int k = 0; k < s2.length; k++) {
						coutln("->" + s2[k]);
						if (s2[k].indexOf(PREFIX_HTTP) >= 0) {
							strDownloadURL.add(s2[k]);
						}
					}
				}
			}
		}
		// 重複チェック
		for (int i = 0; i < strDownloadURL.size(); i++) {
			coutln("strDownloadURL.get(" + i + ")= " + strDownloadURL.get(i));
		}
		String item;
		for (int i = 0; i < strDownloadURL.size(); i++) {
			item = strDownloadURL.get(i);
			for (int j = i + 1; j < strDownloadURL.size(); j++) {
				String comp = strDownloadURL.get(j);
				if (item.equals(comp)) {
					strDownloadURL.remove(j);
					j--;
					coutln(j + ":" + comp + " is delete.");
				}
			}
		}
		for (int i = 0; i < strDownloadURL.size(); i++) {
			coutln("strDownloadURL.get(" + i + ")= " + strDownloadURL.get(i));
		}
		return strDownloadURL;
	}

	/**
	 * 不要なフォルダを削除する
	 *
	 * @param strRename
	 */
	private void deleteFolder(String strRename) {
		// TODO 自動生成されたメソッド・スタブ
		coutln("deleteFolder(" + strRename + ") is called.");
		File f = new File(strRename);
		if (f.exists()) {
			if (f.isDirectory()) {
				coutln("folder found.");
				String fs[] = f.list();
				coutln("fs.length=" + fs.length);

				for (int i = 0; i < fs.length; i++) {
					deleteFolder(fs[i]);
				}
			}
		} else {

		}
		coutln("file delete completed.");

	}

}
