package com.example.bluetooth;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class BoxView extends View {

	Bitmap bm;
	Paint pnt;
	Canvas c;
	String str = "Made in Korea";
	int width;
	int height;
	float mX, mY;
	Path mPath;
	ArrayList<Vertex> arVertex;
	static final float TOUCH_TOLERANCE = 4;

	@SuppressLint("NewApi")
	public BoxView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

		bm = Bitmap.createBitmap(128, 16, Bitmap.Config.ARGB_8888);
		pnt = new Paint();
		c = new Canvas(bm);
		arVertex = new ArrayList<Vertex>();

		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Rect screenSize = new Rect();
		display.getRectSize(screenSize);		
		Log.d("LRFApp", "Size : " + screenSize);
		int w = bm.getWidth();
		int h = bm.getHeight();
		int X_Size = screenSize.width();
		int Y_Size = screenSize.height();
		X_Factor = X_Size/w;
		Y_Factor = (Y_Size/h) / 3;
		
	}

	@SuppressLint("NewApi")
	public BoxView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub

		bm = Bitmap.createBitmap(128, 16, Bitmap.Config.ARGB_8888);
		pnt = new Paint();
		c = new Canvas(bm);
		arVertex = new ArrayList<Vertex>();

		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Rect screenSize = new Rect();
		display.getRectSize(screenSize);		
		Log.d("LRFApp", "Size : " + screenSize);
		int w = bm.getWidth();
		int h = bm.getHeight();
		int X_Size = screenSize.width();
		int Y_Size = screenSize.height();
		X_Factor = X_Size/w;
		Y_Factor = (Y_Size/h) / 4;
		
	}

	@SuppressLint("NewApi")
	public BoxView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		bm = Bitmap.createBitmap(128, 16, Bitmap.Config.ARGB_8888);
		pnt = new Paint();
		c = new Canvas(bm);
		arVertex = new ArrayList<Vertex>();

		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Rect screenSize = new Rect();
		display.getRectSize(screenSize);		
		Log.d("LRFApp", "Size : " + screenSize);
		int w = bm.getWidth();
		int h = bm.getHeight();
		int X_Size = screenSize.width();
		int Y_Size = screenSize.height();
		X_Factor = X_Size/w;
		Y_Factor = (Y_Size/h) / 3;		
	}

	private int X_Factor;
	private int Y_Factor;
	@SuppressLint("NewApi")
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawColor(Color.WHITE);

		pnt.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		// 기본 문자열 출력. 안티 알리아싱을 적용했다.
		// pnt.setAntiAlias(true);
		pnt.setColor(Color.RED);

		int w = bm.getWidth();
		int h = bm.getHeight();
		
		Rect src = new Rect(0, 0, w, h);
		Rect dst = new Rect(0, 0, w * X_Factor, h * Y_Factor);

		c.drawColor(Color.BLACK);
		c.drawText(str, 25, 12, pnt);

		canvas.drawBitmap(bm, src, dst, pnt);

		for (int i = 0; i < arVertex.size(); i++) {
			if (arVertex.get(i).Draw) {
				c.drawLine(arVertex.get(i - 1).x, arVertex.get(i - 1).y,
						arVertex.get(i).x, arVertex.get(i).y, pnt);
			}

		}

	}

	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) { // 눌렀을때
			arVertex.add(new Vertex(event.getX()/X_Factor,event.getY()/Y_Factor,false));
			
			return true;
			
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			arVertex.add(new Vertex(event.getX()/X_Factor,event.getY()/Y_Factor,true));
			invalidate();
			
			return false;

		}
		return false;
	}

	public class Vertex { // 정점 하나에 대한 정보를 가지는 클래스

		float x;
		float y;
		boolean Draw;

		Vertex(float ax, float ay, boolean ad) {
			x = ax;
			y = ay;
			Draw = ad;
		}
	}
	
	public String update_RawData()
	{
        byte b;
        byte[] rawDataList = new byte[32*16];
		int i = 0;
		for (int y = 0; y < bm.getHeight(); y++) {
			for (int x = 0; x < bm.getWidth(); x += 4) {
				b = 0;
                if (Color.red(bm.getPixel(x + 0, y)) > 100) b |= 0x40;
                if (Color.red(bm.getPixel(x + 1, y)) > 100) b |= 0x10;
                if (Color.red(bm.getPixel(x + 2, y)) > 100) b |= 0x04;
                if (Color.red(bm.getPixel(x + 3, y)) > 100) b |= 0x01;

                    //b = 0x03;
                rawDataList[i++] = b;
			}
		}
		Log.d("RAW_DATA", rawDataList.toString());
		return bytesToHex(rawDataList);
	}
	
	public static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
	    for (byte b : bytes) {
	        sb.append(String.format("%02X", b));	     
	    }
	    return sb.toString();
	}
}

/*
 * @Override public boolean onTouch(View v, MotionEvent event) { // TODO
 * Auto-generated method stub
 * 
 * float x = event.getX(); float y = event.getY();
 * 
 * switch (event.getAction()) { case MotionEvent.ACTION_DOWN:
 * c.drawText("sdfsadfs", 20, 10, pnt); Log.d("DOwn", "DOWN");
 * 
 * //touch_start(x, y); //invalidate(); break; case MotionEvent.ACTION_MOVE:
 * //touch_move(x, y); Log.d("MOVE", "MOVE"); //invalidate(); break; case
 * MotionEvent.ACTION_UP: //touch_up(); Log.d("UP", "UP"); //invalidate();
 * break; }
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * return true;
 * 
 * 
 * 
 * 
 * }
 */

