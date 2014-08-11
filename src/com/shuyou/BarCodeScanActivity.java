package com.shuyou;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;

import com.bookpals.zxingscanner.OnDecodeCompletionListener;
import com.bookpals.zxingscanner.ScannerView;
import com.umeng.analytics.MobclickAgent;

public class BarCodeScanActivity extends Activity {

	private ScannerView scannerView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_barcodescan);
		scannerView = (ScannerView)findViewById(R.id.scanner_view);
		scannerView.setOnDecodeListener(new OnDecodeCompletionListener() {
			
			@Override
			public void onDecodeCompletion(String barcodeFormat, String barcode, Bitmap bitmap) {
				Intent i = new Intent(BarCodeScanActivity.this, BarCodeScanResultActivity.class);
				i.putExtra("barcodeFormat", barcodeFormat);
				i.putExtra("barcode", barcode);
				startActivity(i);
				finish();
			}
		});
	}
	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		scannerView.onPause();
		MobclickAgent.onPause(this);
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		scannerView.onResume();
		MobclickAgent.onResume(this);
	}

	
}
