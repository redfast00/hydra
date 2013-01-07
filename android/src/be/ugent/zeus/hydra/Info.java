package be.ugent.zeus.hydra;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.ui.info.InfoList;
import com.actionbarsherlock.app.SherlockListActivity;
import com.dd.plist.BinaryPropertyListParser;
import com.dd.plist.BinaryPropertyListWriter;
import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.NSString;
import com.dd.plist.XMLPropertyListParser;
import com.google.analytics.tracking.android.EasyTracker;

/**
 *
 * @author blackskad
 */
public class Info extends SherlockListActivity {

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    setTitle(R.string.title_info);

    NSArrayWrapper wrapper = (NSArrayWrapper) getIntent().getParcelableExtra("content");
    NSArray content;
    if (wrapper != null) {
      content = wrapper.array;
    } else {
      try {
        content= (NSArray) XMLPropertyListParser.parse(getResources().openRawResource(R.raw.info_content));
      } catch (Exception ex) {
        Log.e("[Hydra.Info]", "Failed to parse the info content!");
        ex.printStackTrace();
        this.finish();
        return;
      }
    }
    setListAdapter(new InfoList(this, content));
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);

    // Get the item that was clicked
    NSDictionary item = (NSDictionary) getListAdapter().getItem(position);

    NSObject action;
    if ((action = item.objectForKey("subcontent")) != null) {
      NSArrayWrapper wrapper = new NSArrayWrapper((NSArray) action);

      Intent intent = new Intent(this, Info.class);
      intent.putExtra("content", wrapper);
      startActivity(intent);
    } else if ((action = item.objectForKey("url")) != null) {
      startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(((NSString) action).toString())));
    } else if ((action = item.objectForKey("html")) != null) {
      Intent intent = new Intent(this, InfoWebActivity.class);
      intent.putExtra("page", ((NSString) action).toString());
      startActivity(intent);
    } else if ((action = item.objectForKey("association")) != null) {
      Toast.makeText(this, "TODO: implement the association for " + action.toString(), Toast.LENGTH_SHORT).show();
      System.err.println(action);
    } else {
      System.err.println("WHAT THE FUCK IS THIS SHIT?");
    }
  }
  
  private static class NSArrayWrapper implements Parcelable {
    private NSArray array;

    public NSArrayWrapper (NSArray array) {
      this.array = array;
    }
    
    private NSArrayWrapper (Parcel in) {
      try {
        int size = in.readInt();
        byte[] data = new byte[size];
        in.readByteArray(data);
        array = (NSArray) BinaryPropertyListParser.parse(data);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    public int describeContents() {
      return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
      try {
        byte[] data = BinaryPropertyListWriter.writeToArray(array);
        dest.writeInt(data.length);
        dest.writeByteArray(data);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
        // this is used to regenerate your object. All Parcelables must have a CREATOR that implements these two methods
    public static final Parcelable.Creator<NSArrayWrapper> CREATOR = new Parcelable.Creator<NSArrayWrapper>() {
        public NSArrayWrapper createFromParcel(Parcel in) {
            return new NSArrayWrapper(in);
        }

        public NSArrayWrapper[] newArray(int size) {
            return new NSArrayWrapper[size];
        }
    };

  }  

  @Override
  public void onStart() {
    super.onStart();
    EasyTracker.getInstance().activityStart(this); // Add this method.
  }

  @Override
  public void onStop() {
    super.onStop();
    EasyTracker.getInstance().activityStop(this); // Add this method.
  }
}