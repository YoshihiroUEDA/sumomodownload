package util;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class TJpegFileDownloader  extends TDebug{
	public TJpegFileDownloader(String downloadFileURL, String downloadFolderName, String refere) {
		File f = new File(downloadFolderName);
		if (!f.exists()) {
			return;
		}
		URL url = null;
		HttpURLConnection urlcon = null;

		String words[] = downloadFileURL.split("/");
		String filename = words[words.length - 1];
		String saveFilename = downloadFolderName + System.getProperty("file.separator")
				+ System.getProperty("file.separator") + filename;
		dPrintln("save filename:" + saveFilename);

		f = renameDuplicateFilename(saveFilename);
		try {
			url = new URL(downloadFileURL);
			urlcon = (HttpURLConnection) url.openConnection();
			urlcon.setRequestMethod("POST");
			urlcon.setRequestProperty("Referer", refere);
			urlcon.setAllowUserInteraction(false);
			urlcon.setInstanceFollowRedirects(true);
			urlcon.setRequestMethod("GET");
			urlcon.connect();

			int httpStatusCode = urlcon.getResponseCode();
			if (HttpURLConnection.HTTP_OK != httpStatusCode) {
				System.out.println("session error.");
				System.exit(-1);
			}
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
		// filecleaner(downloadFolderName);

	}

	private File renameDuplicateFilename(String saveFilename) {
		File f;
		f = new File(saveFilename);
		if (f.exists()) {
			// rename処理が必要
			String temp = null;
			for (int i = 0; i < 10; i++) {
				temp = saveFilename.replace(".jpg", i + ".jpg");
				File tempFile = new File(temp);
				if (!f.exists())
					break;
			}
			dPrintln("src: " + saveFilename);
			dPrintln("dest:" + temp);

			// System.out.println("file exists.");
			// System.exit(-1);
		}
		return f;
	}

	public void filecleaner(String downloadFolderName) {
		
		setDebugFlag(false);
		
		dPrintln("縮小版ファイルの移動");

		String multipleFolder = "縮小版";
		String strSeparator = System.getProperty("file.separator");
		String smallPicFolder = downloadFolderName + strSeparator + multipleFolder;

		File f1 = new File(downloadFolderName);
		//
		File f2 = new File(smallPicFolder);
		if (!f2.exists()) {
			f2.mkdir();
		}

		File[] fs = f1.listFiles();
		for (int i = 0; i < fs.length; i++) {
			String filename1 = fs[i].getName();
			if (filename1.indexOf("s.jpg") >= 0) { // 縮小版ファイルがあった
				File FileMoveToName = null;
				dPrintln(fs[i].getPath());
				dPrintln(fs[i].getParent());
				dPrintln(fs[i].getName());
				String moveToFolderName = fs[i].getParent() + strSeparator + multipleFolder;
				String moveToFilename = moveToFolderName + strSeparator + fs[i].getName();
				File fdest = new File(moveToFilename);
				fs[i].renameTo(fdest);
				// System.exit(-1);
			}

		}
	}

}
