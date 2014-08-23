package com.jm.instademo.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.listeners.AuthStatusListener;
import com.instademo.R;
import com.jm.instademo.constants.ApplicationData;
import com.library.connection.InstagramApp;
import com.library.entity.ImageDetails;

public class MainActivity extends Activity {

	private InstagramApp mApp;
	private Button btnConnect;
	private TextView tvSummary;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.instademo.R.layout.main);

		mApp = new InstagramApp(this, ApplicationData.CLIENT_ID,
				ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
		mApp.setListener(listener);

		tvSummary = (TextView) findViewById(R.id.tvSummary);

		btnConnect = (Button) findViewById(R.id.btnConnect);
		btnConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if (mApp.hasAccessToken()) {
					final AlertDialog.Builder builder = new AlertDialog.Builder(
							MainActivity.this);
					builder.setMessage("Disconnect from Instagram?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialog, int id) {
							mApp.resetAccessToken();
							btnConnect.setText("Connect");
							tvSummary.setText("Not connected");
						}
					})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					final AlertDialog alert = builder.create();
					alert.show();
				} else {
					mApp.authorize();
				}
				
				mApp.getAccessToken("");
				
			}
		});

		if (mApp.hasAccessToken()) {
			tvSummary.setText("Connected");
			btnConnect.setText("Disconnect");
			mApp.fetchImageURL();
			
		}

	}
	
	
	public void showImageList(View v){
		if(mApp != null  && mApp.hasAccessToken())
		{
		mApp.fetchImageURL();
		}
		else
		{
			Toast.makeText(this, R.string.alert_message, Toast.LENGTH_SHORT).show();
		}
	}
	

	AuthStatusListener listener = new AuthStatusListener() {

		@Override
		public void onSuccess(ArrayList<ImageDetails> pURLs) {
			tvSummary.setText("Connected");
			btnConnect.setText("Disconnect");
			Intent i = new Intent(getApplicationContext(), PhotoGallaryActivity.class);
			i.putExtra("URL", pURLs);
			startActivity(i);
		}

		@Override
		public void onFail(String error) {
			Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
		}
	};
}