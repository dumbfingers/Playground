package com.yeyaxi.android.playground.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yeyaxi.android.playground.R;
import com.yeyaxi.android.playground.adapter.CommentsAdapter;
import com.yeyaxi.android.playground.api.ApiClient;
import com.yeyaxi.android.playground.constant.Params;
import com.yeyaxi.android.playground.model.Comment;
import com.yeyaxi.android.playground.model.Post;
import com.yeyaxi.android.playground.model.User;
import com.yeyaxi.android.playground.util.AvatarUriUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DetailFragment extends Fragment {

    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.comments_count)
    TextView commentsCount;
    @BindView(R.id.body)
    TextView body;
    Unbinder unbinder;
    @BindView(R.id.backdrop)
    ImageView backdrop;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.icon_comment)
    ImageView iconComment;

    private ApiClient apiClient;
    private CommentsAdapter adapter;
    private List<Comment> comments = new ArrayList<>();

    public static DetailFragment newInstance(Bundle extra) {
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(extra);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_post_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        this.apiClient = new ApiClient();

        this.adapter = new CommentsAdapter();

        setupLayoutManager();
        this.recyclerView.setAdapter(this.adapter);

        if (args != null) {
            Long postId = args.getLong(Params.PARAM_POST_ID);
            Long userId = args.getLong(Params.PARAM_USER_ID);
            refreshContent(postId, userId);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).setSupportActionBar(this.toolbar);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        this.toolbar.setNavigationOnClickListener(v -> getFragmentManager().popBackStack());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void refreshContent(Long postId, Long userId) {
        Observable<List<Comment>> commentObservable = this.apiClient.getApiInterface().getComments(postId);
        Observable<User> userObservable = this.apiClient.getApiInterface().getUser(userId);
        Observable<Post> postObservable = this.apiClient.getApiInterface().getPost(postId);

        Observable zipped = Observable.zip(commentObservable, userObservable, postObservable, (comments, user, post) -> {
            post.setUser(user);
            post.setComments(comments);
            return post;
        });

        zipped.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        post -> this.fillView((Post) post),
                        throwable -> showError()
                );
    }

    private void fillView(Post post) {
        if (!isResumed() || post == null) {
            return;
        }

        this.collapsingToolbar.setTitle(post.getTitle());
        this.body.setText(post.getBody());

        User user = post.getUser();
        if (user != null) {
            this.userName.setText(user.getName());
            String uri = AvatarUriUtil.getAvatarUri(post.getUser().getEmail());
            Picasso.with(getContext()).load(uri).into(this.imageView);
            Picasso.with(getContext()).load(uri).into(this.backdrop);
        }

        List<Comment> comments = post.getComments();
        if (comments != null) {
            this.iconComment.setVisibility(View.VISIBLE);
            this.comments = new ArrayList<>(comments);
            this.adapter.setDataSource(this.comments);
            this.adapter.notifyDataSetChanged();
            this.commentsCount.setText(String.format(getResources().getQuantityString(R.plurals.plural_comment, comments.size()), comments.size()));
        }
    }

    private void setupLayoutManager() {
        LinearLayoutManager llm = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        this.recyclerView.setLayoutManager(llm);
    }

    private void showError() {
        if (!isResumed()) {
            return;
        }
        Toast.makeText(getContext(), R.string.error, Toast.LENGTH_LONG).show();
    }
}

