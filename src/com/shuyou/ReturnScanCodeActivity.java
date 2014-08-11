package com.shuyou;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ReturnScanCodeActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_returnscancode);
		
		TextView tmpText = (TextView)findViewById(R.id.returnscancode_bookname);
		tmpText.setText(getIntent().getStringExtra("bookName"));
		tmpText = (TextView)findViewById(R.id.returnscancode_bookowner);
		tmpText.setText(getIntent().getStringExtra("bookOwnerName"));
		findViewById(R.id.returnscancode_bookowner_position).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(ReturnScanCodeActivity.this, UserInfoActivity.class);
				i.putExtra("userid", getIntent().getStringExtra("userid"));
				startActivity(i);
			}
		});
		
		ImageView codeImage = (ImageView)findViewById(R.id.returnscancode_codeimage);
		/*#shuyou#r，解码用，#shuyou#表述书友二维码，r表示换书二维码*/
		try {
			codeImage.setImageBitmap(CreateQRCode("#shuyou#r" + getIntent().getStringExtra("code")));
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		findViewById(R.id.returnscancode_back_button).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
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
