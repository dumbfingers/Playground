package com.yeyaxi.android.playground.adapter;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yeyaxi.android.playground.R;
import com.yeyaxi.android.playground.model.Comment;
import com.yeyaxi.android.playground.util.AvatarUriUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentsViewHolder> {

    private List<Comment> dataSource;

    @Override
    public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment, parent, false);
        return new CommentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentsViewHolder holder, int position) {
        if (this.dataSource == null) {
            return;
        }
        Comment comment = this.dataSource.get(position);
        if (comment == null) {
            return;
        }
        holder.commentBody.setText(comment.getBody());
        holder.userName.setText(comment.getEmail());
        String uri = AvatarUriUtil.getAvatarUri(comment.getEmail());
        Picasso.with(holder.itemView.getContext()).load(uri).into(holder.avatarView);
    }

    @Override
    public int getItemCount() {
        return this.dataSource == null ? 0 : this.dataSource.size();
    }

    public void setDataSource(List<Comment> dataSource) {
        this.dataSource = dataSource;
    }

    class CommentsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar_view)
        AppCompatImageView avatarView;
        @BindView(R.id.user_name)
        TextView userName;
        @BindView(R.id.comment_body)
        TextView commentBody;

        public CommentsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
