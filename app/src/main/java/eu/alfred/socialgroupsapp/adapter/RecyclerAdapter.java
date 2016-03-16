package eu.alfred.socialgroupsapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import eu.alfred.socialgroupsapp.R;
import eu.alfred.socialgroupsapp.model.Group;

/**
 * Created by deniz.coskun on 16.03.16.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.GroupsViewHolder> {

    private List<Group> groups;
    private LayoutInflater mInflater;

    public RecyclerAdapter(Context context, List<Group> groups) {
        this.groups = groups;
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
        Group group = groups.get(position);
        holder.setData(group, position);
    }

    @Override
    public int getItemCount() { return groups.size(); }


    class GroupsViewHolder extends RecyclerView.ViewHolder {

        TextView groupNameTextView, groupMembersCountTextView, groupDescriptionTextView;
        Group group;

        public GroupsViewHolder(View itemView) {
            super(itemView);
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
        }

    }
}
