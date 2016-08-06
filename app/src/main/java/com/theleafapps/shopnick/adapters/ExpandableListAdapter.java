package com.theleafapps.shopnick.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.theleafapps.shopnick.R;
import com.theleafapps.shopnick.models.ExpandedMenuModel;
import com.theleafapps.shopnick.models.SubCategory;
import com.theleafapps.shopnick.ui.ProductListActivity;
import com.theleafapps.shopnick.utils.Commons;
import com.theleafapps.shopnick.utils.LinkedMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aviator on 22/07/16.
 */
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context mContext;
    private List<SubCategory> subCatList;
    private List<ExpandedMenuModel> mListDataHeader; // header titles

    // child data in format of header title, child title
    private HashMap<ExpandedMenuModel, List<String>> mListDataChild;
    ExpandableListView expandList;

    public ExpandableListAdapter(Context context, List<ExpandedMenuModel> listDataHeader, HashMap<ExpandedMenuModel, List<String>> listChildData, ExpandableListView mView) {
        this.mContext = context;
        this.mListDataHeader = listDataHeader;
        this.mListDataChild = listChildData;
        this.expandList = mView;
    }

    @Override
    public int getGroupCount() {
        int i = mListDataHeader.size();
        Log.d("GROUPCOUNT", String.valueOf(i));
        return this.mListDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int childCount = 0;
        childCount = this.mListDataChild.get(this.mListDataHeader.get(groupPosition)).size();
        return childCount;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.mListDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Log.d("CHILD", mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .get(childPosition).toString());
        return this.mListDataChild.get(this.mListDataHeader.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ExpandedMenuModel headerTitle = (ExpandedMenuModel) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.listheader, null);
        }
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.submenu);
//        ImageView headerIcon = (ImageView) convertView.findViewById(R.id.iconimage);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle.getIconName());
//        headerIcon.setImageResource(headerTitle.getIconImg());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        subCatList = Commons.catIdToSubCatMap.getValue(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_submenu, null);
        }

        TextView txtListChildSubCatId   =   (TextView) convertView.findViewById(R.id.nav_sub_cat_id_tv);
        TextView txtListChildCatId      =   (TextView) convertView.findViewById(R.id.nav_cat_id_tv);
        TextView txtListChild           =   (TextView) convertView.findViewById(R.id.submenu);

        txtListChildSubCatId.setText(String.valueOf(subCatList.get(childPosition).sub_category_id));
        txtListChildCatId.setText(String.valueOf(Commons.catIdToSubCatMap.getEntry(groupPosition).getKey()));
        txtListChild.setText(childText);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(mContext,"child clicked",Toast.LENGTH_LONG).show();
                String subCatIdStr  =   ((TextView)((LinearLayout) v).getChildAt(0)).getText().toString();
                String catIdStr     =   ((TextView)((LinearLayout) v).getChildAt(1)).getText().toString();
                int subCatId        =   Integer.parseInt(subCatIdStr);
                int catId           =   Integer.parseInt(catIdStr);

                Intent intent = new Intent(mContext, ProductListActivity.class);
                intent.putExtra("categoryId",catId);
                intent.putExtra("subCatId",subCatId);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}