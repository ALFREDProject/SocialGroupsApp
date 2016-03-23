package eu.alfred.socialgroupsapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import eu.alfred.socialgroupsapp.GroupDetailsActivity;
import eu.alfred.socialgroupsapp.R;
import eu.alfred.socialgroupsapp.model.Group;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.GroupsViewHolder> {

    private LinkedHashMap<String, Group> groups;
    private LayoutInflater mInflater;
    private Context context;

    public RecyclerAdapter(Context context, LinkedHashMap<String, Group> groups) {
        this.groups = groups;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerAdapter.GroupsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.group_list_item, parent, false);
        GroupsViewHolder holder = new GroupsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.GroupsViewHolder holder, int position) {
        Group group = (new ArrayList<Group>(groups.values())).get(position);
        //Group group = groups.get(position);
        holder.setData(group, position);
        //holder.setListeners();
    }

    @Override
    public int getItemCount() { return groups.size(); }

    class GroupsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView groupNameTextView, groupMembersCountTextView, groupDescriptionTextView;
        Group group;

        public GroupsViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            groupNameTextView = (TextView) itemView.findViewById(R.id.groupNameTextView);
            groupMembersCountTextView = (TextView) itemView.findViewById(R.id.groupMembersCountTextView);
            groupDescriptionTextView = (TextView) itemView.findViewById(R.id.groupDescriptionTextView);
        }

        public void setData(Group group, int position) {
            this.groupNameTextView.setText(group.getName());
            if(group.getMemberIds().length < 1) { this.groupMembersCountTextView.setText("No members found!"); }
            else { this.groupMembersCountTextView.setText("Number of Members: " + group.getMemberIds().length); }
            if(group.getDescription() == null) { this.groupDescriptionTextView.setText("No description found for this group"); }
            else { this.groupDescriptionTextView.setText(group.getDescription()); }
            this.group = group;
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            String groupId = (new ArrayList<String>(groups.keySet())).get(position);
            Log.d("Click", groupId);

            Intent groupDetailsIntent = new Intent(context, GroupDetailsActivity.class);
            groupDetailsIntent.putExtra("GroupID", groupId);
            context.startActivity(groupDetailsIntent);
        }
    }


}
