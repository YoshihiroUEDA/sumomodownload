package sumomodownload;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TEHentai2 extends TDebug {
	/**
	 * 
	 * @param URLAddress ダウンロードのURL
	 * @param SaveFolder 保存場所
	 */
	public TEHentai2(String URLAddress, String SaveFolder) {
		//	ダウンロード処理
		TEHentaiDownloaderForHTML obj = new TEHentaiDownloaderForHTML(URLAddress, SaveFolder);
		// obj.downloadFile();

	}
}

class TEHentaiDownloaderForJpeg extends TDebug {

}

/**
 * @author 吉祥
 *
 */
class TEHentaiDownloaderForHTML extends TDebug {
	private static final String TD_CLASS_PTDD_LT = "<td class=\"ptdd\">&lt;";
	private static final String URL_IDENTIFIRE_STRING = "http://";
	private static final String URL_SUBPAGE_STRING = "http://g.e-hentai.org/s";
	private static final String HTTP_PREFIX = "http://";
	private static final String JPEG_SUFFIX = ".jpg";
	ArrayList<String> _urlList = null;
	ArrayList<String> listSumneil = new ArrayList<String>();

	/**
	 * コンストラクタ
	 * 一覧の格納されたページと保存場所を指定される
	 * 
	 * ※複数ページ分割に対応していない（２０１５．１２．０７）
	 * 
	 * @param targetURL
	 * @param saveFolder
	 */
	public TEHentaiDownloaderForHTML(String targetURL, String saveFolder) {
		ArrayList<String> list = new ArrayList<>();
		coutln("TEHentaiDownloaderForHTML() constructor start...");
		for (int jj = 0; jj < listSumneil.size(); jj++) {
			if (listSumneil.get(jj).equals(targetURL)) {
				return;
			}
		}
		// 保存先フォルダのチェック
		checkFolderExist(saveFolder);
		//
		URL url = null;
		HttpURLConnection urlcon = null;

		// 一括して接続処理を行う
		try {
			urlcon = openConnection(targetURL);

			BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
			while (true) {
				String line = br.readLine();
				// coutln("->>"+line);
				if (null == line) {
					break;
				}
				// if ( line.indexOf("<div class=\"gm\"")>=0){
				// coutln("target string found.");
				// }
				list.add(line);
			}

			// 一括して切断処理を行う
			closeConnection(urlcon, br);
		} catch (Exception e) {
			System.out.println("異常終了");
			System.exit(-1);
		}
		coutln("ダウンロードURLの切り出し処理を行います。");
		ArrayList<String> urllist = new ArrayList<String>();
		pickupURLString(list, urllist);
		for (int x = 0; x < listSumneil.size(); x++) {
			TEHentaiDownloaderForHTML obj = new TEHentaiDownloaderForHTML(listSumneil.get(x), saveFolder);
		}
		//	2枚目以降のページの取得(ただし、同じページのリンクは除外
		for (int kx=0; kx<list.size(); kx++){

			if( list.get(kx).indexOf(TD_CLASS_PTDD_LT)>=0	){
				String [] words = list.get(kx)	.split("</td>");
				for (int kxx=0; kxx<words.length; kxx++){
					if ( words[kxx].indexOf(TD_CLASS_PTDD_LT)>=0){
						coutln("target found<"+words[kxx].indexOf(TD_CLASS_PTDD_LT)+">:" + words[kxx]	);
					}
				}
			}
		}

		setDebugFlag(true);
		for (int k = 0; k < urllist.size(); k++) {
			dPrintln(k + ":" + urllist.get(k));

		}
		setDebugFlag(false);
		_urlList = urllist;

		for (int xx = 0; xx < _urlList.size(); xx++) {
			try {
				downloadSubpage(urllist.get(xx), targetURL, saveFolder);
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

	}
	/**
	 * サブページのダウンロード
	 * 
	 * @param pageURL			このページの中からダウンロードを行う
	 * @param reffereFromURL	リファラの指定
	 * @param SaveFolder		保存場所
	 * @throws IOException
	 */
	private void downloadSubpage(String pageURL, String reffereFromURL, String SaveFolder) throws IOException {
		coutln("downloadSubpage() start.");

		URL url = new URL(pageURL);
		HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
		urlcon.setRequestMethod("GET");
		urlcon.setInstanceFollowRedirects(false);
		urlcon.connect();
		BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
		String line;
		while (null != (line = br.readLine())) {
			String words[] = line.split("[\"']");
			for (int xx = 0; xx < words.length; xx++) {
				if (words[xx].indexOf(HTTP_PREFIX) >= 0 && words[xx].indexOf(JPEG_SUFFIX) >= 0) {
					coutln("downloadSubpage():" + xx + words[xx]);
					downloadFile(words[xx], pageURL, SaveFolder);
				}
			}
		}
		br.close();
		br = null;
		urlcon.disconnect();
		urlcon = null;
	}

	/**
	 * ファイルのダウンロード処理
	 * 
	 * @author 吉祥
	 * @param saveFolder	保存フォルダ
	 * @param pageURL		画像ページのURL；参照元のURL（リファラとして設定）
	 * @param imageURL		画像ファイルのアドレス
	 * @throws IOException
	 *
	 *
	 */
	public void downloadFile(String imageURL, String pageURL, String saveFolder) throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		
		URL url = new URL(imageURL);
		HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
		urlcon.setRequestMethod("GET");
		urlcon.setInstanceFollowRedirects(true);
		urlcon.setRequestProperty("Reffere", pageURL);
		urlcon.setAllowUserInteraction(false);
		urlcon.connect();

		int httpStatusCode = urlcon.getResponseCode();
		if (HttpURLConnection.HTTP_OK != httpStatusCode) {
			coutln("connect error.");
			return;
		}
		File file;
		String[] words = imageURL.split("/");
		String outputFilename = saveFolder + "\\" + words[words.length - 1];
		file = new File(outputFilename);
		if (file.exists()) {
			// リネーム処理
			return;
		}
		coutln("output filename:" + outputFilename);

		try {
			DataInputStream dis = new DataInputStream(urlcon.getInputStream());
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			byte[] buff = new byte[4096];
			int readByte = 0, totalSize = 0;
			while (-1 != (readByte = dis.read(buff))) {
				dos.write(buff, 0, readByte);
				totalSize += readByte;
			}
			coutln(words[words.length - 1] + " " + totalSize + " byte write.");
			if (0 == totalSize) {
				file.delete();
			}
			dos.flush();
			dos.close();
			dos = null;
			dis.close();
			dis = null;
			urlcon.disconnect();
			urlcon = null;

		} catch (FileNotFoundException e) {
			coutln("file cannot created.");
		}

	}
	/**
	 * サブページのURLを抽出してリストを作成する
	 * 
	 * @param list		元のHTML文が格納；解析対象
	 * @param urllist	サブページとして発見されたURL
	 */
	private void pickupURLString(ArrayList<String> list, ArrayList<String> urllist) {
		for (int i = 0; i < list.size(); i++) {
			String line = list.get(i);
			if (line.indexOf("<div class=\"gm\"") >= 0) {
				coutln("target string processing.");
			}
			String[] words = line.split("[\"']");
			for (int j = 0; j < words.length; j++) {
				// coutln( "->>"+words[j]+"<<");

				if (words[j].indexOf(URL_SUBPAGE_STRING) >= 0) {
					// coutln(j + ":" + words[j]);
					urllist.add(words[j]);

				}
//				if (words[j].indexOf("http://g.e-hentai.org/g/") >= 0) {
////					for ( int xx =0 ; xx<words.length; xx++){
////						if ( words[xx].equals(words[j]))break;
////					}
//					coutln("->>" + words[j] + "<<");
//					listSumneil.add(words[j]);
//				}
			}
		}
	}
	/**
	 * 
	 * ？？
	 * 
	 * 
	 * @param targetURL
	 * @param reffere
	 * @param saveFolder
	 * @throws MalformedURLException
	 * @throws ProtocolException
	 * @throws IOException
	 */
	public TEHentaiDownloaderForHTML(String targetURL, String reffere, String saveFolder)
			throws MalformedURLException, ProtocolException, IOException {
		// 保存先フォルダのチェック
		checkFolderExist(saveFolder);
		//
		URL url = null;
		HttpURLConnection urlcon = null;

		// 一括して接続処理を行う
		urlcon = openConnection(targetURL, reffere);

		BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
		while (true) {
			String line = br.readLine();
			if (null == line) {
				break;
			}
			dPrintln(line);
		}

		// 一括して切断処理を行う
		closeConnection(urlcon, br);

	}
	/**
	 * フォルダの有無をチェックし、なければ作成する
	 * 
	 * @param saveFolder
	 */
	private void checkFolderExist(String saveFolder) {
		File f = new File(saveFolder);
		if (!f.exists()) {
			if (f.mkdir()) {
				dPrintln("フォルダの作成に失敗しました。");
				System.exit(-1);
			}
		}
	}
	/**
	 * コネクションを閉じる
	 * 
	 * @param urlcon
	 * @param br
	 * @throws IOException
	 */
	private void closeConnection(HttpURLConnection urlcon, BufferedReader br) throws IOException {
		br.close();
		urlcon.disconnect();
	}
	/**
	 * コネクションを張る
	 * 
	 * @param targetURL
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ProtocolException
	 */
	private HttpURLConnection openConnection(String targetURL)
			throws MalformedURLException, IOException, ProtocolException {
		URL url;
		HttpURLConnection urlcon;
		urlcon = openConnectionPre(targetURL);
		urlcon.setRequestMethod("GET");
		urlcon.setAllowUserInteraction(false);
		urlcon.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");
		urlcon.connect();

		openConnectionParameterRead(urlcon);
		return urlcon;
	}
	/**
	 * コネクションを張る
	 * 
	 * @param targetURL
	 * @param reffere
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws ProtocolException
	 */
	private HttpURLConnection openConnection(String targetURL, String reffere)
			throws MalformedURLException, IOException, ProtocolException {
		URL url;
		HttpURLConnection urlcon;
		urlcon = openConnectionPre(targetURL);
		urlcon.setRequestMethod("GET");
		urlcon.setAllowUserInteraction(false);
		urlcon.setRequestProperty("Accept-Language", "ja;q=0.7,en;q=0.3");

		// Reffereの設定

		//

		urlcon.connect();

		openConnectionParameterRead(urlcon);
		return urlcon;
	}
	/**
	 * 接続したこhttpコネクションのパラメータを読み取る
	 * @param urlcon
	 */
	private void openConnectionParameterRead(HttpURLConnection urlcon) {
		Map<String, List<String>> headers = urlcon.getHeaderFields();
		Iterator it = headers.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			dPrintln("  " + key + ": " + headers.get(key));

		}
	}
	/**
	 * コネクションの接続を行う
	 * @param targetURL
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private HttpURLConnection openConnectionPre(String targetURL) throws MalformedURLException, IOException {
		URL url;
		HttpURLConnection urlcon;
		url = new URL(targetURL);
		urlcon = (HttpURLConnection) url.openConnection();
		return urlcon;
	}
}