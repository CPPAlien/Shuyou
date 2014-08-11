package com.shuyou.lib;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import com.shuyou.utils.FileUtils;
import com.shuyou.utils.LogHelper;
import com.shuyou.values.Values;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AsyncImageLoader {

	private HashMap<String, SoftReference<Drawable>> imageCache;

	private Context mContext;

	private static final String TAG = "AsyncImageLoader";

	private boolean isCancel;
	private boolean isSave = true;

	public AsyncImageLoader(Context context) {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
		mContext = context;
		isCancel = false;
	}

	/**
	 * 停止图片下载，需要时可调用
	 * 
	 * @author jiabin
	 */
	public void stopPicDownload() {
		isCancel = true;
	}
	
	/**
	 * 关闭缓存
	 */
	public void shutdownSave() {
		isSave = false;
	}

	public Drawable loadDrawable(final String imageUrl,
			final ImageCallback imageCallback) {
		/*有缓存，且缓存打开，则直接读取图像*/
		if (imageCache.containsKey(imageUrl) && isSave) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageUrl);
			}
		};
		new Thread() {
			@Override
			public void run() {
				// Drawable drawable = loadImageFromUrl(imageUrl);
				File cacheFile = FileUtils.getCacheFile(mContext, imageUrl);
				if (cacheFile == null) {
					// TODO 处理异常,SD卡不存在
					return;
				}
				Drawable drawable = null;
				try {
					if (cacheFile.exists() && isSave) {
						drawable = Drawable.createFromPath(cacheFile
								.getCanonicalPath());
					} else {
						// 判断空间剩余量
						if (FileUtils.getUsableSpace(FileUtils.getDiskCacheDir(
								mContext, "bookPics")) < FileUtils.DEFAULT_REST_DISK_CACHE_SIZE) {
							/*Toast.makeText(mContext, "空间不足20M，无法缓存图片",
									Toast.LENGTH_SHORT).show();*/
							return;
						} else {
							drawable = getDrawableAndwrite(mContext, cacheFile,
									imageUrl);
						}
					}
				} catch (Exception e) {
					String temp = cacheFile.getAbsolutePath();
					cacheFile.delete();
					File tempFile = new File(temp + Values.PICSAVEFORMAT);
					tempFile.delete();
					/*Toast.makeText(mContext, "网络异常，图片加载失败！", Toast.LENGTH_SHORT)
							.show();*/
					e.printStackTrace();
					return;
				}
				if (drawable == null) {
					return;
				}
				imageCache.put(imageUrl, new SoftReference<Drawable>(drawable));
				Message message = handler.obtainMessage(0, drawable);
				handler.sendMessage(message);
			}
		}.start();
		return null;
	}

	protected Drawable getDrawableAndwrite(Context Context, File cacheFile,
			String imageUrl) throws IOException {
		Drawable drawable = null;
		// File cacheFile = FileUtil.getCacheFile(context, imageUri);

		// 显示网络上的图片
		URL myFileUrl = new URL(imageUrl);
		HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
		conn.setDoInput(true);
		conn.connect();

		InputStream is = conn.getInputStream();

		BufferedOutputStream bos = null;
		String temppath = cacheFile.getAbsolutePath();
		cacheFile = new File(temppath + Values.PICSAVEFORMAT);
		bos = new BufferedOutputStream(new FileOutputStream(cacheFile));
		LogHelper.i(TAG, "write file to " + cacheFile.getCanonicalPath());

		byte[] buf = new byte[1024];
		int len = 0;
		// 将网络上的图片存储到本地
		while ((len = is.read(buf)) > 0 && isCancel == false) {
			bos.write(buf, 0, len);
		}

		is.close();
		bos.close();

		if (isCancel == true) {
			cacheFile.delete();
			return null;
		}
		cacheFile.renameTo(new File(temppath));// 只是进行了重命名操作，cacheFile的路径并没有变
		// 从本地加载图片
		drawable = Drawable.createFromPath(temppath);
		// String name = MD5Util.MD5(imageUri);

		return drawable;
	}

	public static Drawable loadImageFromUrl(String url) {
		URL m;
		InputStream i = null;
		try {
			m = new URL(url);
			i = (InputStream) m.getContent();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		Drawable d = Drawable.createFromStream(i, "src");
		return d;
	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl);
	}

}
