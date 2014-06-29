package ru.lusoft.surguch;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import ru.lusoft.surguch.adapter.DocumentListAdapter;
import ru.lusoft.surguch.document.DocumentItem;
import ru.lusoft.surguch.security.UserToken;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
//	private static final String URL = "http://192.168.88.236:3000/doclist.json";
	
	ArrayList<DocumentItem> documents;
	ArrayAdapter<DocumentItem> adapter;
	View loadMoreDocuments;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.list);
        documents = new ArrayList<DocumentItem>();
        Log.i(TAG, "Document list: " + documents);
        adapter = new DocumentListAdapter(this, documents);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	
        	@SuppressLint("ShowToast")
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		DocumentItem item = (DocumentItem) parent.getItemAtPosition(position);
        		Toast.makeText(getApplicationContext(), "Click on document: " + item, Toast.LENGTH_LONG);
        	}
		});
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

//			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Toast.makeText(getBaseContext(), "Long clicked (id=" + id + "): " + adapter.getItem(position), 
						Toast.LENGTH_SHORT).show();
				return false;
			}
		});
		listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
			
//			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		    	getMenuInflater().inflate(R.menu.actions, menu);
			}
		});
		
//		new BackgroundDocumentList().execute(URL);
		// достаточно для того, чтобы обновить список.
		documents.add(new DocumentItem("1", "Документ 1", "Провайдер 1", null));
        documents.add(new DocumentItem("2", "Документ 2", "Провайдер 2", null));
        adapter.notifyDataSetChanged();
    }
    
    
    @SuppressLint("ShowToast") 
    public void parseDocuments(String resp) {
    	if (resp == null) {
    		Toast.makeText(getBaseContext(), "Nothing any documents", Toast.LENGTH_LONG).show();
    		documents.clear();
    		adapter.notifyDataSetChanged();
    		return;
    	}
		try {
			JSONArray array = new JSONArray(resp);
			List<DocumentItem> items = DocumentItem.toModels(array);
			documents.clear();
			documents.addAll(items);
    		adapter.notifyDataSetChanged();
		} catch (JSONException e) {
			Log.w(TAG, "Cannot to get json-array from string '" + resp + "'", e);
		}
    }
    
    
    /* This will be invoked when a menu item is selected */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	switch (item.getItemId()) {
		case R.id.view_doc: // view document
			Toast.makeText(getBaseContext(), "View document: position=" + info.position, Toast.LENGTH_SHORT).show();
			Intent i = new Intent(getBaseContext(), ViewDocument.class);
			i.putExtra("index", info.position);
			i.putExtra("body", documents.get(info.position).getName());
			startActivity(i);
			break;
		case R.id.sing_doc: // sing in document
			Toast.makeText(getBaseContext(), "Sing-in document: position=" + info.position, Toast.LENGTH_SHORT).show();
			Intent sd = new Intent(getBaseContext(), SingDocument.class);
			sd.putExtra("body", documents.get(info.position).getName());
			sd.putExtra("name", documents.get(info.position).getName());
			startActivity(sd);
			break;
		case R.id.del_doc: // delete document
			Toast.makeText(getBaseContext(), "Delete document: position=" + info.position, Toast.LENGTH_SHORT).show();
			break;
		case R.id.view_key: // view key
			Toast.makeText(getBaseContext(), "View security keys: position=" + info.position, Toast.LENGTH_SHORT).show();
			InputStream in = getResources().openRawResource(R.raw.ecypguch); //getAssets().open("debug.keystore");
			Log.i(TAG, "Load keystore-inputstream: " + in);
			UserToken.initialize(in);
			break;
		default:
			break;
		}
    	return true;
    }
    
    
    protected class BackgroundDocumentList extends AsyncTask<String, Void, String> {

    	private static final String TAG_WEB = "BackgroundDocumentList";
    	
    	private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
    	
    	
		@Override
		protected String doInBackground(String... params) {
			String result = null;
			HttpClient client = new DefaultHttpClient();
			HttpGet get = new HttpGet(params[0]);
			try {
				HttpResponse response = client.execute(get);
				HttpEntity entity = response.getEntity();
				result = EntityUtils.toString(entity);
				if (result != null) {
					Log.i(TAG_WEB, "\nGet response: " + result);
				}
			} catch (IOException e) {
				Log.w(TAG, "Cannot get entity-response '" + params[0] + "'", e);
			}
			return result;
		}
		
		
		@Override
		protected void onCancelled() {
			dialog.dismiss();
			Toast toast = Toast.makeText(getApplicationContext(), "Error connection to server", Toast.LENGTH_LONG);
			toast.setGravity(Gravity.TOP, 25, 400);
			toast.show();
		}
		
		
		@Override
		protected void onPreExecute() {
			dialog.setMessage("Получаю Ваши данные... Пожалуйста подождите...");
			dialog.show();
		}
		
		
		@Override
		protected void onPostExecute(String result) {
			dialog.dismiss();
			Log.i(TAG_WEB, "Thread finished. Display content as list view");
			parseDocuments(result);
		}
    }
}
