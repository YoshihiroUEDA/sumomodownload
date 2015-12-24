package sumomodownload;

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
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

/**
 * 全て作り直し
 *
 * @author 吉祥
 *
 */
public class TEHentai4 extends TMyDebug {

	private static final String CONST_QUOTE_SET = "[\"']";
	private static final String CONST_QUOTE = "\"";
	private static final String PREFIX_HTTP = "http";
	private static final String PREFOX_SUB_PAGE = "http://g.e-hentai.org/s";

	static public void main(String[] args) {
		new TMain();
	}
	
	/**
	 *
	 * @param _url
	 * @param _saveFolder
	 */
	public TEHentai4(String _url, String _saveFolder) {
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
		URL url;
		HttpURLConnection urlcon;
		File file;
		String outputFolder = System.getProperty("user.home") + System.getProperty("file.separator") + "OneDrive"
				+ System.getProperty("file.separator") + item.getName();
		coutln( "item.getName() : "+ item.getName());
		coutln( "outputFolder = " + outputFolder);
		file = new File(outputFolder);
		if (!file.exists()) {
			if (!file.mkdir()) {
				coutln("save folder created.");
			}
		}
		try {
			ArrayList<String> list = httpDownload1stStage(item);
			ArrayList<String> listSubPage = new ArrayList<>();

			httpDownload2ndStage(list, listSubPage);

			for (int i = 0; i < listSubPage.size(); i++) {
				url = new URL(listSubPage.get(i));
				urlcon = (HttpURLConnection) url.openConnection();
				urlcon.setRequestMethod("GET");
				urlcon.setAllowUserInteraction(false);
				urlcon.connect();

				BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
				String line;
				while (null != (line = br.readLine())) {
					String words[] = line.split(CONST_QUOTE_SET);
					for (int xx = 0; xx < words.length; xx++) {
						if (words[xx].indexOf("http://g.e-hentai.org/?f_shash") >= 0)
							continue;
						if (words[xx].indexOf(PREFIX_HTTP) >= 0 && words[xx].indexOf(".jpg") >= 0) {
							coutln("words[" + xx + "]=" + words[xx]);
							String fileParts[] = words[xx].split("/");
							String filename = fileParts[fileParts.length - 1];
							coutln("filename = " + filename);
							downloadImage(words, xx, outputFolder, filename);
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
		URL imageURL = new URL(words[xx]);
		HttpURLConnection urlcon = (HttpURLConnection) imageURL.openConnection();
		urlcon.setRequestMethod("GET");
		urlcon.setInstanceFollowRedirects(true);
		urlcon.setAllowUserInteraction(false);
		urlcon.connect();

		int httpStatusCode = urlcon.getResponseCode();
		if (HttpURLConnection.HTTP_OK != httpStatusCode) {
			coutln("connect error");
			return;
		}
		// フォルダの存在チェック
		File f = new File(outputFolder);
		if (!f.exists() || !f.isDirectory()) {
			// フォルダが存在しない

			return;
		}
		String workname = outputFolder + System.getProperty("file.separator") + filename;
		coutln("workname = " + workname);
		File file = new File(workname);
		if (file.exists()) {
			File fileBackupName = new File(outputFolder + System.getProperty("file.separator") + "backup_" + filename);
			if (fileBackupName.exists()) {
				fileBackupName.delete();
			}
			file.renameTo(fileBackupName);

		}
		DataInputStream dis = new DataInputStream(urlcon.getInputStream());
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		byte[] buff = new byte[4096];
		int readByte = 0, totalSize = 0;
		while (-1 != (readByte = dis.read(buff))) {
			dos.write(buff, 0, readByte);
			totalSize += readByte;
		}
		if (0 == totalSize) {
			file.delete();
		} else {
			coutln("file size = " + totalSize);
		}
		dos.flush();
		dos.close();
		dos = null;
		dis.close();
		dis = null;
		urlcon.disconnect();
		urlcon = null;
	}

	private void httpDownload2ndStage(ArrayList<String> list, ArrayList<String> listSubPage)
			throws MalformedURLException, IOException, ProtocolException {
		URL url;
		HttpURLConnection urlcon;
		for (int lp = 0; lp < list.size(); lp++) {
			url = new URL(list.get(lp));
			urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setRequestMethod("GET");
			urlcon.setInstanceFollowRedirects(true);
			urlcon.setAllowUserInteraction(false);
			urlcon.connect();

			int httpStatusCode = urlcon.getResponseCode();
			if (HttpURLConnection.HTTP_OK != httpStatusCode) {
				coutln("connect error");
				return;
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
			while (true) {
				String line = br.readLine();
				if (null == line) {
					break;

				}
				String ws[] = line.split("<a href");
				for (int i = 0; i < ws.length; i++) {

					if (ws[i].indexOf(PREFIX_HTTP) >= 0 && ws[i].indexOf(PREFOX_SUB_PAGE) >= 0) {
						String ws2[] = ws[i].split(CONST_QUOTE);
						for (int k = 0; k < ws2.length; k++) {
							if (ws2[k].indexOf(PREFOX_SUB_PAGE) == 0) {
								listSubPage.add(ws2[k]);
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

	private ArrayList<String> httpDownload1stStage(DownloadItem item)
			throws MalformedURLException, IOException, ProtocolException {
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
	 * @param _name	漫画のタイトル
	 * @param _urlString	漫画のURL
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
