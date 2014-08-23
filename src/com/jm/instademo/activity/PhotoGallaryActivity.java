package com.jm.instademo.activity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.listeners.AuthStatusListener;
import com.instademo.R;
import com.jm.instademo.adapter.AdapterImage;
import com.jm.instademo.constants.ApplicationData;
import com.library.connection.InstagramApp;
import com.library.entity.ImageDetails;
import com.loopj.android.image.SmartImageView;

public class PhotoGallaryActivity extends Activity implements OnScrollListener{

	private ArrayList<ImageDetails> mURL;
	private SmartImageView mImageView;
	private LinearLayout lParentLayout;
	InstagramApp mApp;
	private AdapterImage mImageAdapters;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_gallary);
		setImagefromtoList();
		mApp = new InstagramApp(this, ApplicationData.CLIENT_ID,
				ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
		mApp.setListener(appendListListener);
	}

	@SuppressWarnings("unchecked")
	private void setImagefromtoList (){
		mURL= (ArrayList<ImageDetails>) getIntent().getSerializableExtra("URL");
		ListView lv = (ListView) findViewById(R.id.lvImagelist);
		mImageAdapters= new AdapterImage(getApplicationContext(), mURL);
		lv.setAdapter(mImageAdapters);
		lv.setOnScrollListener(this);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				displayFullScreenImage(mURL.get(position).getmImageURILarge());
			}
		});

	}

	private void displayFullScreenImage(String imageURL) {
		mImageView = (SmartImageView) findViewById(R.id.ivFullScreen);
		lParentLayout = (LinearLayout) findViewById(R.id.llParentLayout);
		new GetImagefromNetwork().execute(imageURL);

	}


	private class GetImagefromNetwork extends AsyncTask<String, Void, Bitmap>{
		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bm= getBitmapFromURL(params[0]);
			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			mImageView.setImageBitmap(result);
			lParentLayout.setVisibility(View.VISIBLE);
			super.onPostExecute(result);
		}

	}


	private  Bitmap getBitmapFromURL(String src) {
		try {
			URL url = new URL(src);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap =null;
			Canvas canvas = new Canvas();

			Display display = getWindowManager().getDefaultDisplay(); 
			Point lPoint = new Point();
			display.getSize(lPoint);
			int width = lPoint.x;
			int height =lPoint.y;
			try{
				myBitmap = BitmapFactory.decodeStream(input);
			} catch (Exception e){}
			myBitmap = Bitmap.createScaledBitmap(myBitmap,width,height,true);
			canvas.drawBitmap(myBitmap,0,0,null);
			return myBitmap;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}


	public void closeFullscreen(View view){
		((ImageView)view.findViewById(R.id.ivFullScreen)).setImageDrawable(null);;
		view.setVisibility(View.GONE);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	int preLast;

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		switch(view.getId()) {
		case R.id.lvImagelist:     

			int lastItem = firstVisibleItem + visibleItemCount;
			if(lastItem == totalItemCount) {
				if(preLast!=lastItem){ 
					mApp.fetchImageURL();
					preLast = lastItem;
				}
				else
					preLast=0;
			}
		}

	}
	
	AuthStatusListener appendListListener = new AuthStatusListener() {

		@Override
		public void onSuccess(ArrayList<ImageDetails> pURLs) {
		mURL.addAll(pURLs);	
		mImageAdapters.notifyDataSetChanged();
		}

		@Override
		public void onFail(String error) {
		}
	};

}