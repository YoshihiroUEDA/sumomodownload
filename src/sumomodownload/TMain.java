package sumomodownload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import util.DownloadDataLibrary;
import util.TDebug;
import util.TEHentai4;
import util.TGetHtmlSource;
import util.TJpegFileDownloader;

public class TMain extends TDebug {
	public static void main(String args[]) {
		System.out.println("Program start....");

		new TMain();
		System.out.println("Program terminated.");

	}

	public TMain() {
		setDebugAll(false);
		// ダウンロードの要素を持った配列
		DownloadDataLibrary obj = new DownloadDataLibrary();

		for (int kk = 0; kk < obj.length(); kk++) {
			String storeSpaceName = obj.getName(kk);

			String strDesktopFolderName = System.getProperty("user.home");
			String strUserDir = System.getProperty("user.dir");
			String strFileSeparator = System.getProperty("file.separator");
			String saveFolderName = strDesktopFolderName + strFileSeparator + "Desktop" + strFileSeparator
					+ storeSpaceName;
			File f = new File(saveFolderName);

			if (!f.exists()) {
				if (!f.mkdir()) {
					dPrintln("folder[" + saveFolderName + "] is created.");
				}
			}

			try {
				String line = obj.getUrl(kk);
				if (line.indexOf("e-hentai") >= 0) {
//
//					 setDebugFlag(true);
//					 dPrintln("e-hentai");
//					 TEHentai hen = new TEHentai(obj.getUrl(kk),
//					 saveFolderName, storeSpaceName);
//					 hen.doDownload();
//					 setDebugFlag(false);

					setDebugFlag(true);
					dPrintln("e-hentai download");
//					TEHentai3 obj1 = new TEHentai3(line, saveFolderName);
					TEHentai4 obj1 = new TEHentai4(line, storeSpaceName	);

					setDebugFlag(false);

				} else {
					TGetHtmlSource html = new TGetHtmlSource(obj.getUrl(kk));
					ArrayList<String> lists = html.getURLs();

					setDebugFlag(true);

					setDebugFlag(false);

					dPrintln("TMain::TMain().saveFolderName:" + saveFolderName);

					setDebugFlag(false);

					for (int i = 0; i < lists.size(); i++) {
						TJpegFileDownloader obj2 = new TJpegFileDownloader(lists.get(i), saveFolderName,
								obj.getName(kk));
						obj2.filecleaner(saveFolderName);

					}
				}

			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
			// System.out.println("Program terminated.");
		}

	}

}
