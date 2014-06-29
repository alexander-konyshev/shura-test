/**
 * 
 */
package ru.lusoft.surguch.adapter;

import java.util.ArrayList;

import ru.lusoft.surguch.R;
import ru.lusoft.surguch.document.DocumentItem;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * @author Konyshev
 *
 */
public class DocumentListAdapter extends ArrayAdapter<DocumentItem> {

	private static final String TAG = "DocumentListAdapter";

	
	public DocumentListAdapter(Activity context, ArrayList<DocumentItem> objects) {
		super(context, R.layout.document_list, objects);
	}
	
	
	private static class ViewHolder {
		TextView name;
		TextView provider;
		TextView date;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i(TAG, "View for position=" + position);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			LayoutInflater inflater = LayoutInflater.from(getContext());
			convertView = inflater.inflate(R.layout.document_list, parent, false);
			holder.name = (TextView) convertView.findViewById(R.id.docName);
			holder.provider = (TextView) convertView.findViewById(R.id.provider);
			holder.date = (TextView) convertView.findViewById(R.id.docDate);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		DocumentItem item = getItem(position);
		holder.name.setText(item.getName());
		holder.provider.setText(item.getProvider());
		holder.date.setText(item.getDocumentDate().toString());
		return convertView;
	}
//	
//	
//	/* (non-Javadoc)
//	 * @see android.widget.ArrayAdapter#add(java.lang.Object)
//	 */
//	@Override
//	public void add(DocumentItem object) {
//		Log.i(TAG, "Add document: " + object);
//		super.add(object);
//	}
//	
//	
//	/* (non-Javadoc)
//	 * @see android.widget.ArrayAdapter#addAll(java.util.Collection)
//	 */
//	@Override
//	public void addAll(Collection<? extends DocumentItem> collection) {
//		Log.i(TAG, "Add all documents: " + collection);
//		super.addAll(collection);
//	}
//	
//	
//	/* (non-Javadoc)
//	 * @see android.widget.ArrayAdapter#addAll(java.lang.Object[])
//	 */
//	@Override
//	public void addAll(DocumentItem... items) {
//		Log.i(TAG, "All documents: " + items);
//		super.addAll(items);
//	}
}
