package org.wiktionary;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class WikiItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;

	public WikiItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
	}
	
	public WikiItemizedOverlay(Drawable defaultMarker, Context context) {
		this(defaultMarker);
		mContext = context;
	}
	
	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}
	
	@Override
	public int size() {
		return mOverlays.size();
	}
	
	@Override
	protected boolean onTap(int index) {
		// Weirdest thing ever! index is always 0 and mOverlays size is 1
		Log.d("WikiItemizedOverlay", "Index " + index + " Overlay title " + mOverlays.get(index).getTitle());
		final NearMeActivity ma = (NearMeActivity) mContext;
//		// find geoname
		final GeoName geoname = ma.getGeoName(mOverlays.get(index).getTitle());
		if(geoname != null) {
			final ClickableDialog dialog = new ClickableDialog(mContext);
			dialog.setContentView(R.layout.geoname_dialog);
			dialog.setTitle(geoname.getTitle());
			TextView summary = (TextView) dialog.findViewById(R.id.summary);
			summary.setText(geoname.getSummary());
			
			dialog.setCanceledOnTouchOutside(true);
			dialog.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
					Intent i = new Intent();
					Bundle b = new Bundle();
					b.putString("wiktionaryUrl", geoname.getWiktionaryUrl());
					Log.d("WikiItemizedOverlay", "Overlay URL "+geoname.getWiktionaryUrl());
					i.putExtras(b);
					ma.setResult(NearMePlugin.RESULT_OK, i);
					ma.finish();
				}
			});
			dialog.show();
		} else {
			Log.d("WikiItemizedOverlay", "Could not find geopoint");
		}
		
		return true;
	}

}
