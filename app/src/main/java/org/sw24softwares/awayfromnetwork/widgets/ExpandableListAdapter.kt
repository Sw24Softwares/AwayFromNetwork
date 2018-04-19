package org.sw24softwares.awayfromnetwork

import java.util.HashMap
import kotlin.collections.List

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView

class ExpandableListAdapter (context : Context, listDataHeader : List<String>, listChildData : HashMap<String,List<String>>) : BaseExpandableListAdapter()  {
        private var mContext = context
        private var mListDataHeader = listDataHeader
        private var  mListDataChild = listChildData
         
        override fun getChild(groupPosition : Int, childPosititon : Int) : Any {
                return this.mListDataChild.get(mListDataHeader.get(groupPosition))?.get(childPosititon) as Any
        }
 
        override fun getChildId(groupPosition : Int, childPosition : Int) : Long {
                return childPosition.toLong()
        }
 
        override fun getChildView(groupPosition : Int, childPosition : Int, isLastChild : Boolean, _convertView : View?, parent : ViewGroup) : View {
                var convertView = _convertView
                val childText = getChild(groupPosition, childPosition) as String
                if (convertView == null) {
                        var infalInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                        convertView = infalInflater.inflate(R.layout.expandable_list_item, null)
                }
                val  txtListChild = convertView?.findViewById(R.id.expandable_item_text) as TextView
                txtListChild.setText(childText)
                return convertView
        }
 
        override fun getChildrenCount(groupPosition : Int) : Int {
                return mListDataChild.get(mListDataHeader.get(groupPosition))?.size as Int
        }
 
        override fun getGroup(groupPosition : Int) : Any {
                return mListDataHeader.get(groupPosition) 
        }
 
        override fun getGroupCount() : Int {
                return mListDataHeader.size
        }
        
        override fun getGroupId(groupPosition : Int) : Long {
                return groupPosition.toLong()
        }
 
        override fun getGroupView(groupPosition : Int, isExpanded : Boolean, _convertView : View?, parent : ViewGroup) : View {
                var convertView = _convertView
                val  headerTitle = getGroup(groupPosition) as String
                if (convertView == null) {
                        var infalInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater 
                        convertView = infalInflater.inflate(R.layout.expandable_list_group, null)
                }
                val listHeader = convertView?.findViewById(R.id.expandable_group_text) as TextView
                listHeader.setTypeface(null, Typeface.BOLD)
                listHeader.setText(headerTitle)
                return convertView
        }
 
        override fun hasStableIds() : Boolean {
                return false
        }
 
        override fun isChildSelectable(groupPosition : Int, childPosition : Int) : Boolean {
                return true
        }
}
