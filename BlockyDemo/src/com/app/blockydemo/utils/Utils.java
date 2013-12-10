/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/**
 * Copyright for original "String buildPath" held by:
 * 	Copyright (C) 2008 Rob Manning
 * 	manningr@users.sourceforge.net
 * Source: http://www.java2s.com/Code/Java/File-Input-Output/Autilityclassformanipulatingpaths.htm
 */

package com.app.blockydemo.utils;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.badlogic.gdx.utils.GdxRuntimeException;

import com.app.blockydemo.ProjectManager;
import com.app.blockydemo.R;
import com.app.blockydemo.common.Constants;
import com.app.blockydemo.common.LookData;
import com.app.blockydemo.common.ScreenValues;
import com.app.blockydemo.common.SoundInfo;
import com.app.blockydemo.content.Project;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Utils {

	private static final String TAG = Utils.class.getSimpleName();
	public static final int PICTURE_INTENT = 1;
	public static final int FILE_INTENT = 2;
	public static final int TRANSLATION_PLURAL_OTHER_INTEGER = 767676;
	private static final int DEFAULT_SCREEN_WIDTH = 1280;
	private static final int DEFAULT_SCREEN_HEIGHT = 768;

	// Suppress default constructor for noninstantiability
	private Utils() {
		throw new AssertionError();
	}

	public static boolean externalStorageAvailable() {
		String externalStorageState = Environment.getExternalStorageState();
		return externalStorageState.equals(Environment.MEDIA_MOUNTED)
				&& !externalStorageState.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
	}

	public static boolean checkForExternalStorageAvailableAndDisplayErrorIfNot(final Context context) {
		return true;
	}

	public static void updateScreenWidthAndHeight(Context context) {
		if (context != null) {
			WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			DisplayMetrics displayMetrics = new DisplayMetrics();
			windowManager.getDefaultDisplay().getMetrics(displayMetrics);
			ScreenValues.SCREEN_WIDTH = displayMetrics.widthPixels;
			ScreenValues.SCREEN_HEIGHT = displayMetrics.heightPixels;
		} else {
			//a null-context should never be passed. However, an educated guess is needed in that case.
			ScreenValues.SCREEN_WIDTH = DEFAULT_SCREEN_WIDTH;
			ScreenValues.SCREEN_HEIGHT = DEFAULT_SCREEN_HEIGHT;
		}

	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	/**
	 * Constructs a path out of the pathElements.
	 * 
	 * @param pathElements
	 *            the strings to connect. They can have "/" in them which will be de-duped in the result, if necessary.
	 * @return
	 *         the path that was constructed.
	 */
	public static String buildPath(String... pathElements) {
		StringBuilder result = new StringBuilder("/");

		for (String pathElement : pathElements) {
			result.append(pathElement).append("/");
		}

		String returnValue = result.toString().replaceAll("/+", "/");

		if (returnValue.endsWith("/")) {
			returnValue = returnValue.substring(0, returnValue.length() - 1);
		}

		return returnValue;
	}

	public static String buildProjectPath(String projectName) {
		return buildPath(Constants.DEFAULT_ROOT, deleteSpecialCharactersInString(projectName));
	}

	public static void showErrorDialog(Context context, int errorMessageId) {
	}

	public static View addSelectAllActionModeButton(LayoutInflater inflator, ActionMode mode, Menu menu) {
		return null;
	}

	public static String md5Checksum(File file) {

		if (!file.isFile()) {
			return null;
		}

		MessageDigest messageDigest = getMD5MessageDigest();

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[Constants.BUFFER_8K];

			int length = 0;

			while ((length = fis.read(buffer)) != -1) {
				messageDigest.update(buffer, 0, length);
			}
		} catch (IOException e) {
			Log.w(TAG, "IOException thrown in md5Checksum()");
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				Log.w(TAG, "IOException thrown in finally block of md5Checksum()");
			}
		}

		return toHex(messageDigest.digest()).toLowerCase(Locale.US);
	}

	public static String md5Checksum(String string) {
		MessageDigest messageDigest = getMD5MessageDigest();

		messageDigest.update(string.getBytes());

		return toHex(messageDigest.digest()).toLowerCase(Locale.US);
	}

	private static String toHex(byte[] messageDigest) {
		StringBuilder md5StringBuilder = new StringBuilder(2 * messageDigest.length);

		for (byte b : messageDigest) {
			md5StringBuilder.append("0123456789ABCDEF".charAt((b & 0xF0) >> 4));
			md5StringBuilder.append("0123456789ABCDEF".charAt((b & 0x0F)));
		}

		return md5StringBuilder.toString();
	}

	private static MessageDigest getMD5MessageDigest() {
		MessageDigest messageDigest = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			Log.w(TAG, "NoSuchAlgorithmException thrown in getMD5MessageDigest()");
		}

		return messageDigest;
	}

	public static int getVersionCode(Context context) {
		int versionCode = -1;
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			versionCode = packageInfo.versionCode;
		} catch (NameNotFoundException nameNotFoundException) {
			Log.e(TAG, "Name not found", nameNotFoundException);
		}
		return versionCode;
	}

	public static String getVersionName(Context context) {
		String versionName = "unknown";
		try {
			PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			versionName = packageInfo.versionName;
		} catch (NameNotFoundException nameNotFoundException) {
			Log.e(TAG, "Name not found", nameNotFoundException);
		}
		return versionName;
	}

	public static int getPhysicalPixels(int densityIndependentPixels, Context context) {
		final float scale = context.getResources().getDisplayMetrics().density;
		int physicalPixels = (int) (densityIndependentPixels * scale + 0.5f);
		return physicalPixels;
	}

	public static void saveToPreferences(Context context, String key, String message) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = sharedPreferences.edit();
		edit.putString(key, message);
		edit.commit();
	}

	public static void loadProjectIfNeeded(Context context) {
	}

	public static String getCurrentProjectName(Context context) {
		return ProjectManager.getInstance().getCurrentProject().getName();
	}

	public static String deleteSpecialCharactersInString(String stringToAdapt) {
		return stringToAdapt.replaceAll("[\"*/:<>?\\\\|]", "");
	}

	public static String getUniqueObjectName(String name) {
		return searchForNonExistingObjectNameInCurrentProgram(name, 0);
	}

	private static String searchForNonExistingObjectNameInCurrentProgram(String name, int nextNumber) {
		String newName;

		if (nextNumber == 0) {
			newName = name;
		} else {
			newName = name + nextNumber;
		}

		if (ProjectManager.getInstance().spriteExists(newName)) {
			return searchForNonExistingObjectNameInCurrentProgram(name, ++nextNumber);
		}

		return newName;
	}

	public static String getUniqueLookName(String name) {
		return searchForNonExistingLookName(name, 0);
	}

	private static String searchForNonExistingLookName(String name, int nextNumber) {
		String newName = null;
		return newName;
	}

	public static String getUniqueSoundName(String title) {
		return searchForNonExistingSoundTitle(title, 0);
	}

	public static Project findValidProject() {
		Project loadableProject = null;
		return loadableProject;
	}

	private static String searchForNonExistingSoundTitle(String title, int nextNumber) {
		// search for sounds with the same title
		String newTitle = null;
		return newTitle;
	}

	public static Pixmap getPixmapFromFile(File imageFile) {
		Pixmap pixmap = null;
		try {
			GdxNativesLoader.load();
			pixmap = new Pixmap(new FileHandle(imageFile));
		} catch (GdxRuntimeException e) {
			return null;
		} catch (Exception e1) {
			return null;
		}
		return pixmap;
	}

	public static void rewriteImageFileForStage(Context context, File lookFile) throws IOException {
	}

	public static String getUniqueProjectName() {
		String projectName = "project_" + String.valueOf(System.currentTimeMillis());
		return projectName;
	}

	public static boolean isStandardProject(Project projectToCheck, Context context) {
		return true;

	}

	public static int convertDoubleToPluralInteger(double value) {
		double absoluteValue = Math.abs(value);
		if (absoluteValue > 2.5) {
			return (int) Math.round(absoluteValue);
		} else {
			if (absoluteValue == 0.0 || absoluteValue == 1.0 || absoluteValue == 2.0) {
				return (int) absoluteValue;
			} else {
				// Random Number to get into the "other" keyword for values like 0.99 or 2.001 seconds or degrees
				// in hopefully all possible languages
				return TRANSLATION_PLURAL_OTHER_INTEGER;
			}
		}
	}

	public static boolean checkIfProjectExistsOrIsDownloadingIgnoreCase(String programName) {
		File projectDirectory = new File(Utils.buildProjectPath(programName));
		return projectDirectory.exists();
	}

	public static void setSelectAllActionModeButtonVisibility(View selectAllActionModeButton, boolean setVisible) {
		if (selectAllActionModeButton == null) {
			return;
		}

		if (setVisible) {
			selectAllActionModeButton.setVisibility(View.VISIBLE);
		} else {
			selectAllActionModeButton.setVisibility(View.GONE);
		}
	}
}
