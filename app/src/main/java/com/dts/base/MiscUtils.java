package com.dts.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.widget.Toast;

import com.dts.base.appGlobals;
import com.dts.combust.R;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Locale;

public class MiscUtils {
	
	public ColorDef color;
	
	private Context cont;
	private appGlobals gl;
	
	private DecimalFormat thnumformat,thdecformat,plainnumformat,decformat,onedecformat,sixformat;
    private String slang="EN";
	
	public MiscUtils(Context context, appGlobals global) {
		
		cont=context; 
		gl=global;
		
		color=new ColorDef(cont);
		
		thnumformat = new DecimalFormat("#,###");
		thdecformat = new DecimalFormat("#,###.##");
		decformat = new DecimalFormat("#0.00");
		plainnumformat = new DecimalFormat("#0");
		onedecformat = new DecimalFormat("#0.0");
		sixformat = new DecimalFormat("#0.000000");
	}
	
	public MiscUtils(Context context) {
		
		cont=context; 
		
		color=new ColorDef(cont);
		
		thnumformat = new DecimalFormat("#,###");
		thdecformat = new DecimalFormat("#,###.##");
		decformat = new DecimalFormat("#0.00");
		plainnumformat = new DecimalFormat("#0");
		onedecformat = new DecimalFormat("#0.0");
		sixformat = new DecimalFormat("#0.000000");
	}
	
	public String decfrm(double val) {
		return decformat.format(val);
	}
	
	public String frmintth(double val) {
		return thnumformat.format(val);
	}
	
	public String frmdblth(double val) {
		return thdecformat.format(val);
	}
	
	public String frmintnum(double val) {
		return plainnumformat.format(val);
	}

	public String frmonedec(double val) {
		return onedecformat.format(val);
	}

	public String frmsix(double val) {
		return sixformat.format(val);
	}
	
	public int CInt(String s) {
		return Integer.parseInt(s);
	}

	public double CDbl(String s) {
		return Double.parseDouble(s);
	}
	
	public String CStr(int v){
		return String.valueOf(v);
	}
	
	public String CStr(double v){
		return String.valueOf(v);
	}
	
	public boolean emptystr(String s){
		if (s==null || s.isEmpty()) {
			return true;
		} else{
			return false;
		}
	}

	public void setlang(){
		slang= Locale.getDefault().getLanguage();
		slang=slang.substring(0,2);
	}
	
	public String lstr(String s1, String s2) {
		if (slang.equalsIgnoreCase("ES")) {
			return s2;
		} else {	
			return s1;
		}
	}
	
	public void msgbox(String msg) {
		
		if (msg==null || msg.isEmpty()) {return;}
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(cont);
    	
		dialog.setTitle(R.string.app_name);
		dialog.setMessage(msg);
		//dialog.setIcon(R.drawable.info48);
		
		dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which) {
    	    	//Toast.makeText(getApplicationContext(), "Yes button pressed",Toast.LENGTH_SHORT).show();
    	    }
    	});
		dialog.show();
	
	}   
	
	public void msgbox(int v) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(cont);
    	
		dialog.setTitle(R.string.app_name);
		dialog.setMessage(String.valueOf(v));
		//dialog.setIcon(R.drawable.info48);
		
		dialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which) {
    	    }
    	});
		dialog.show();
	
	}   
	
	public void toast(String msg) {
		Toast.makeText(cont,msg, Toast.LENGTH_SHORT).show();
	}
	
	public void toast(double msg) {
		Toast.makeText(cont,""+msg, Toast.LENGTH_SHORT).show();
	}
	
	public int intdiv(double val,double div) {
		if (div==0) return 0;
		
		val=val/div;
		return (int) val;
	}
	
	public int trunc(double val,double div) {
		if (div==0) return 0;
		
		val=val/div;val= Math.floor(val);
		return (int) val;
	}
	
	public class ColorDef {
		
		public int neonyellow,neongreen,neonorange,neonpink,neonblue;
		
		private Context cont;
		
		public ColorDef(Context context) {
			cont=context; 
			
			neonyellow= Color.rgb(243,243,21);
			neongreen= Color.rgb(193,253,51);
			neonorange= Color.rgb(255,153,51);
			neonpink= Color.rgb(252,90,184);
			neonblue= Color.rgb(13,213,252);
			
		}
		
	}
	
	public boolean fileexists(String fname) {
		try {
			File file = new File(fname);
			return file.exists();
		} catch (Exception e) {
			return false;
		}	
	}

	public Bitmap scaleBitmap(Bitmap bm, int size1, int size2) {
		Bitmap bms;
		Matrix matrix;
		int imw,imh;
		double bmw,bmh,z1,z2,z3,z4,zm1,zm2,zm;

		try {
			bmw=bm.getWidth();
			bmh=bm.getHeight();

			z1=bmw/size1;
			z2=bmh/size2;
			zm1=Math.max(z1,z2);
			z3=bmw/size2;
			z4=bmh/size1;
			zm2=Math.max(z3,z4);
			zm=Math.min(zm1,zm2);

			imw=(int) (bmw/zm);
			imh=(int) (bmh/zm);

			Bitmap scaledBitmap = Bitmap.createBitmap(imw,imh,Bitmap.Config.RGB_565);

			float scaleX = imw / (float) bm.getWidth();
			float scaleY = imh / (float) bm.getHeight();
			float pivotX = 0;
			float pivotY = 0;

			Matrix scaleMatrix = new Matrix();
			scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

			Canvas canvas = new Canvas(scaledBitmap);
			canvas.setMatrix(scaleMatrix);
			canvas.drawBitmap(bm, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

			return scaledBitmap;
		} catch (Exception e) {
			String ee=e.getMessage();
			return null;
		}


	}

}

