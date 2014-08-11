package com.shuyou.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Comparator;

import com.shuyou.net.BookDetail;
import com.shuyou.net.JsonParser;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

public class FileUtils {

	private static final String TAG = "FileUtils";

	// 默认的剩余磁盘缓存大小
	public static final int DEFAULT_REST_DISK_CACHE_SIZE = 1024 * 1024 * 20; // 20MB

	public static File getCacheFile(Context context, String imageUri) {
		File cacheFile = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// File sdCardDir = Environment.getExternalStorageDirectory();
			String fileName = getFileName(imageUri) + "_";
			/*
			 * File dir = new File(sdCardDir.getCanonicalPath() +
			 * AsynImageLoader.CACHE_DIR);
			 */
			File dir = getDiskCacheDir(context, "bookPics");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			cacheFile = new File(dir, fileName);
			LogHelper.i(TAG, "exists:" + cacheFile.exists() + ",dir:" + dir
					+ ",file:" + fileName);
		}

		return cacheFile;
	}

	public static String getFileName(String path) {
		int index = path.lastIndexOf("/");
		return path.substring(index + 1);
	}

	/**
	 * 获取可以使用的缓存目录
	 * 
	 * @param context
	 * @param uniqueName
	 *            目录名称
	 * @return
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) ? getExternalCacheDir(context)
				.getPath() : context.getCacheDir().getPath();

		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 获取程序外部的缓存目录
	 * 
	 * @param context
	 * @return
	 */
	public static File getExternalCacheDir(Context context) {
		final String cacheDir = "/Android/data/" + context.getPackageName()
				+ "/cache/";
		return new File(Environment.getExternalStorageDirectory().getPath()
				+ cacheDir);
	}

	/**
	 * 获取bitmap的字节大小
	 * 
	 * @param bitmap
	 * @return
	 */
	public static int getBitmapSize(Bitmap bitmap) {
		return bitmap.getRowBytes() * bitmap.getHeight();
	}

	/**
	 * 获取文件路径空间大小
	 * 
	 * @param path
	 * @return
	 */
	public static long getUsableSpace(File path) {
		LogHelper.i(TAG, path.getAbsolutePath());
		try {
			final StatFs stats = new StatFs(path.getPath());
			return (long) stats.getBlockSize()
					* (long) stats.getAvailableBlocks();
		} catch (Exception e) {
			LogHelper.e(TAG,
					"获取 sdcard 缓存大小 出错，请查看AndroidManifest.xml 是否添加了sdcard的访问权限");
			e.printStackTrace();
			return -1;
		}

	}

	/**
	 * 删除过期的图片
	 * 
	 * @author jiabin
	 * @param dirPath
	 *            删除文件所在的目录
	 * @param filelimits
	 *            该目录中文件上限
	 */
	public static void deleteExpiredImage(String dirPath, int filelimits) {
		if (filelimits <= 0 || dirPath == null)
			return;
		File Dir = new File(dirPath);
		if (!Dir.exists())
			return;
		if (!Dir.isDirectory())
			return;
		File list[] = Dir.listFiles();
		if (list == null || list.length <= filelimits)
			return;
		Arrays.sort(list, new Comparator<File>() {
			public int compare(File f1, File f2) {
				long diff = f1.lastModified() - f2.lastModified();
				if (diff > 0)
					return 1;
				else if (diff == 0)
					return 0;
				else
					return -1;
			}

			public boolean equals(Object obj) {
				return true;
			}

		});
		for (int k = 0; k < list.length - filelimits; k++)
			list[k].delete();
		return;

	}

	/**
	 * 写入详情页信息文件
	 * 
	 * @author jiabin
	 * @param item
	 * @return 成功返回true
	 */
	public static boolean writeDetailBookInfo(Context context, BookDetail item) {
		if (item == null) {
			return false;
		}

		if (!SDCardUtils.isExsit()) {
			LogHelper.w(TAG, "savePlateJsonData, sdcard was unmounted.");
			return false;
		}
		String filePath = SDCardUtils.getBookDetailInfoCachePath()
				+ item.getIsbn() + ".info";
		if (!createFile(filePath)) {
			return false;
		}
		String json = "";
		json = JsonParser.toJsonString(item);
		File file = new File(filePath);
		LogHelper.i(TAG, "setIni file=" + file.getAbsolutePath());
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(json.getBytes("utf-8"));
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
					fos = null;
					return false;
				}
			}
		}

	}

	/**
	 * 读取详情页信息文件
	 * 
	 * @author jiabin
	 * @param isbn
	 * @return 文件不存在或者读取异常都返回null
	 */
	public static BookDetail readDetailBookInfo(String isbn) {
		if (isbn == null) {
			return null;
		}
		if (!SDCardUtils.isExsit()) {
			LogHelper.w(TAG, "readPlateJsonData, sdcard was unmounted.");
			return null;
		}
		String filePath = SDCardUtils.getBookDetailInfoCachePath() + isbn
				+ ".info";
		File file = new File(filePath);
		if (!file.exists() || !file.isFile()) {
			LogHelper.w(TAG, "initFocus, path: " + filePath + " not exists");
			return null;
		}
		String json = "";
		StringBuffer sb = new StringBuffer();
		BookDetail item = null;
		if (file != null && file.exists()) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(file));
				String temp = null;
				temp = br.readLine();
				while (temp != null) {
					sb.append(temp);
					temp = br.readLine();
				}
				json = sb.toString();
				item = (BookDetail) JsonParser.parseObject(json, BookDetail.class);
				return item;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						br = null;
						return null;
					}
				}
			}
		}
		return item;
	}

	public static boolean createFile(String fileName) {
		if (TextUtils.isEmpty(fileName)) {
			LogHelper.w(TAG, "createFile, fileName: " + fileName);
			return false;
		}

		File file = new File(fileName);
		boolean isDirExists = false;
		if (!file.getParentFile().exists()) {
			isDirExists = file.getParentFile().mkdirs();
		} else {
			isDirExists = true;
		}

		if (!isDirExists) {
			LogHelper.i(TAG, "createFile, mkdir: "
					+ file.getParentFile().getAbsolutePath() + " --" + false);
		}

		if (!isDirExists) {
			return false;
		}

		boolean isFileExists = false;
		if (!file.exists()) {
			try {
				isFileExists = file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			isFileExists = true;
		}

		if (!isFileExists) {
			LogHelper.w(TAG, "createFile, mkFile: " + file.getAbsolutePath() + " --"
					+ isFileExists);
		}
		return isFileExists;
	}
	
	/**
     * 清空某个目录下的所有文件及文件夹
     * 
     * @param file
     */
    public static void clearCache(File file) {
        try {
            if (file != null && file.exists() && file.isDirectory()) {
                File[] fileList = file.listFiles();
                for (int i = 0; i < fileList.length; i++) {
                    if (fileList[i].isFile()) {
                        fileList[i].delete();
                    } else if (fileList[i].isDirectory()) {
                        clearCache(fileList[i]);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    /**
     * 清空某个目录下的所有文件及文件夹
     * 
     * @param filePathPath
     */
    public static void clearCache(String filePath) {
        try {
            clearCache(new File(filePath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
