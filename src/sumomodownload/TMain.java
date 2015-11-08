package sumomodownload;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TMain {
	public static void main(String args[]) {
		System.out.println("Program start....");
		new TMain();
		System.out.println("Program terminated.");

	}

	public TMain() {

		DownloadDataLibrary obj = new DownloadDataLibrary();
		for (int kk = 0; kk < obj.length(); kk++) {
			try {

				TGetHtmlSource html = new TGetHtmlSource(obj.getUrl(kk));
				ArrayList<String> lists = html.getURLs();

				String storeSpaceName = obj.getName(kk);

				String strDesktopFolderName = System.getProperty("user.home");
				String strUserDir = System.getProperty("user.dir");
				String strFileSeparator = System.getProperty("file.separator");

				String saveFolderName = strDesktopFolderName + strFileSeparator + "Desktop" + strFileSeparator
						+ storeSpaceName;
				File f = new File(saveFolderName);
				if (!f.exists()) {
					if (!f.mkdir()) {
						System.out.println("folder[" + saveFolderName + "] is created.");
					}
				}

				System.out.println("TMain::TMain().saveFolderName:" + saveFolderName);

				for (int i = 0; i < lists.size(); i++) {
					TJpegFileDownloader obj2 = new TJpegFileDownloader(lists.get(i), saveFolderName, obj.getName(kk));
					obj2.filecleaner(saveFolderName);

				}

			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}

	}

}
