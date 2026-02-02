package com.mediarevolution_remote.android;

import java.io.File;
import java.util.ArrayList;

import com.mediarevolution_remote.android.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class udpFolderArrayAdapter extends ArrayAdapter<String> {

	private class CachedView {
		public ImageView image;
		public TextView text1;
		public TextView text2;
		public CachedView(final View v) {
			image = (ImageView) v.findViewById(R.id.image);
			text1 = (TextView) v.findViewById(R.id.text1);
			text2 = (TextView) v.findViewById(R.id.text2);
			v.setTag(this);
		}
	}

	private final LayoutInflater mInflater;
	private ArrayList<String> mFolders = new ArrayList<String>();	

	public udpFolderArrayAdapter(final Context context, final int layout, final ArrayList<String> folders) {
		super(context, layout, folders);
		this.mInflater = LayoutInflater.from(context);
		this.mFolders = folders;
	}

	@Override
	public View getView(final int position, View view, final ViewGroup parent) {	
	 try {
		if (view == null) {
			view = mInflater.inflate(R.layout.udpselect_folder_list_item, null);
			new CachedView(view);
		}
		final CachedView cache = (CachedView)view.getTag();
		final String folder = mFolders.get(position).toString();
		final String folderX;
		String filename = "";
		if (folder != null) {
			cache.image.setImageResource(R.drawable.albumart_mp_unknown_small);	
			filename = MRUtils.extractFileNameFromStringX(new File(folder).getName());
			filename = MRUtils.extractFileNameFromStringWithoutExtension(filename);
			cache.text1.setText(filename);  
			folderX = new File(folder).getName().substring(0,folder.lastIndexOf("\\"));
			cache.text2.setTextSize(11);
			cache.text2.setSingleLine(false);
			cache.text2.setText(folderX);					
		}
		
	    } catch (final Exception e) {}  
	 
		return view;
	};

}  //ENDE