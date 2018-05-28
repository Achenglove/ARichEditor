package com.ccr.aricheditor.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class HttpClientHelper {
	private final String tag = HttpClientHelper.class.getSimpleName();
	private final boolean VDEBUG = true;

	/**
	 * download file from given URL
	 * 
	 * @param path
	 *            file URL
	 * @return if download success,return file path.other return null
	 */
	public String download(String path) {
		if (path.trim().equals("")) {
			if (VDEBUG) {
				Log.e(tag, "given path is empty!");
			}
			return "";
		}
		boolean res = true;
		String filePath = Environment.getExternalStorageDirectory().getPath()
				+ "/.android/folder/image/";
		File fp = new File(filePath);
		if (!fp.exists()) {
			if (!fp.mkdirs()) {
				if (VDEBUG) {
					Log.e(tag, "mkdir folder faild,folder path = " + filePath);
				}
				return "";
			}
		}
		String fileName = Math.abs(path.hashCode()) + ".jpg";
		fp = new File(filePath + fileName);
		if (fp.exists()) {
			if (VDEBUG) {
				Log.d(tag, "file " + fp.getName() + " exits");
			}
			return filePath + fileName;
		}
		InputStream inStream = null;
		FileOutputStream outStream = null;
		try {
			URL mUrl = new URL(path);
			HttpURLConnection conncetion = (HttpURLConnection) mUrl
					.openConnection();
			conncetion.setDoInput(true);
			conncetion.connect();
			inStream = conncetion.getInputStream();
			outStream = new FileOutputStream(fp);
			byte[] buf = new byte[4096];
			int len = 0;
			while ((len = inStream.read(buf)) != -1) {
				outStream.write(buf, 0, len);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			res = false;
		} catch (IOException e) {
			e.printStackTrace();
			res = false;
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					res = false;
				}
			}
			if (outStream != null) {
				try {
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					res = false;
				}
			}
		}
		if (res) {
			return filePath + fileName;
		} else {
			return "";
		}
	}
}