package com.jm.instademo.adapter;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.instademo.R;
import com.library.entity.ImageDetails;
import com.loopj.android.image.SmartImageView;

public class AdapterImage extends BaseAdapter {
	private Context mContext; 
	private ArrayList<ImageDetails> mImageDataList; 

	public AdapterImage(Context context, ArrayList<ImageDetails> pImageList) {
		this.mContext=context;
		this.mImageDataList=pImageList;
	}

	public int getCount() {
		return mImageDataList.size();

	}

	public Object getItem(int position) {
		return mImageDataList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			LayoutInflater lLayoutInflater = LayoutInflater.from(mContext);
			convertView = lLayoutInflater.inflate(R.layout.rowlist,null);
			holder = new ViewHolder();
			holder.lImageView = (SmartImageView) convertView.findViewById(R.id.ivRowImage);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.lImageView.setImageUrl(mImageDataList.get(position).getmImageURI());


		return convertView;
	}

	static class  ViewHolder {
		SmartImageView lImageView;

	}

}
