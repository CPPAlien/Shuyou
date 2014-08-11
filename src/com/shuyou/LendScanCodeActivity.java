package com.shuyou;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LendScanCodeActivity extends Activity{

	private String daysString = null;
	private TextView daysText = null;
	private ImageView codeImage = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_lendscancode);
		TextView tmpText = (TextView)findViewById(R.id.lendscancode_bookname);
		tmpText.setText(getIntent().getStringExtra("bookName"));
		daysText = (TextView)findViewById(R.id.lendscancode_lenddays);
		daysString = daysText.getText().toString();
		if(daysString.length() == 1) {   //如果是一位，则在前面添加0,为方便后面解码统一处理
			daysString = "0"+daysString;
		}
		
		codeImage = (ImageView)findViewById(R.id.lendscancode_codeimage);
		/*#shuyou#l，解码用，#shuyou#表述书友二维码，l表示借出二维码*/
		try {
			codeImage.setImageBitmap(CreateQRCode("#shuyou#l" + daysString + getIntent().getStringExtra("code")));
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		findViewById(R.id.lendscancode_back_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		findViewById(R.id.lendscancode_lenddays_position).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Dialog daysPickerDialog = new Dialog(LendScanCodeActivity.this, R.style.ShuyouDialog);
				daysPickerDialog.setCanceledOnTouchOutside(true);
				daysPickerDialog.setContentView((RelativeLayout)getLayoutInflater().inflate(R.layout.shuyou_dialog_dayspicker, null));
				final EditText daysEdit = (EditText)daysPickerDialog.findViewById(R.id.dayspicker_days);
				daysEdit.setText(daysString);
				daysPickerDialog.findViewById(R.id.dayspicker_sub_button).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int tmpDaysCount = Integer.parseInt(daysString);
						-- tmpDaysCount;
						if(tmpDaysCount < 1){
							tmpDaysCount = 1;
						}
						daysEdit.setText(tmpDaysCount + "");
						daysString = tmpDaysCount + "";
						daysText.setText(daysString);
					}
					
				});
				
				daysPickerDialog.findViewById(R.id.dayspicker_plus_button).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						int tmpDaysCount = Integer.parseInt(daysString);
						++ tmpDaysCount;
						if(tmpDaysCount > 99){
							tmpDaysCount = 99;
						}
						daysEdit.setText(tmpDaysCount + "");
						daysString = tmpDaysCount + "";
						daysText.setText(daysString);
					}
					
				});
				//每次重新设定时间后，改变二维码
				daysPickerDialog.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface arg0) {
						// TODO Auto-generated method stub
						if(daysString.length() == 1) {   //如果是一位，则在前面添加0,为方便后面解码统一处理
							daysString = "0"+daysString;
						}
						try {
							codeImage.setImageBitmap(CreateQRCode("#shuyou#l" + daysString + getIntent().getStringExtra("code")));
						} catch (WriterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				daysPickerDialog.show();
			}
			
		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	/**
	 * 生成二维码图像
	 * @param str
	 * @return
	 * @throws WriterException
	 */
	public Bitmap CreateQRCode(String str) throws WriterException
	{
		//生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
		BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, 400, 400);
		int width = matrix.getWidth();  
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];  
        for (int y = 0; y < height; y++) {  
            for (int x = 0; x < width; x++) {  
                if(matrix.get(x, y)){  
                    pixels[y * width + x] = 0xff000000;  
                }  
                  
            }  
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);  
        //通过像素数组生成bitmap,具体参考api  
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
        return bitmap;
	}
	
}