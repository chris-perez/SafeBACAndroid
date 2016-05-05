package com.example.chris.safebacandroid;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Chris on 5/5/2016.
 */
public class FriendAdapter extends ArrayAdapter<FriendItem> {
  Context context;
  List<FriendItem> friendItemList;
  int layoutResID;

  public FriendAdapter(Context context, int resource, List<FriendItem> listItems) {
    super(context, resource, listItems);
    this.context = context;
    this.layoutResID = resource;
    this.friendItemList = listItems;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    final FriendItemHolder friendItemHolder;
    View view = convertView;
    if (view == null) {
      LayoutInflater inflater = ((Activity) context).getLayoutInflater();
      friendItemHolder = new FriendItemHolder();

      view = inflater.inflate(layoutResID, parent, false);
      friendItemHolder.name = (TextView) view.findViewById(R.id.name);
      friendItemHolder.bac = (TextView) view.findViewById(R.id.bac);
      friendItemHolder.bacVisibility = (Button) view.findViewById(R.id.bac_visibility);

      view.setTag(friendItemHolder);
    } else {
      friendItemHolder = (FriendItemHolder) view.getTag();
    }

    final FriendItem dItem = (FriendItem) this.friendItemList.get(position);
    friendItemHolder.name.setText(dItem.getName());
    if (dItem.getBac() > -1) {
      friendItemHolder.bac.setText(dItem.getBac()+"");
    } else {
      friendItemHolder.bac.setText("--");
    }

    if (dItem.getBacVisible()) {
      friendItemHolder.bacVisibility.setText("Make BAC invisible");
    } else {
      friendItemHolder.bacVisibility.setText("Make BAC visible");
    }

    friendItemHolder.bacVisibility.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dItem.setBacVisible(!dItem.getBacVisible());
        new SetVisibilityTask().execute(dItem);
        if (dItem.getBacVisible()) {
          friendItemHolder.bacVisibility.setText("Make BAC invisible");
        } else {
          friendItemHolder.bacVisibility.setText("Make BAC visible");
        }
      }
    });

    return view;
  }

  private static class FriendItemHolder {
    TextView name;
    TextView bac;
    Button bacVisibility;
  }

  public class SetVisibilityTask extends AsyncTask<FriendItem, Void, Void> {
    @Override
    protected Void doInBackground(FriendItem... params) {
      FriendItem friend = params[0];

      APICaller.setVisibility(friend.getId(), friend.getBacVisible());
      return null;
    }
  }
}
