package util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import sumomodownload.TMain;

/**
 * 全て作り直し
 *
 * @author 吉祥
 *
 */
public class TEHentai4 extends TMyDebug {
	public static final String LOGFILE = "Logger.txt"; // Log file.
	private static final String THIS_IS_WARNING_CONTENTS = "This gallery has been flagged as <strong>Offensive For Everyone</strong>. Due to its content, it should not be viewed by anyone.";
	private static final String CONST_QUOTE_SET = "[\"']";
	private static final String CONST_QUOTE = "\"";
	private static final String PREFIX_HTTP = "http";
	private static final String PREFOX_SUB_PAGE = "http://g.e-hentai.org/s";
	private static final String PREFIX_SUB_PAGE_MAIN = "http://g.e-hentai.org/g/";

	private Logger logger = null;

	static public void main(String[] args) {
		new TMain();
	}

	/**
	 *
	 * @param _url
	 * @param _saveFolder
	 */
	public TEHentai4(String _url, String _saveFolder) {
		logger = Logger.getLogger(this.getClass().getName());
		FileHandler fh = null;
		try {
			fh = new FileHandler(LOGFILE, true);
			fh.setFormatter(new java.util.logging.SimpleFormatter());

		} catch (SecurityException | IOException e) {
			// TODO 自動生成された catch ブロック
			logger.info(this.getClass().getName() + " Constructor error.");
			e.printStackTrace();

		}
		logger.addHandler(fh);
		logger.setLevel(Level.INFO);

		logger.info("TEHentai4() start.");

		DownloadItem item = new DownloadItem(_saveFolder, _url);
		downloadFilesFromURL(item);
	}

	public void TMain() {
		URL url = null;
		HttpURLConnection urlcon = null;

		// DownloadItem item = new DownloadItem("[蛇光院三郎] 膣内射精プラトニック",
		// "http://g.e-hentai.org/g/884909/5c7ba6b81c/");
		// DownloadItem item = new DownloadItem("[ペニイレ] 今日ママが友達に輪姦されます。 [DL版]",
		// "http://g.e-hentai.org/g/884995/de8c3877b7/" );
		// DownloadItem item =new DownloadItem("[宮野金太郎] ようこそ！潮吹き海岸海水欲情 (たのしいB地区)
		// [DL版] [童貞未泯漢化]", "http://g.e-hentai.org/g/884987/ef632af530/" );
		// DownloadItem item = new DownloadItem("[由浦カズヤ] きざし 第1-6話",
		// "http://g.e-hentai.org/g/884956/e64405aedb/");
		DownloadItem item = new DownloadItem("[しんば鷹史] 裸の湿度", "http://g.e-hentai.org/g/884896/7e9ffef386/");

		downloadFilesFromURL(item);

		coutln("program terminated.");
	}

	private void downloadFilesFromURL(DownloadItem item) {
		logger.info("downloadFilesFromURL() start.");

		URL url;
		HttpURLConnection urlcon;
		File file;
		String outputFolder = System.getProperty("user.home") + System.getProperty("file.separator") + "OneDrive"
				+ System.getProperty("file.separator") + "comic-private" + System.getProperty("file.separator")
				+ item.getName();

		logger.info("item.getName() : " + item.getName());
		logger.info("outputFolder = " + outputFolder);

		file = new File(outputFolder);
		if (!file.exists()) {
			if (!file.mkdir()) {
				logger.info("save folder [" + outputFolder + "] created.");
			}
		}
		logger.info("HTML download phase.");
		try {
			ArrayList<String> list = httpDownload1stStage(item);
			ArrayList<String> listSubPage = new ArrayList<>();

			httpDownload2ndStage(list, listSubPage, item.getUrlString());

			logger.info("subpage contents download");

			for (int i = 0; i < listSubPage.size(); i++) {
				url = new URL(listSubPage.get(i));
				urlcon = (HttpURLConnection) url.openConnection();
				urlcon.setRequestMethod("GET");
				urlcon.setAllowUserInteraction(false);
				urlcon.connect();
				logger.info("http status code=" + urlcon.getResponseCode());

				BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
				String line;
				while (null != (line = br.readLine())) {
					// logger.info(line);
					if (line.indexOf(THIS_IS_WARNING_CONTENTS) >= 0) {
						logger.info("グロページの発見画面に遭遇");

					}
					String words[] = line.split(CONST_QUOTE_SET);
					for (int xx = 0; xx < words.length; xx++) {
						if (words[xx].indexOf("http://g.e-hentai.org/?f_shash") >= 0)
							continue;
						if (words[xx].indexOf(PREFIX_HTTP) >= 0 && words[xx].indexOf(".jpg") >= 0) {
							coutln("words[" + xx + "]=" + words[xx]);
							String fileParts[] = words[xx].split("/");
							String filename = fileParts[fileParts.length - 1];
							logger.info("filename = " + filename);
							downloadImage2(words, xx, outputFolder, filename);
						}
					}
				}
			}
		} catch (MalformedURLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	private void downloadImage2(String[] words, int xx, String outputFolder, String filename)
			throws MalformedURLException, IOException {
		logger.info("downloadImage2() start...");

		logger.info("出力先フォルダのチェック starting...");
		File f = new File(outputFolder);
		if (!f.exists()) {
			logger.info("出力先フォルダが存在しない");
			logger.warning("cannot download file:" + filename);
			return;
		}

		logger.info("出力ファイル名の重複チェック starting...");
		String tempFilename = outputFolder + System.getProperty("file.separator");
		String workname = tempFilename + filename;
		f = new File(workname);
		if (f.exists()) {
			logger.info("出力ファイル名が存在します。");
			String[] fFilename = filename.split("\\.");
			logger.info("(ベースネームと拡張子で２が出ればOK) = " + fFilename.length);

			String baseFilename = fFilename[0];
			String filenameExt = fFilename[1];
			int i = 0;
			do {
				String worknametemp = new String(tempFilename + i + "_" + baseFilename + "." + filenameExt);
				logger.info(worknametemp);
				File ff = new File(worknametemp);
				if (!ff.exists())
					break;
				i++;
			} while (true);
			logger.info("old filename = " + f.getPath());
			f = new File(new String(tempFilename + i + "_" + baseFilename + "." + filenameExt));
			logger.info("new filename = " + f.getPath());

		}

		logger.info("ネットワーク接続処理 starting....");
		URL imageURL = new URL(words[xx]);
		HttpURLConnection urlcon = (HttpURLConnection) imageURL.openConnection();
		urlcon.setRequestMethod("GET");
		urlcon.setInstanceFollowRedirects(true);
		urlcon.setAllowUserInteraction(false);
		try {
			urlcon.connect();
		} catch (ConnectException e) {
			logger.info("connection error.");
			urlcon.disconnect();
			urlcon = null;
			logger.warning("cannot download file: " + filename);

			return;
		}
		int httpStatusCode = 0;
		try {
			httpStatusCode = urlcon.getResponseCode();
		} catch (SocketException e) {
			logger.warning("getResponseCode(); error:");
			logger.info(e.toString());
		}
		if (HttpURLConnection.HTTP_OK != httpStatusCode) {
			logger.warning("connect error");
			urlcon.disconnect();
			urlcon = null;
			logger.warning("cannot download file:" + filename);

			return;
		}
		logger.info("ダウンロードストリームの設定 starting...");
		DataInputStream dis = new DataInputStream(urlcon.getInputStream());
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
		} catch (FileNotFoundException e) {
			dis.close();
			dis = null;
			urlcon.disconnect();
			urlcon = null;
			logger.warning("cannot download file: " + filename);

			return;
		}

		logger.info("download loop starting...");
		byte[] buff = new byte[4096];
		int readByte = 0, totalSize = 0;
		try {

			while (-1 != (readByte = dis.read(buff))) {
				dos.write(buff, 0, readByte);
				totalSize += readByte;
			}
		} catch (SocketException e) {
			coutln("socketException coase.");
			coutln("skip process.");
		}

		logger.info("close process starting...");
		dos.flush();
		dos.close();
		dos = null;
		dis.close();
		dis = null;
		urlcon.disconnect();
		urlcon = null;
		logger.info("(249)Filename = " + f.getPath() + ",  totalSize = " + totalSize);
		if (0 == totalSize) {
			f.delete();
		} else {

			coutln("(254)file size = " + totalSize);
		}

		if (totalSize < 1000) {
			coutln("small size picture delete.");
			logger.info(f.getName() + "is delete.");
			f.delete();

		}

	}

	/**
	 *
	 * @param words
	 * @param xx
	 * @param outputFolder
	 * @param filename
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void downloadImage(String[] words, int xx, String outputFolder, String filename)
			throws MalformedURLException, IOException {
		logger.info("downloadImage() start");

		URL imageURL = new URL(words[xx]);
		HttpURLConnection urlcon = (HttpURLConnection) imageURL.openConnection();
		urlcon.setRequestMethod("GET");
		urlcon.setInstanceFollowRedirects(true);
		urlcon.setAllowUserInteraction(false);
		try {
			urlcon.connect();
		} catch (ConnectException e) {
			coutln("connect exception process skip.");
			logger.info("connection exception occured. process skip.");
			urlcon.disconnect();
			urlcon = null;

			return;

		}
		int httpStatusCode = urlcon.getResponseCode();
		logger.info("httpStatusCode = " + httpStatusCode);
		if (HttpURLConnection.HTTP_OK != httpStatusCode) {
			logger.warning("connect error");
			return;
		}
		// フォルダの存在チェック
		File f = new File(outputFolder);
		if (!f.exists() || !f.isDirectory()) {
			logger.info("フォルダが存在しない");

			// フォルダが存在しない
			// f.mkdir();
			return;
		}
		// ファイルの重複チェック
		String workname = outputFolder + System.getProperty("file.separator") + filename;
		logger.info("workname = " + workname);
		File file = new File(workname);
		// ファイルの重複チェック
		if (file.exists()) {
			logger.info("(189)書き出しファイルが存在します");

			int i = 0;
			File ff;
			String tempFilename = outputFolder + System.getProperty("file.separator");
			String[] fFilename = filename.split("\\.");
			logger.info("fFilename.length = " + fFilename.length);

			String baseFilename = fFilename[0];
			String filenameExt = fFilename[1];

			do {
				String worknametemp = new String(tempFilename + i + "_" + baseFilename + "." + filenameExt);
				ff = new File(worknametemp);
				if (!ff.exists())
					break;
				i++;
			} while (true);
			if (i != 0)
				logger.info("(207)new filename=" + ff.getName());

			file.renameTo(ff);

		}
		// if (file.exists()) {
		// logger.info("folder [" + workname + "] is exist.");
		// File fileBackupName = new File(outputFolder +
		// System.getProperty("file.separator") + "backup_" + filename);
		// if (fileBackupName.exists()) {
		// fileBackupName.delete();
		// coutln("file delete.");
		// }
		// file.renameTo(fileBackupName);
		//
		// }
		DataInputStream dis = new DataInputStream(urlcon.getInputStream());
		DataOutputStream dos = null;
		try {
			dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		} catch (FileNotFoundException e) {
			dis.close();
			dis = null;
			urlcon.disconnect();
			urlcon = null;
			return;
		}
		byte[] buff = new byte[4096];
		int readByte = 0, totalSize = 0;
		try {

			while (-1 != (readByte = dis.read(buff))) {
				dos.write(buff, 0, readByte);
				totalSize += readByte;
			}
		} catch (SocketException e) {
			coutln("socketException coase.");
			coutln("skip process.");
		}

		dos.flush();
		dos.close();
		dos = null;
		dis.close();
		dis = null;
		urlcon.disconnect();
		urlcon = null;
		logger.info("(249)Filename = " + file + "totalSize = " + totalSize);
		if (0 == totalSize) {
			file.delete();
		} else {

			coutln("(254)file size = " + totalSize);
		}

		if (totalSize < 1000) {
			coutln("small size picture delete.");
			logger.info(file.getName() + "is delete.");
			file.delete();

		}
	}

	private void httpDownload2ndStage(ArrayList<String> list, ArrayList<String> listSubPage, String urlString)
			throws MalformedURLException, IOException, ProtocolException {
		logger.info("httpDownload2ndStage() start..");

		URL url;
		HttpURLConnection urlcon;
		for (int lp = 0; lp < list.size(); lp++) {
			url = new URL(list.get(lp));
			urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setRequestMethod("GET");
			urlcon.setInstanceFollowRedirects(true);
			urlcon.setAllowUserInteraction(false);
			urlcon.setRequestProperty("Referer", urlString);
			urlcon.connect();

			int httpStatusCode = urlcon.getResponseCode();
			if (HttpURLConnection.HTTP_OK != httpStatusCode) {
				coutln("connect error");
				return;
			}

			logger.info("data size(HttpURLConnection).getContentLength()=" + urlcon.getContentLength());

			BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
			String line;
			while (null != (line = br.readLine())) {
				ArrayList<String> list_subpage = new ArrayList<>();
				if (line.indexOf(THIS_IS_WARNING_CONTENTS) >= 0) {
					logger.info("エイダの場合グロページの発見画面");
					while (null != (line = br.readLine())) {
						if (line.indexOf("<a href") >= 0) {
							logger.info("link found.");
							String ww[] = line.split("<a href");
							for (int lp1 = 0; lp1 < ww.length; lp1++) {
								if (ww[lp1].indexOf(PREFIX_HTTP) >= 0) {
									String[] wqq = ww[lp1].split(CONST_QUOTE);
									for (int lp2 = 0; lp2 < wqq.length; lp2++) {
										if (wqq[lp2].indexOf(PREFIX_HTTP) >= 0) {
											logger.info(wqq[lp2]);
											// エログロページは特殊処理
											list_subpage = downloadFilesFromWarningSceeen(wqq[lp2]);

										}
									}

								}
							}
						}
					}
					continue;
				}
				if (0!=list_subpage.size()) {
					for (int z = 0; z < list_subpage.size(); z++) {
						listSubPage.add(list_subpage.get(z));
					}
				}
				String ws[] = line.split("<a href");
				for (int i = 0; i < ws.length; i++) {
					// logger.info(ws[i]);

					if (ws[i].indexOf(PREFIX_HTTP) >= 0 && ws[i].indexOf(PREFOX_SUB_PAGE) >= 0) {
						String ws2[] = ws[i].split(CONST_QUOTE);

						for (int k = 0; k < ws2.length; k++) {
							// logger.info("-->" + ws2[k]);

							if (ws2[k].indexOf(PREFOX_SUB_PAGE) == 0) {
								listSubPage.add(ws2[k]);
								logger.info("url add: " + ws2[k]);
							}
						}

					}
				}
				;
			}
			urlcon.disconnect();
			urlcon = null;
			url = null;

		}
	}

	private ArrayList<String> downloadFilesFromWarningSceeen(String urlString) throws IOException {
		// TODO 自動生成されたメソッド・スタブ
		logger.info("downloadFilesFromWarningScreen() is starting...");
		logger.info("urlString:" + urlString);
		ArrayList<String> list = new ArrayList<>();

		URL url = new URL(urlString);
		HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
		urlcon.setRequestMethod("GET");
		urlcon.setInstanceFollowRedirects(true);
		urlcon.setAllowUserInteraction(false);

		urlcon.setRequestProperty("Referer", urlString);
		urlcon.connect();

		int httpStatusCode = urlcon.getResponseCode();
		int httpDataSize = urlcon.getContentLength();
		logger.info("http status code : " + httpStatusCode);
		logger.info("http content length: " + httpDataSize);

		BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));

		String line;
		while (null != (line = br.readLine())) {
			if (line.indexOf("<a href=") >= 0) {
				String[] ws = line.split(CONST_QUOTE_SET);
				for (int qqq = 0; qqq < ws.length; qqq++) {
					if (ws[qqq].indexOf(PREFIX_SUB_PAGE_MAIN) == 0) {
						coutln("->" + ws[qqq]);
						list.add(ws[qqq]);
					}
				}

			}
		}
		br.close();
		br = null;
		urlcon.disconnect();
		urlcon = null;
		url = null;
		return list;
	}

	private ArrayList<String> httpDownload1stStage(DownloadItem item)
			throws MalformedURLException, IOException, ProtocolException {
		logger.info("httpDownload1stStage() starting...");

		URL url;
		HttpURLConnection urlcon;
		url = new URL(item.getUrlString());
		urlcon = (HttpURLConnection) url.openConnection();
		urlcon.setRequestMethod("GET");
		urlcon.setInstanceFollowRedirects(true);
		urlcon.setAllowUserInteraction(false);
		urlcon.connect();

		int httpStatusCode = urlcon.getResponseCode();
		if (HttpURLConnection.HTTP_OK != httpStatusCode) {
			coutln("connect error");
			System.exit(-1);
		}
		File file;
		String words[] = item.getUrlString().split("/");
		String outputFilename = MY_SAVE_FOLDER + "\\" + words[words.length - 1];
		file = new File(outputFilename);
		if (file.exists()) {
			coutln("already file exists.");
			return null;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
		String line;
		ArrayList<String> list = new ArrayList<>();
		while (true) {
			line = br.readLine();
			if (null == line) {
				break;
			}
			words = line.split(CONST_QUOTE);
			for (int i = 0; i < words.length; i++) {
				if (words[i].indexOf(PREFIX_HTTP) == 0) {

					//
					if (words[i].indexOf(item.getUrlString()) >= 0) {

						list.add(words[i]);
					}
				} else if (words[i].indexOf(PREFIX_HTTP) > 0) {
					String ws[] = words[i].split("[()]");
					for (int j = 0; j < ws.length; j++) {
						if (ws[j].indexOf(PREFIX_HTTP) >= 0) {
							// coutln( ws[j]);
						}
					}
				}
			}

		}
		br.close();
		br = null;
		for (int i = 0; i < list.size(); i++) {
			for (int j = list.size() - 1; j > i; j--) {
				if (list.get(i).equals(list.get(j))) {
					list.remove(j);
				}
			}
		}
		for (int i = 0; i < list.size(); i++) {
			coutln(list.get(i));
		}
		return list;
	}

}

class TConst {
	final String MY_SAVE_FOLDER = "c:\\";

}

class TMyDebug extends TConst {
	public void coutln(String str) {
		System.out.println(str);
	}

}

class DownloadItem {
	String name;
	String urlString;

	/**
	 * item管理を行うclass
	 *
	 * @param _name
	 *            漫画のタイトル
	 * @param _urlString
	 *            漫画のURL
	 */
	public DownloadItem(String _name, String _urlString) {
		name = _name;
		urlString = _urlString;
	}

	public String getName() {
		return name;
	}

	public String getUrlString() {
		return urlString;
	}

}
